package io.github.redstoneparadox.cpu.api;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Peripheral<T extends PeripheralBlockEntity> {
    protected  @Nullable T wrapped;

    public Peripheral(@NotNull T wrapped) {
        this.wrapped = wrapped;
    }

    public void disconnect() {
        wrapped = null;
    }
}
