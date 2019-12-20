package io.github.redstoneparadox.cpu.networking

import io.github.redstoneparadox.cpu.id
import io.github.redstoneparadox.cpu.misc.CpuContainer
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry

object Packets {

    private val listeners: MutableMap<Int, CpuContainer> = mutableMapOf()

    fun registerPackets() {
        ServerSidePacketRegistry.INSTANCE.register("cpu:save".id()) { context, buf ->
            saveScript(buf.readInt(), buf.readString())
        }
        ServerSidePacketRegistry.INSTANCE.register("cpu:run".id()) { context, buf ->
            runScript(buf.readInt())
        }
    }

    fun listen(container: CpuContainer) {
        listeners[container.syncId] = container
    }

    private fun saveScript(syncId: Int, script: String) {
        listeners[syncId]?.saveRemote(script)
    }

    private fun runScript(syncId: Int) {
        listeners[syncId]?.runRemote()
    }
}