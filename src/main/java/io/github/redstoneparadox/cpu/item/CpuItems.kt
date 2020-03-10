package io.github.redstoneparadox.cpu.item

import io.github.redstoneparadox.cpu.block.CpuBlocks
import net.minecraft.block.Block
import net.minecraft.item.*
import net.minecraft.util.registry.Registry

object CpuItems {

    val CPU = blockItem(CpuBlocks.CPU)
    val SPEAKER = blockItem(CpuBlocks.SPEAKER)
    val MODEM = blockItem(CpuBlocks.MODEM)
    val PRINTER = blockItem(CpuBlocks.PRINTER)

    val PRINTED_DOCUMENT = WrittenBookItem(Item.Settings().group(ItemGroup.MISC))

    fun register() {
        Registry.register(Registry.ITEM, "cpu:cpu", CPU)
        Registry.register(Registry.ITEM, "cpu:speaker", SPEAKER)
        Registry.register(Registry.ITEM, "cpu:modem", MODEM)
        register("cpu:printer", PRINTER)

        register("cpu:printed_document", PRINTED_DOCUMENT)
    }

    private fun register(id: String, item: Item) {
        Registry.register(Registry.ITEM, id, item)
    }

    private fun blockItem(block: Block): BlockItem {
        return BlockItem(block, Item.Settings())
    }
}