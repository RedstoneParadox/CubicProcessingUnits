package io.github.redstoneparadox.cpu.block.entity

import io.github.redstoneparadox.cpu.api.Peripheral
import io.github.redstoneparadox.cpu.api.PeripheralBlockEntity
import io.github.redstoneparadox.cpu.api.PeripheralHandle
import io.github.redstoneparadox.cpu.block.CpuBlocks
import io.github.redstoneparadox.cpu.block.RedstoneAdapterBlock
import org.jetbrains.annotations.NotNull

class RedstoneAdapterBlockEntity: PeripheralBlockEntity(CpuBlockEntityTypes.REDSTONE_ADAPTER) {
    var handle: PeripheralHandle? = null

    override fun getPeripheral(handle: PeripheralHandle): Peripheral<*> {
        this.handle = handle
        return RedstoneAdapterPeripheral(this)
    }

    override fun getDefaultName(): String {
        return "redstone adapter"
    }

    fun setEmitting(value: Boolean) {
        val world = world
        if (world != null) {
            val state = world.getBlockState(pos)
            world.setBlockState(pos, state.with(RedstoneAdapterBlock.EMITTING, value), 2)
            world.updateNeighbors(pos, CpuBlocks.REDSTONE_ADAPTER)
        }
    }

    fun setPower(value: Int) {
        val world = world
        if (world != null) {
            val state = world.getBlockState(pos)
            world.setBlockState(pos, state.with(RedstoneAdapterBlock.POWER, value), 2)
            world.updateNeighbors(pos, CpuBlocks.REDSTONE_ADAPTER)
        }
    }

    fun getPower(): Int {
        val world = world
        if (world != null) {
            val state = world.getBlockState(pos)
            if (state[RedstoneAdapterBlock.EMITTING] == false) return 0
            else return state[RedstoneAdapterBlock.POWER]
        }
        return 0
    }

    class RedstoneAdapterPeripheral(wrapped: @NotNull RedstoneAdapterBlockEntity): Peripheral<RedstoneAdapterBlockEntity>(wrapped) {
        @Synchronized
        fun setEmitting(value: Boolean) {
            wrapped?.setEmitting(value)
        }

        @Synchronized
        fun setPower(value: Int) {
            wrapped?.setPower(value)
        }

        @Synchronized
        fun getPower(): Int {
            return wrapped?.getPower() ?: 0
        }
    }
}