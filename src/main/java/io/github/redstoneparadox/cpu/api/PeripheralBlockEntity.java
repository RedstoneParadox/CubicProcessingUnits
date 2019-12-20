package io.github.redstoneparadox.cpu.api;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;

public abstract class PeripheralBlockEntity extends BlockEntity {
    public PeripheralBlockEntity(BlockEntityType<?> type) {
        super(type);
    }

    public abstract Peripheral<?> getPeripheral(PeripheralHandle handle);

    public abstract String getDefaultName();
}
