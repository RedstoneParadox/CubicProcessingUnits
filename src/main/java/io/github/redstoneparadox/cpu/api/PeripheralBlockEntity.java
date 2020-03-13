package io.github.redstoneparadox.cpu.api;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import org.jetbrains.annotations.NotNull;

public abstract class PeripheralBlockEntity extends BlockEntity {
    public PeripheralBlockEntity(BlockEntityType<?> type) {
        super(type);
    }

    public abstract Peripheral<?> getPeripheral(@NotNull PeripheralHandle handle);

    public abstract String getDefaultName();

    public abstract boolean isConnected();
}
