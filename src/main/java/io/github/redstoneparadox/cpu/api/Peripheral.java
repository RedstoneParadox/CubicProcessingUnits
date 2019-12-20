package io.github.redstoneparadox.cpu.api;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Peripheral<T extends PeripheralBlockEntity> {

    private @Nullable T wrapped;

    public Peripheral(@NotNull T wrapped) {
        this.wrapped = wrapped;
    }

    void disconnect() {
        wrapped = null;
    }
}
