package io.github.redstoneparadox.cpu.misc

import net.minecraft.container.Container
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import io.github.redstoneparadox.cpu.block.entity.CpuBlockEntity

class CpuContainer(val world: World, val pos: BlockPos, syncId: Int) : Container(null, syncId) {
    override fun canUse(player: PlayerEntity): Boolean {
        return true
    }

    fun save(script: String) {
        val be = world.getBlockEntity(pos)
        if (be is CpuBlockEntity) be.save(script)
    }

    fun load(): String {
        val be = world.getBlockEntity(pos)
        if (be is CpuBlockEntity) return be.load()
        return  ""
    }

    fun run() {
        val be = world.getBlockEntity(pos)
        if (be is CpuBlockEntity) be.run()
    }
}