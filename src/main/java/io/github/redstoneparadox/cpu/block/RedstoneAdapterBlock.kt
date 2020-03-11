package io.github.redstoneparadox.cpu.block

import io.github.redstoneparadox.cpu.block.entity.ModemBlockEntity
import io.github.redstoneparadox.cpu.block.entity.RedstoneAdapterBlockEntity
import net.fabricmc.fabric.api.block.FabricBlockSettings
import net.minecraft.block.*
import net.minecraft.block.entity.BlockEntity
import net.minecraft.item.ItemPlacementContext
import net.minecraft.state.StateManager
import net.minecraft.state.property.BooleanProperty
import net.minecraft.state.property.Properties
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.world.BlockView
import net.minecraft.world.World

class RedstoneAdapterBlock: BlockWithEntity(FabricBlockSettings.copy(Blocks.IRON_BLOCK).build()) {
    override fun createBlockEntity(view: BlockView?): BlockEntity? {
        return RedstoneAdapterBlockEntity()
    }

    override fun onBlockRemoved(state: BlockState, world: World, pos: BlockPos, newState: BlockState, moved: Boolean) {
        val be = world.getBlockEntity(pos)
        if (be is RedstoneAdapterBlockEntity) be.handle?.disconnect()
        super.onBlockRemoved(state, world, pos, newState, moved)
    }

    override fun getRenderType(state: BlockState?): BlockRenderType {
        return BlockRenderType.MODEL
    }

    override fun emitsRedstonePower(state: BlockState): Boolean {
        return state[EMITTING]
    }

    override fun getWeakRedstonePower(state: BlockState, view: BlockView, pos: BlockPos, facing: Direction): Int {
        return state[POWER]
    }

    override fun appendProperties(builder: StateManager.Builder<Block, BlockState>) {
        builder.add(EMITTING, POWER)
        super.appendProperties(builder)
    }

    override fun getPlacementState(ctx: ItemPlacementContext): BlockState = defaultState.with(EMITTING, false).with(POWER, 0)

    companion object {
        internal val EMITTING = BooleanProperty.of("emitting")
        internal val POWER = Properties.POWER
    }
}