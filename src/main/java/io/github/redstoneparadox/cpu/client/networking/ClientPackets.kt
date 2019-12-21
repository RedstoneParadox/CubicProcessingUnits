package io.github.redstoneparadox.cpu.client.networking

import io.github.redstoneparadox.cpu.id
import io.netty.buffer.Unpooled
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry
import net.minecraft.network.Packet
import net.minecraft.server.network.packet.CustomPayloadC2SPacket
import net.minecraft.sound.SoundCategory
import net.minecraft.util.Identifier
import net.minecraft.util.PacketByteBuf
import net.minecraft.util.math.BlockPos
import net.minecraft.util.registry.Registry
import net.minecraft.world.World

object ClientPackets {

    fun registerPackets() {
        ClientSidePacketRegistry.INSTANCE.register("cpu:speaker".id()) { context, buf ->
            val world = context.player.world
            val pos = buf.readBlockPos()
            val id = buf.readIdentifier()
            val f = buf.readFloat()
            val g = buf.readFloat()
            playSound(world, pos, id, f, g, true)
        }
    }

    private fun playSound(world: World, pos: BlockPos, id: Identifier, f: Float, g: Float, b: Boolean) {
        val event = Registry.SOUND_EVENT.get(id)
        world.playSound(pos.x.toDouble(), pos.y.toDouble(), pos.z.toDouble(), event, SoundCategory.BLOCKS, f, g, b)
    }

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