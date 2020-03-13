package io.github.redstoneparadox.cpu.block

import io.github.redstoneparadox.cpu.block.entity.DiskDriveBlockEntity
import io.github.redstoneparadox.cpu.item.FloppyDiskItem
import net.fabricmc.fabric.api.block.FabricBlockSettings
import net.minecraft.block.*
import net.minecraft.block.entity.BlockEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemPlacementContext
import net.minecraft.state.StateManager
import net.minecraft.state.property.BooleanProperty
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.world.BlockView
import net.minecraft.world.World

class DiskDriveBlock:  HorizontalFacingBlock(FabricBlockSettings.copy(Blocks.IRON_BLOCK).build()), BlockEntityProvider {
    override fun createBlockEntity(view: BlockView): BlockEntity {
        return DiskDriveBlockEntity()
    }

    override fun onBlockRemoved(state: BlockState, world: World, pos: BlockPos, newState: BlockState, moved: Boolean) {
        val be = world.getBlockEntity(pos)
        if (be is DiskDriveBlockEntity) be.handle?.disconnect()
        super.onBlockRemoved(state, world, pos, newState, moved)
    }

    override fun onUse(state: BlockState, world: World, pos: BlockPos, player: PlayerEntity, hand: Hand, hit: BlockHitResult): ActionResult {
        if (world.isClient) return ActionResult.SUCCESS

        val be = world.getBlockEntity(pos)
        if (be is DiskDriveBlockEntity) {
            val stack = player.getStackInHand(hand)
            if (stack.isEmpty && !be.isEmpty()) {
                player.setStackInHand(hand, be.remove())
                be.handle?.disconnect()
                be.handle = null
                world.setBlockState(pos, state.with(HAS_DISK, false))
                return ActionResult.SUCCESS
            }
            else if (stack.item is FloppyDiskItem && be.insert(stack)) {
                be.handle?.disconnect()
                be.handle = null
                world.setBlockState(pos, state.with(HAS_DISK, true))
                return ActionResult.SUCCESS
            }
        }

        return ActionResult.FAIL
    }

    override fun getRenderType(state: BlockState?): BlockRenderType {
        return BlockRenderType.MODEL
    }

    override fun appendProperties(builder: StateManager.Builder<Block, BlockState>) {
        builder.add(FACING, HAS_DISK)
    }

    override fun getPlacementState(ctx: ItemPlacementContext): BlockState {
        return defaultState.with(FACING, ctx.playerFacing.opposite).with(HAS_DISK, false)
    }

    companion object {
        val HAS_DISK: BooleanProperty = BooleanProperty.of("has_disk")
    }
}