package io.github.redstoneparadox.cpu.client.networking

import io.github.redstoneparadox.cpu.id
import io.netty.buffer.Unpooled
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry
import net.minecraft.network.Packet
import net.minecraft.server.network.packet.CustomPayloadC2SPacket
import net.minecraft.util.Identifier
import net.minecraft.util.PacketByteBuf

object ClientPackets {

    fun saveScriptPacket(syncId: Int, script: String) {
        val bytes = PacketByteBuf(Unpooled.buffer())
        bytes.writeInt(syncId)
        bytes.writeString(script)
        dispatch(CustomPayloadC2SPacket("cpu:save".id(), bytes))
    }

    fun runScriptPacket(syncId: Int) {
        val bytes = PacketByteBuf(Unpooled.buffer())
        bytes.writeInt(syncId)
        dispatch(CustomPayloadC2SPacket("cpu:run".id(), bytes))
    }

    private fun dispatch(packet: Packet<*>) {
        ClientSidePacketRegistry.INSTANCE.sendToServer(packet)
    }
}