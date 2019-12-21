package io.github.redstoneparadox.cpu.block

import io.github.redstoneparadox.cpu.block.entity.SpeakerBlockEntity
import net.fabricmc.fabric.api.block.FabricBlockSettings
import net.minecraft.block.*
import net.minecraft.block.entity.BlockEntity
import net.minecraft.util.math.BlockPos
import net.minecraft.world.BlockView
import net.minecraft.world.IWorld
import net.minecraft.world.World

class SpeakerBlock: BlockWithEntity(FabricBlockSettings.copy(Blocks.IRON_BLOCK).build()) {
    override fun createBlockEntity(view: BlockView?): BlockEntity? {
        return SpeakerBlockEntity()
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