package io.github.redstoneparadox.cpu.client.networking

import io.github.redstoneparadox.cpu.id
import io.netty.buffer.Unpooled
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry
import net.minecraft.network.Packet
import net.minecraft.network.packet.c2s.play.CustomPayloadC2SPacket
import net.minecraft.particle.ParticleTypes
import net.minecraft.sound.SoundCategory
import net.minecraft.util.Identifier
import net.minecraft.util.PacketByteBuf
import net.minecraft.util.math.BlockPos
import net.minecraft.util.registry.Registry
import net.minecraft.world.World
import kotlin.math.ln

object ClientPackets {

    fun registerPackets() {
        ClientSidePacketRegistry.INSTANCE.register("cpu:speaker".id()) { context, buf ->
            val world = context.player.world
            val pos = buf.readBlockPos()
            val id = buf.readIdentifier()
            val volume = buf.readFloat()
            val pitch = buf.readFloat()
            playSpeakerSound(world, pos, id, volume, pitch, true)
        }
    }

    private fun playSpeakerSound(world: World, pos: BlockPos, id: Identifier, volume: Float, pitch: Float, b: Boolean) {
        val event = Registry.SOUND_EVENT.get(id)
        world.playSound(pos.x.toDouble(), pos.y.toDouble(), pos.z.toDouble(), event, SoundCategory.BLOCKS, volume, pitch, b)
        val i = (2*ln(pitch) - 12*ln(2.0))/ln(2.0)
        world.addParticle(ParticleTypes.NOTE, pos.x.toDouble() + 0.5, pos.y.toDouble() + 1.2, pos.z.toDouble() + 0.5, i / 24.0, 0.0, 0.0)
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