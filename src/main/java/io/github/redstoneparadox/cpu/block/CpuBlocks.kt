package io.github.redstoneparadox.cpu.block

import net.minecraft.block.Block
import net.minecraft.util.registry.Registry

object CpuBlocks {

    val COMPUTER: Block = ComputerBlock()
    val SPEAKER: Block = SpeakerBlock()
    val MODEM: Block = ModemBlock()
    val PRINTER: Block = PrinterBlock()
    val REDSTONE_ADAPTER: Block = RedstoneAdapterBlock()
    val DISK_DRIVE: Block = DiskDriveBlock()

    fun register() {
        Registry.register(Registry.BLOCK, "cpu:computer", COMPUTER)
        Registry.register(Registry.BLOCK, "cpu:speaker", SPEAKER)
        Registry.register(Registry.BLOCK, "cpu:modem", MODEM)
        register("cpu:printer", PRINTER)
        register("cpu:redstone_adapter", REDSTONE_ADAPTER)
        register("cpu:disk_drive", DISK_DRIVE)
    }

    fun register(id: String, block: Block) {
        Registry.register(Registry.BLOCK, id, block)
    }
}