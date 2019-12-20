package io.github.redstoneparadox.cpu.api;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;

public abstract class PeripheralBlockEntity extends BlockEntity {
    public PeripheralBlockEntity(BlockEntityType<?> type) {
        super(type);
    }

    public abstract Peripheral<?> getPeripheral(PeripheralHandle handle);
}
