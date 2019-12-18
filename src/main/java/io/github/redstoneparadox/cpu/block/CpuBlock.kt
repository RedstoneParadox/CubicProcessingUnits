package io.github.redstoneparadox.cpu.block

import net.fabricmc.fabric.api.block.FabricBlockSettings
import net.fabricmc.fabric.api.container.ContainerProviderRegistry
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.block.BlockWithEntity
import net.minecraft.block.Blocks
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

class CpuBlock: BlockWithEntity(FabricBlockSettings.copy(Blocks.IRON_BLOCK).build()) {
    override fun createBlockEntity(view: BlockView?): BlockEntity? {
        return CpuBlockEntity()
    }

    override fun onUse(state: BlockState, world: World, pos: BlockPos, player: PlayerEntity, hand: Hand, hit: BlockHitResult): ActionResult {
        if (world.isClient) return ActionResult.SUCCESS

        val be = world.getBlockEntity(pos)
        if (be != null && be is CpuBlockEntity) {
            ContainerProviderRegistry.INSTANCE.openContainer("cpu:cpu".id(), player) { buf -> buf.writeBlockPos(pos) }
        }

        return ActionResult.SUCCESS
    }
}