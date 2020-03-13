package io.github.redstoneparadox.cpu.block

import net.fabricmc.fabric.api.block.FabricBlockSettings
import net.fabricmc.fabric.api.container.ContainerProviderRegistry
import net.minecraft.block.entity.BlockEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.world.BlockView
import net.minecraft.world.World
import io.github.redstoneparadox.cpu.block.entity.ComputerBlockEntity
import io.github.redstoneparadox.cpu.id
import net.minecraft.block.*
import net.minecraft.item.ItemPlacementContext
import net.minecraft.state.StateManager

class ComputerBlock: HorizontalFacingBlock(FabricBlockSettings.copy(Blocks.IRON_BLOCK).build()), BlockEntityProvider {
    override fun createBlockEntity(view: BlockView): BlockEntity {
        return ComputerBlockEntity()
    }

    override fun onBlockRemoved(state: BlockState, world: World, pos: BlockPos, newState: BlockState, moved: Boolean) {
        val be = world.getBlockEntity(pos)
        if (be is ComputerBlockEntity) be.onRemove()
        super.onBlockRemoved(state, world, pos, newState, moved)
    }

    override fun onUse(state: BlockState, world: World, pos: BlockPos, player: PlayerEntity, hand: Hand, hit: BlockHitResult): ActionResult {
        if (world.isClient) return ActionResult.SUCCESS

        val be = world.getBlockEntity(pos)
        if (be != null && be is ComputerBlockEntity) {
            ContainerProviderRegistry.INSTANCE.openContainer("cpu:computer".id(), player) { buf -> buf.writeBlockPos(pos) }
        }

        return ActionResult.SUCCESS
    }

    override fun appendProperties(builder: StateManager.Builder<Block, BlockState>) {
        builder.add(FACING)
    }

    override fun getPlacementState(ctx: ItemPlacementContext): BlockState {
        return defaultState.with(FACING, ctx.playerFacing.opposite)
    }
}