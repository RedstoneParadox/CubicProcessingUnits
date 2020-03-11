package io.github.redstoneparadox.cpu.api;

import io.github.redstoneparadox.cpu.block.entity.ComputerBlockEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PeripheralHandle {
    private boolean closed = false;
    private @Nullable ComputerBlockEntity computer;

    public PeripheralHandle(@NotNull ComputerBlockEntity computer) {
        this.computer = computer;
    }

    public void disconnect() {
        if (!closed && computer != null) {
            closed = true;
            computer.disconnect(this);
            computer = null;
        }
    }
}
