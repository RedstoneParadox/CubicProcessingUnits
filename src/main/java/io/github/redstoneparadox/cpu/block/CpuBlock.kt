package io.github.redstoneparadox.cpu.block

import io.github.redstoneparadox.cpu.api.PeripheralBlockEntity
import io.github.redstoneparadox.cpu.api.PeripheralHandle
import net.fabricmc.fabric.api.block.FabricBlockSettings
import net.fabricmc.fabric.api.container.ContainerProviderRegistry
import net.minecraft.block.entity.BlockEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.PacketByteBuf
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.world.BlockView
import net.minecraft.world.World
import io.github.redstoneparadox.cpu.block.entity.CpuBlockEntity
import io.github.redstoneparadox.cpu.id
import net.minecraft.block.*
import net.minecraft.entity.LivingEntity
import net.minecraft.item.ItemPlacementContext
import net.minecraft.item.ItemStack
import net.minecraft.server.world.ServerWorld
import net.minecraft.state.StateManager
import net.minecraft.util.math.Direction
import java.util.*

class CpuBlock: HorizontalFacingBlock(FabricBlockSettings.copy(Blocks.IRON_BLOCK).build()), BlockEntityProvider {
    override fun createBlockEntity(view: BlockView): BlockEntity {
        return CpuBlockEntity()
    }

    override fun neighborUpdate(state: BlockState, world: World, pos: BlockPos, block: Block, neighborPos: BlockPos, moved: Boolean) {
        findPeripherals(world, pos, neighborPos)
    }

    override fun onUse(state: BlockState, world: World, pos: BlockPos, player: PlayerEntity, hand: Hand, hit: BlockHitResult): ActionResult {
        if (world.isClient) return ActionResult.SUCCESS

        val be = world.getBlockEntity(pos)
        if (be != null && be is CpuBlockEntity) {
            ContainerProviderRegistry.INSTANCE.openContainer("cpu:cpu".id(), player) { buf -> buf.writeBlockPos(pos) }
        }

        return ActionResult.SUCCESS
    }

    override fun appendProperties(builder: StateManager.Builder<Block, BlockState>) {
        builder.add(FACING)
    }

    override fun getPlacementState(ctx: ItemPlacementContext): BlockState {
        return defaultState.with(FACING, ctx.playerFacing.opposite)
    }

    companion object {
        internal fun findPeripherals(world: World, pos: BlockPos, neighborPos: BlockPos) {
            if (!world.isClient) {
                val be = world.getBlockEntity(pos)
                val neighborBe = world.getBlockEntity(neighborPos)
                if (be is CpuBlockEntity && neighborBe is PeripheralBlockEntity) {
                    val handle = PeripheralHandle(be)
                    val peripheral = neighborBe.getPeripheral(handle)
                    be.connect(handle, peripheral, neighborBe.defaultName)
                }
            }
        }
    }
}