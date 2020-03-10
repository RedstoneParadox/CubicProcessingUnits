package io.github.redstoneparadox.cpu.block.entity

import net.minecraft.block.entity.BlockEntityType
import net.minecraft.util.registry.Registry
import io.github.redstoneparadox.cpu.block.CpuBlocks
import java.util.function.Supplier

object CpuBlockEntityTypes {

    val CPU: BlockEntityType<CpuBlockEntity> = BlockEntityType.Builder.create(Supplier { CpuBlockEntity() }, CpuBlocks.CPU).build(null)
    val SPEAKER: BlockEntityType<SpeakerBlockEntity> = BlockEntityType.Builder.create(Supplier { SpeakerBlockEntity() }, CpuBlocks.SPEAKER).build(null)
    val MODEM: BlockEntityType<ModemBlockEntity> = BlockEntityType.Builder.create(Supplier { ModemBlockEntity() }, CpuBlocks.MODEM).build(null)
    val PRINTER: BlockEntityType<PrinterBlockEntity> = BlockEntityType.Builder.create(Supplier { PrinterBlockEntity() }, null).build(null)

    fun register() {
        Registry.register(Registry.BLOCK_ENTITY_TYPE, "cpu:cpu", CPU)
        Registry.register(Registry.BLOCK_ENTITY_TYPE, "cpu:speaker", SPEAKER)
        Registry.register(Registry.BLOCK_ENTITY_TYPE, "cpu:modem", MODEM)
        register("cpu:printer", PRINTER)
    }

    fun register(id: String, type: BlockEntityType<*>) {
        Registry.register(Registry.BLOCK_ENTITY_TYPE, id, type)
    }
}