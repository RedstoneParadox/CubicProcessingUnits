package io.github.redstoneparadox.cpu.computer

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.builder.RequiredArgumentBuilder
import io.github.redstoneparadox.cpu.api.Peripheral
import io.github.redstoneparadox.cpu.api.PeripheralBlockEntity
import io.github.redstoneparadox.cpu.api.PeripheralHandle
import io.github.redstoneparadox.cpu.scripting.File
import io.github.redstoneparadox.cpu.scripting.Folder
import io.github.redstoneparadox.cpu.util.SynchronizedBox
import jdk.nashorn.api.scripting.NashornScriptEngineFactory
import kotlinx.coroutines.*
import net.minecraft.nbt.CompoundTag
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import java.util.*
import java.util.function.Consumer
import java.util.function.Function
import java.util.function.Supplier
import javax.script.Bindings
import javax.script.ScriptContext
import javax.script.ScriptEngine
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class Computer(private val world: ServerWorld, var cores: Int, private val posSupplier: () -> BlockPos) {
    private val jobQueue: Queue<Pair<Job, Continuation<ScriptEngine>>> = LinkedList<Pair<Job, Continuation<ScriptEngine>>>()
    private val runningJobs: MutableList<Job> = mutableListOf()
    private var rootDirectory: Folder = Folder.createRootDirectory()
    private var currentFolder = rootDirectory

    private val peripherals: MutableMap<PeripheralHandle, Peripheral<*>> = mutableMapOf()
    private val handles: MutableMap<String, PeripheralHandle> = mutableMapOf()

    private var checked: Boolean = false

    private val commandDispatcher = CommandDispatcher<Computer>()
    private var openScript: File<*>? = null

    init {
        // Returns 2 to open the script.
        val createFile = LiteralArgumentBuilder
            .literal<Computer>("file")
            .then(
                RequiredArgumentBuilder
                    .argument<Computer, String>("name", StringArgumentType.word())
                    .executes {
                        val file = currentFolder.getFile(it.getArgument("name", String::class.java))
                        if (file.extension == "js") {
                            openScript = file
                            return@executes 2;
                        }
                        return@executes 1
                    }
                    .build()
            )
            .build()

        val openFolder = LiteralArgumentBuilder
            .literal<Computer>("folder")
            .then(
                RequiredArgumentBuilder
                    .argument<Computer, String>("name", StringArgumentType.word())
                    .executes {
                        currentFolder = currentFolder.openSubfolder(it.getArgument("name", String::class.java));
                        return@executes 1
                    }
                    .build()
            )
            .build()

        val parentFolder = LiteralArgumentBuilder
            .literal<Computer>("parent")
            .executes {
                if (currentFolder.hasParent()) {
                    currentFolder = currentFolder.openParent()
                    return@executes 1
                }
                return@executes 0
            }
            .build()

        commandDispatcher.root.addChild(createFile)
        commandDispatcher.root.addChild(openFolder)
        commandDispatcher.root.addChild(parentFolder)
    }

    fun executeCommand(command: String): Int {
        return try {
            commandDispatcher.execute(command, this)
        } catch (e: Exception) {
            e.printStackTrace()
            0
        }
    }

    private fun updateConnections() {
        val keys = mutableListOf<String>()
        for (entry in handles) {
            if (entry.value.isClosed) {
                peripherals.remove(entry.value)?.disconnect()
                keys.add(entry.key)
            }
        }
        keys.forEach { handles.remove(it) }

        for (direction in Direction.values()) {
            val neighborPos = posSupplier().offset(direction)
            val neighborBe = world.getBlockEntity(neighborPos)
            if (neighborBe is PeripheralBlockEntity && !neighborBe.isConnected) {
                val handle = PeripheralHandle(this)
                val peripheral = neighborBe.getPeripheral(handle)
                connect(handle, peripheral, neighborBe.defaultName)
            }
        }
    }

    @Synchronized
    fun run(script: String) {
        var job: Job? = null

        @Synchronized
        fun queueContinuation(continuation: Continuation<ScriptEngine>) {
            job?.let { jobQueue.offer(Pair(it, continuation)) }
        }

        job = GlobalScope.launch {
            val engine: ScriptEngine = suspendCoroutine { queueContinuation(it) }

            try {
                engine.eval(script)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun connect(handle: PeripheralHandle, peripheral: Peripheral<*>, name: String) {
        peripherals[handle] = peripheral
        var trueName = name
        var count = 1
        while (handles.containsKey(trueName)) {
            count += 1
            trueName = "$name$count"
        }
        handles[trueName] = handle
    }

    fun disconnect(handle: PeripheralHandle) {
        if (peripherals.containsKey(handle)) peripherals.remove(handle)
        var key = ""
        for (entry in handles) {
            if (entry.value == handle) {
                key = entry.key
                entry.value.disconnect()
                break
            }
        }
        handles.remove(key)
    }

    fun tick() {
        updateConnections()
        runningJobs.removeIf { it.isCompleted || it.isCancelled }
        while (runningJobs.size < cores) {
            if (jobQueue.isEmpty()) break
            val pair = jobQueue.remove()
            pair.second.resume(createNewEngine())
            runningJobs.add(pair.first)
        }
    }

    fun isDirty(): Boolean {
        return !checked && rootDirectory.isDirty()
    }

    fun markChecked() {
        checked = true
    }

    fun markClean() {
        checked = false
        rootDirectory.markClean()
    }

    fun shutDown() {
        jobQueue.clear()
        runningJobs.forEach { it.cancel() }
        runningJobs.clear()
        peripherals.values.forEach { it.disconnect() }
        handles.values.forEach { it.disconnect() }
        peripherals.clear()
        handles.clear()
    }

    private fun createNewEngine(): ScriptEngine {
        val initialized = SynchronizedBox(false)
        val filter = { _: String -> !initialized.get()}
        val engine = NashornScriptEngineFactory().getScriptEngine(filter)
        val bindings = fillBindings(engine.createBindings())
        initialized.set(true)

        engine.setBindings(bindings, ScriptContext.ENGINE_SCOPE)

        return engine
    }

    private fun fillBindings(bindings: Bindings): Bindings {
        bindings["delay"] = Consumer { ms: Long -> runBlocking { delay(ms) } }
        bindings["getPeripheral"] = Function { name: String -> getPeripheral(name) }
        bindings["openFileSystem"] = Supplier { rootDirectory }

        return bindings
    }

    @Synchronized
    private fun getPeripheral(name: String): Peripheral<*>? {
        val handle = handles[name]
        if (handle != null) {
            val peripheral = peripherals[handle]
            if (peripheral != null) return peripheral
            else handles.remove(name)
        }
        return null
    }

    fun fromNBT(tag: CompoundTag) {
        if (tag.contains("filesystem")) rootDirectory = Folder.fromNBT(tag.getCompound("filesystem"))
    }

    fun toNBT(tag: CompoundTag) {
        tag.put("filesystem", rootDirectory.toNBT())
    }
}