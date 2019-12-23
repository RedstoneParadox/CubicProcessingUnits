package io.github.redstoneparadox.cpu.util

class SynchronizedBox<T>(private var value: T) {
    @Synchronized
    fun get(): T {
        return value
    }

    @Synchronized
    fun set(value: T) {
        this.value = value
    }

    inline fun operate(function: T.() -> T) {
        set(function(get()))
    }
}