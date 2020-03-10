package io.github.redstoneparadox.cpu.networking

import io.github.redstoneparadox.cpu.id
import io.github.redstoneparadox.cpu.misc.CpuContainer
import io.github.redstoneparadox.cpu.misc.PrinterContainer
import io.netty.buffer.Unpooled
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry
import net.fabricmc.fabric.api.server.PlayerStream
import net.minecraft.container.Container
import net.minecraft.network.Packet
import net.minecraft.network.packet.s2c.play.CustomPayloadS2CPacket
import net.minecraft.server.network.ServerPlayNetworkHandler
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.Identifier
import net.minecraft.util.PacketByteBuf
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

object Packets {

    private val cpuListeners: MutableMap<Int, CpuContainer> = mutableMapOf()
    private val printerListensers: MutableMap<Int, PrinterContainer> = mutableMapOf()

    fun registerPackets() {
        ServerSidePacketRegistry.INSTANCE.register("cpu:save".id()) { context, buf ->
            saveScript(buf.readInt(), buf.readString())
        }
        ServerSidePacketRegistry.INSTANCE.register("cpu:run".id()) { context, buf ->
            runScript(buf.readInt())
        }
    }

    fun listen(container: Container) {
        if (container is CpuContainer) cpuListeners[container.syncId] = container
        else if(container is PrinterContainer) printerListensers[container.syncId] = container
    }

    private fun saveScript(syncId: Int, script: String) {
        cpuListeners[syncId]?.saveRemote(script)
    }

    private fun runScript(syncId: Int) {
        cpuListeners[syncId]?.runRemote()
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