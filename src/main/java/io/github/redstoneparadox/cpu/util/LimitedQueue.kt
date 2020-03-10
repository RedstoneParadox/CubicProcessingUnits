package io.github.redstoneparadox.cpu.util

import java.util.*


class LimitedQueue<E>(private val limit: Int) : LinkedList<E>() {
    override fun add(element: E): Boolean {
        super.add(element)
        while (size > limit) {
            super.remove()
        }
        return true
    }
}