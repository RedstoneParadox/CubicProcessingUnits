package io.github.redstoneparadox.cpu

import net.fabricmc.fabric.api.container.ContainerFactory
import net.fabricmc.fabric.api.container.ContainerProviderRegistry
import net.minecraft.util.Identifier
import io.github.redstoneparadox.cpu.block.CpuBlocks
import io.github.redstoneparadox.cpu.block.entity.CpuBlockEntityTypes
import io.github.redstoneparadox.cpu.item.CpuItems
import io.github.redstoneparadox.cpu.misc.CpuContainer
import io.github.redstoneparadox.cpu.networking.Packets
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder
import net.minecraft.item.Item
import net.minecraft.item.ItemStack

@SuppressWarnings("unused")
fun init() {
    CpuBlockEntityTypes.register()
    CpuBlocks.register()
    CpuItems.register()

    ContainerProviderRegistry.INSTANCE.registerFactory("cpu:cpu".id(), ContainerFactory { syncID, id, player, buf ->
        CpuContainer(player.world, buf.readBlockPos(), syncID)
    })
    Packets.registerPackets()

    FabricItemGroupBuilder
        .create("cpu:cpu".id())
        .icon { ItemStack(CpuItems.CPU) }
        .appendItems {
            it.append(CpuItems.CPU)
            it.append(CpuItems.SPEAKER)
            it.append(CpuItems.MODEM)
        }
        .build()
}

fun String.id(): Identifier {
    return Identifier(this)
}

fun MutableList<ItemStack>.append(item: Item) {
    this.add(ItemStack(item))
}