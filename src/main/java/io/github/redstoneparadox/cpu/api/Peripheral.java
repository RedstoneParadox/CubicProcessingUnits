package io.github.redstoneparadox.cpu.api;

import io.github.redstoneparadox.cpu.block.entity.CpuBlockEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Peripheral<T> {

    private final String name;
    private @Nullable T wrapped;

    public Peripheral(String name, @NotNull T wrapped) {
        this.name = name;
        this.wrapped = wrapped;
    }

    void disconnect() {
        wrapped = null;
    }

    public synchronized String getName() {
        return name;
    }
}
