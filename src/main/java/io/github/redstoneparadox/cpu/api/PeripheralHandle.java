package io.github.redstoneparadox.cpu.api;

import io.github.redstoneparadox.cpu.computer.Computer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PeripheralHandle {
    private boolean closed = false;
    private @Nullable Computer computer;

    public PeripheralHandle(@NotNull Computer computer) {
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
