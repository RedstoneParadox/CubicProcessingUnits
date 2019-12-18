package io.github.redstoneparadox.cpu.block.entity

import io.github.redstoneparadox.cpu.api.Peripheral
import io.github.redstoneparadox.cpu.misc.SynchronizedBox
import jdk.nashorn.api.scripting.NashornScriptEngineFactory
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import net.minecraft.block.entity.BlockEntity
import net.minecraft.nbt.CompoundTag
import net.minecraft.util.Tickable
import java.util.*
import java.util.function.Consumer
import javax.script.Bindings
import javax.script.ScriptContext
import javax.script.ScriptEngine

class CpuBlockEntity : BlockEntity(CpuBlockEntityTypes.CPU), Tickable {
    private var script: String = ""

    private val engines: Stack<ScriptEngine> = Stack()
    private val jobs: MutableList<Job> = mutableListOf()
    private var cores: Int = 1

    private val peripherals: MutableMap<Any, Peripheral<*>> = mutableMapOf()

    init {
        engines.push(createNewEngine())
    }

    fun connect(peripheral: Peripheral<*>): Any {
        val key = Any()
        peripherals[key] = peripheral
        return key
    }

    fun disconnect(key: Any) {
        if (peripherals.containsKey(key)) peripherals.remove(key)
    }

    fun run() = runBlocking {
        val world = world
        if (world == null || world.isClient) return@runBlocking

        val job = GlobalScope.launch {
            var engine: ScriptEngine? = null
            while (engine == null) {
                engine = requestEngine()
            }

            try {
                engine.eval(script)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            returnEngine(engine)
        }
        jobs.add(job)
        return@runBlocking
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
            println(bindings.entries)
            engines.push(engine)
        }
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

    @Synchronized
    private fun fillBindings(bindings: Bindings): Bindings {
        return bindings
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
        val remaining = jobs.filter { it.isActive }
        jobs.clear()
        jobs.addAll(remaining)
    }
}