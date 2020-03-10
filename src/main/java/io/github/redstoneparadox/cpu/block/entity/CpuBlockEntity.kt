package io.github.redstoneparadox.cpu.block.entity

import io.github.redstoneparadox.cpu.api.Peripheral
import io.github.redstoneparadox.cpu.api.PeripheralHandle
import io.github.redstoneparadox.cpu.util.SynchronizedBox
import jdk.nashorn.api.scripting.NashornScriptEngineFactory
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable
import net.minecraft.block.entity.BlockEntity
import net.minecraft.nbt.CompoundTag
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.Tickable
import java.util.*
import java.util.function.Consumer
import java.util.function.Function
import javax.script.Bindings
import javax.script.ScriptContext
import javax.script.ScriptEngine

class CpuBlockEntity : BlockEntity(CpuBlockEntityTypes.CPU), Tickable, BlockEntityClientSerializable {
    private var booted: Boolean = false
    private var script: String = ""

    private val engines: Stack<ScriptEngine> = Stack()
    private val jobs: MutableList<Job> = mutableListOf()
    private var cores: Int = 1

    private val peripherals: MutableMap<PeripheralHandle, Peripheral<*>> = mutableMapOf()
    private val handles: MutableMap<String, PeripheralHandle> = mutableMapOf()

    private fun boot() {
        if (world is ServerWorld) {
            engines.push(createNewEngine())
            engines.push(createNewEngine())
            engines.push(createNewEngine())
            engines.push(createNewEngine())
            engines.push(createNewEngine())
        }
        booted = true
    }

    fun connect(handle: PeripheralHandle, peripheral: Peripheral<*>, name: String) {
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
    }

    fun run() {
        val world = world
        if (world == null || world.isClient) return

        val job = GlobalScope.launch {
            val engine = createNewEngine()

            try {
                engine.eval(script)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        jobs.add(job)
        return
    }

    @Synchronized
    private fun requestEngine(): ScriptEngine? {
        if (engines.isEmpty()) return null
        return engines.pop()
    }

    @Synchronized
    private fun returnEngine(engine: ScriptEngine) {
        if (engines.size < cores) {
            val bindings = fillBindings(engine.createBindings())
            engine.setBindings(bindings, ScriptContext.ENGINE_SCOPE)
            engines.push(engine)
        }
    }

    @Synchronized
    private fun createNewEngine(): ScriptEngine {
        val initialized = SynchronizedBox(false)
        val filter = { _: String -> !initialized.get()}
        val engine = NashornScriptEngineFactory().getScriptEngine(filter)
        val bindings = fillBindings(engine.createBindings())
        initialized.set(true)

        engine.setBindings(bindings, ScriptContext.ENGINE_SCOPE)

        return engine
    }

    @Synchronized
    private fun fillBindings(bindings: Bindings): Bindings {
        bindings["delay"] = Consumer { ms: Long -> Thread.sleep(ms) }
        bindings["getPeripheral"] = Function { name: String -> getPeripheral(name)}

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

    fun save(script: String) {
        this.script = script
        markDirty()
    }

    fun load(): String {
        return script
    }

    override fun fromTag(tag: CompoundTag) {
        if (tag.contains("script")) script = tag.getString("script")
        super.fromTag(tag)
    }

    override fun toTag(tag: CompoundTag): CompoundTag {
        tag.putString("script", script)
        return super.toTag(tag)
    }

    override fun tick() {
        if (!booted) boot()
        val remaining = jobs.filter { it.isActive }
        jobs.clear()
        jobs.addAll(remaining)
    }

    override fun toClientTag(tag: CompoundTag): CompoundTag {
        tag.putString("script", script)
        return tag
    }

    override fun fromClientTag(tag: CompoundTag) {
        if (tag.contains("script")) script = tag.getString("script")
    }
}