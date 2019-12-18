package io.github.redstoneparadox.cpu.block

import net.minecraft.block.Block
import net.minecraft.util.registry.Registry

object CpuBlocks {

    val CPU: Block = CpuBlock()

    fun register() {
        Registry.register(Registry.BLOCK, "cpu:cpu",
            CPU
        )
    }
}