package io.github.redstoneparadox.cpu.api;

import io.github.redstoneparadox.cpu.block.entity.CpuBlockEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PeripheralHandle {
    private boolean closed = false;
    private @Nullable CpuBlockEntity cpu;

    public PeripheralHandle(@NotNull CpuBlockEntity cpu) {
        this.cpu = cpu;
    }

    public void disconnect() {
        if (!closed && cpu != null) {
            closed = true;
            cpu.disconnect(this);
            cpu = null;
        }
    }
}
