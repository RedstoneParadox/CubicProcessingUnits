package io.github.redstoneparadox.cpu.util

import java.util.*


class LimitedQueue<E>(private val limit: Int) : LinkedList<E>() {
    override fun add(element: E): Boolean {
        val bool = super.add(element)
        println("Did it get added to the queue? ${bool}")
        while (size > limit) {
            super.remove()
        }
        return bool
    }
}