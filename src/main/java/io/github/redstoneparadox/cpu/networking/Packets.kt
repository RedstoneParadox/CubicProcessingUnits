package io.github.redstoneparadox.cpu.networking

import io.github.redstoneparadox.cpu.id
import io.github.redstoneparadox.cpu.misc.CpuContainer
import io.netty.buffer.Unpooled
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry
import net.fabricmc.fabric.api.server.PlayerStream
import net.minecraft.client.network.packet.CustomPayloadS2CPacket
import net.minecraft.network.Packet
import net.minecraft.server.network.ServerPlayNetworkHandler
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.server.network.packet.CustomPayloadC2SPacket
import net.minecraft.util.Identifier
import net.minecraft.util.PacketByteBuf
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

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

    fun soundPacket(world: World, pos: BlockPos, id: Identifier, f: Float, g: Float, b: Boolean) {
        val function = {
            val bytes = PacketByteBuf(Unpooled.buffer())
            bytes.writeBlockPos(pos)
            bytes.writeIdentifier(id)
            bytes.writeFloat(f)
            bytes.writeFloat(g)
            CustomPayloadS2CPacket("cpu:speaker".id(), bytes)
        }
        dispatchToAll(world, pos, function)
    }

    private fun dispatchToAll(world: World, pos: BlockPos, function: () -> Packet<*>) {
        PlayerStream.watching(world, pos)
            .map<ServerPlayerEntity> { it as ServerPlayerEntity}
            .map<ServerPlayNetworkHandler> { it.networkHandler }
            .forEach { it.sendPacket(function()) }
    }
}