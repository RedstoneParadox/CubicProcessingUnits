package io.github.redstoneparadox.cpu.item

import io.github.redstoneparadox.cpu.block.CpuBlocks
import net.minecraft.block.Block
import net.minecraft.item.BlockItem
import net.minecraft.item.Item
import net.minecraft.item.ItemGroup
import net.minecraft.item.WrittenBookItem
import net.minecraft.util.registry.Registry

object CpuItems {

    val COMPUTER = blockItem(CpuBlocks.COMPUTER)
    val SPEAKER = blockItem(CpuBlocks.SPEAKER)
    val MODEM = blockItem(CpuBlocks.MODEM)
    val PRINTER = blockItem(CpuBlocks.PRINTER)
    val REDSTONE_APADTER = blockItem(CpuBlocks.REDSTONE_ADAPTER)
    val DISK_DRIVE = blockItem(CpuBlocks.DISK_DRIVE)

    val PRINTED_DOCUMENT = WrittenBookItem(Item.Settings().group(ItemGroup.MISC).maxCount(16))

    val BLUE_FLOPPY_DISK = FloppyDiskItem()

    fun register() {
        Registry.register(Registry.ITEM, "cpu:computer", COMPUTER)
        Registry.register(Registry.ITEM, "cpu:speaker", SPEAKER)
        Registry.register(Registry.ITEM, "cpu:modem", MODEM)
        register("cpu:printer", PRINTER)
        register("cpu:redstone_adapter", REDSTONE_APADTER)
        register("cpu:disk_drive", DISK_DRIVE)

        register("cpu:printed_document", PRINTED_DOCUMENT)

        register("cpu:blue_floppy_disk", BLUE_FLOPPY_DISK)
    }

    private fun register(id: String, item: Item) {
        Registry.register(Registry.ITEM, id, item)
    }

    private fun blockItem(block: Block): BlockItem {
        return BlockItem(block, Item.Settings())
    }
}