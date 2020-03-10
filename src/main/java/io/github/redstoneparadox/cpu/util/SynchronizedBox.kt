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

    inline fun mutate(function: (t: T) -> T) {
        set(function(get()))
    }

    inline fun borrow(function: (t: T) -> Unit) {
        function(get())
    }

    inline fun <U> map(function: (t: T) -> U): U {
        return function(get())
    }
}