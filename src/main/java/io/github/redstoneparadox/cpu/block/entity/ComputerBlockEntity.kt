package io.github.redstoneparadox.cpu.block.entity

import io.github.redstoneparadox.cpu.api.Peripheral
import io.github.redstoneparadox.cpu.api.PeripheralBlockEntity
import io.github.redstoneparadox.cpu.api.PeripheralHandle
import io.github.redstoneparadox.cpu.computer.Computer
import io.github.redstoneparadox.cpu.scripting.Folder
import io.github.redstoneparadox.cpu.util.SynchronizedBox
import jdk.nashorn.api.scripting.NashornScriptEngineFactory
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable
import net.minecraft.block.entity.BlockEntity
import net.minecraft.nbt.CompoundTag
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.Tickable
import net.minecraft.util.math.Direction
import java.io.*
import java.util.function.Consumer
import java.util.function.Function
import java.util.function.Supplier
import javax.script.Bindings
import javax.script.ScriptContext
import javax.script.ScriptEngine
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class ComputerBlockEntity : BlockEntity(CpuBlockEntityTypes.COMPUTER), Tickable, BlockEntityClientSerializable {
    private var computer: Computer? = null
    private var script: String = ""

    fun getComputer(): Computer? {
        return computer
    }

    fun onRemove() {
        computer?.shutDown()
    }

    fun connect(handle: PeripheralHandle, peripheral: Peripheral<*>, name: String) {
        computer?.connect(handle, peripheral, name)
    }

    fun disconnect(handle: PeripheralHandle) {
        computer?.disconnect(handle)
    }

    fun run() {
        computer?.run(script)
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
        computer?.fromNBT(tag)
        super.fromTag(tag)
    }

    override fun toTag(tag: CompoundTag): CompoundTag {
        tag.putString("script", script)
        computer?.toNBT(tag)
        computer?.markClean()
        return super.toTag(tag)
    }

    override fun tick() {
        val world = world
        if (computer == null && world is ServerWorld) {
            computer = Computer(world, 4) { pos }
            computer?.connectToPeripherals()
        }
        computer?.tick()
        if (computer?.isDirty() == true) {
            computer?.markChecked()
            markDirty()
        }
    }

    override fun toClientTag(tag: CompoundTag): CompoundTag {
        tag.putString("script", script)
        return tag
    }

    override fun fromClientTag(tag: CompoundTag) {
        if (tag.contains("script")) script = tag.getString("script")
    }
}