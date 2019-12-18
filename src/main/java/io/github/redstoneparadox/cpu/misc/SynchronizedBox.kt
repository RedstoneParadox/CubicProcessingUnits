package io.github.redstoneparadox.cpu.misc

class SynchronizedBox<T>(private var value: T) {

    @Synchronized
    fun get(): T {
        return value
    }

    @Synchronized
    fun set(value: T) {
        this.value = value
    }

}