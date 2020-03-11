package io.github.redstoneparadox.cpu.block

import net.minecraft.block.Block
import net.minecraft.util.registry.Registry

object CpuBlocks {

    val COMPUTER: Block = ComputerBlock()
    val SPEAKER: Block = SpeakerBlock()
    val MODEM: Block = ModemBlock()
    val PRINTER: Block = PrinterBlock()

    fun register() {
        Registry.register(Registry.BLOCK, "cpu:computer", COMPUTER)
        Registry.register(Registry.BLOCK, "cpu:speaker", SPEAKER)
        Registry.register(Registry.BLOCK, "cpu:modem", MODEM)
        register("cpu:printer", PRINTER)
    }

    fun register(id: String, block: Block) {
        Registry.register(Registry.BLOCK, id, block)
    }
}