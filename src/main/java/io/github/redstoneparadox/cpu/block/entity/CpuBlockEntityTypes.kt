package io.github.redstoneparadox.cpu.block.entity

import net.minecraft.block.entity.BlockEntityType
import net.minecraft.util.registry.Registry
import io.github.redstoneparadox.cpu.block.CpuBlocks
import java.util.function.Supplier

object CpuBlockEntityTypes {

    val COMPUTER: BlockEntityType<ComputerBlockEntity> = BlockEntityType.Builder.create(Supplier { ComputerBlockEntity() }, CpuBlocks.COMPUTER).build(null)
    val SPEAKER: BlockEntityType<SpeakerBlockEntity> = BlockEntityType.Builder.create(Supplier { SpeakerBlockEntity() }, CpuBlocks.SPEAKER).build(null)
    val MODEM: BlockEntityType<ModemBlockEntity> = BlockEntityType.Builder.create(Supplier { ModemBlockEntity() }, CpuBlocks.MODEM).build(null)
    val PRINTER: BlockEntityType<PrinterBlockEntity> = BlockEntityType.Builder.create(Supplier { PrinterBlockEntity() }, CpuBlocks.PRINTER).build(null)
    val REDSTONE_ADAPTER: BlockEntityType<RedstoneAdapterBlockEntity> = BlockEntityType.Builder.create(Supplier { RedstoneAdapterBlockEntity() }, CpuBlocks.REDSTONE_ADAPTER).build(null)

    fun register() {
        Registry.register(Registry.BLOCK_ENTITY_TYPE, "cpu:computer", COMPUTER)
        Registry.register(Registry.BLOCK_ENTITY_TYPE, "cpu:speaker", SPEAKER)
        Registry.register(Registry.BLOCK_ENTITY_TYPE, "cpu:modem", MODEM)
        register("cpu:printer", PRINTER)
        register("cpu:redstone_adapter", REDSTONE_ADAPTER)
    }

    fun register(id: String, type: BlockEntityType<*>) {
        Registry.register(Registry.BLOCK_ENTITY_TYPE, id, type)
    }
}