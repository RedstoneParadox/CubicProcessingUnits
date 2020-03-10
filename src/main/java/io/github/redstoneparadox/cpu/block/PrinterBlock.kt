package io.github.redstoneparadox.cpu.block

import io.github.redstoneparadox.cpu.block.entity.PrinterBlockEntity
import io.github.redstoneparadox.cpu.block.entity.SpeakerBlockEntity
import net.fabricmc.fabric.api.block.FabricBlockSettings
import net.minecraft.block.BlockRenderType
import net.minecraft.block.BlockState
import net.minecraft.block.BlockWithEntity
import net.minecraft.block.Blocks
import net.minecraft.block.entity.BlockEntity
import net.minecraft.util.math.BlockPos
import net.minecraft.world.BlockView
import net.minecraft.world.World

class PrinterBlock: BlockWithEntity(FabricBlockSettings.copy(Blocks.IRON_BLOCK).build()) {
    override fun createBlockEntity(view: BlockView?): BlockEntity? {
        return PrinterBlockEntity()
    }

    override fun onBlockRemoved(state: BlockState, world: World, pos: BlockPos, newState: BlockState, moved: Boolean) {
        val be = world.getBlockEntity(pos)
        if (be is SpeakerBlockEntity) be.handle?.disconnect()
        super.onBlockRemoved(state, world, pos, newState, moved)
    }

    override fun getRenderType(state: BlockState?): BlockRenderType {
        return BlockRenderType.MODEL
    }
}