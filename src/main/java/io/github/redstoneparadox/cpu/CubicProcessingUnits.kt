package io.github.redstoneparadox.cpu

import net.fabricmc.fabric.api.container.ContainerFactory
import net.fabricmc.fabric.api.container.ContainerProviderRegistry
import net.minecraft.util.Identifier
import io.github.redstoneparadox.cpu.block.CpuBlocks
import io.github.redstoneparadox.cpu.block.entity.CpuBlockEntityTypes
import io.github.redstoneparadox.cpu.misc.CpuContainer
import io.github.redstoneparadox.cpu.networking.Packets

@SuppressWarnings("unused")
fun init() {
    CpuBlockEntityTypes.register()
    CpuBlocks.register()

    ContainerProviderRegistry.INSTANCE.registerFactory("cpu:cpu".id(), ContainerFactory { syncID, id, player, buf ->
        CpuContainer(player.world, buf.readBlockPos(), syncID)
    })
    Packets.registerPackets()
}

fun String.id(): Identifier {
    return Identifier(this)
}