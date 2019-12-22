package io.github.redstoneparadox.cpu.item

import io.github.redstoneparadox.cpu.block.CpuBlocks
import net.minecraft.block.Block
import net.minecraft.item.BlockItem
import net.minecraft.item.Item
import net.minecraft.util.registry.Registry

object CpuItems {

    val CPU = blockItem(CpuBlocks.CPU)
    val SPEAKER = blockItem(CpuBlocks.SPEAKER)

    fun register() {
        Registry.register(Registry.ITEM, "cpu:cpu", CPU)
        Registry.register(Registry.ITEM, "cpu:speaker", SPEAKER)
    }

    private fun blockItem(block: Block): BlockItem {
        return BlockItem(block, Item.Settings())
    }
}