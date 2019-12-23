package io.github.redstoneparadox.cpu.block

import net.minecraft.block.Block
import net.minecraft.util.registry.Registry

object CpuBlocks {

    val CPU: Block = CpuBlock()
    val SPEAKER: Block = SpeakerBlock()
    val MODEM: Block = ModemBlock()

    fun register() {
        Registry.register(Registry.BLOCK, "cpu:cpu", CPU)
        Registry.register(Registry.BLOCK, "cpu:speaker", SPEAKER)
        Registry.register(Registry.BLOCK, "cpu:modem", MODEM)
    }
}