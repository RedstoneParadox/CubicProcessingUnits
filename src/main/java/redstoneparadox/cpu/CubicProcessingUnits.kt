package redstoneparadox.cpu

import net.fabricmc.fabric.api.container.ContainerFactory
import net.fabricmc.fabric.api.container.ContainerProviderRegistry
import net.minecraft.util.Identifier
import redstoneparadox.cpu.block.CpuBlocks
import redstoneparadox.cpu.block.entity.CpuBlockEntityTypes

@SuppressWarnings("unused")
fun init() {
    CpuBlockEntityTypes.register()
    CpuBlocks.register()

    ContainerProviderRegistry.INSTANCE.registerFactory("cpu:cpu".id(), ContainerFactory { syncID, id, player, buf ->  null})
}

fun String.id(): Identifier {
    return Identifier(this)
}