package io.github.redstoneparadox.cpu

import net.fabricmc.fabric.api.container.ContainerFactory
import net.fabricmc.fabric.api.container.ContainerProviderRegistry
import net.minecraft.util.Identifier
import io.github.redstoneparadox.cpu.block.CpuBlocks
import io.github.redstoneparadox.cpu.block.entity.CpuBlockEntityTypes
import io.github.redstoneparadox.cpu.item.CpuItems
import io.github.redstoneparadox.cpu.misc.ComputerContainer
import io.github.redstoneparadox.cpu.misc.PrinterContainer
import io.github.redstoneparadox.cpu.networking.Packets
import io.github.redstoneparadox.cpu.scripting.Document
import io.github.redstoneparadox.cpu.scripting.File
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder
import net.minecraft.item.Item
import net.minecraft.item.ItemStack

@Suppress("unused")
fun init() {
    CpuBlockEntityTypes.register()
    CpuBlocks.register()
    CpuItems.register()

    ContainerProviderRegistry.INSTANCE.registerFactory("cpu:computer".id(), ContainerFactory { syncID, id, player, buf ->
        ComputerContainer(player.world, buf.readBlockPos(), syncID)
    })
    ContainerProviderRegistry.INSTANCE.registerFactory("cpu:printer".id(), ContainerFactory { syncID, id, player, buf ->
        PrinterContainer(player.world, buf.readBlockPos(), player.inventory, syncID)
    })
    Packets.registerPackets()

    FabricItemGroupBuilder
        .create("cpu:cpu".id())
        .icon { ItemStack(CpuItems.COMPUTER) }
        .appendItems {
            it.append(CpuItems.COMPUTER)
            it.append(CpuItems.SPEAKER)
            it.append(CpuItems.MODEM)
            it.append(CpuItems.PRINTER)
            it.append(CpuItems.REDSTONE_APADTER)
        }
        .build()

    File.addSupplier("txt") {
        val document = Document(it, "")
        document.addPage("")
        Document.DocumentFile(document)
    }
    File.addDeserializer("txt", Document.DocumentFile.Companion::fromNBT)
}

fun String.id(): Identifier {
    return Identifier(this)
}

fun MutableList<ItemStack>.append(item: Item) {
    this.add(ItemStack(item))
}