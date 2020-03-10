package io.github.redstoneparadox.cpu.api;

public interface Cloneable<T extends Cloneable<T>> {

    T clone();
}
