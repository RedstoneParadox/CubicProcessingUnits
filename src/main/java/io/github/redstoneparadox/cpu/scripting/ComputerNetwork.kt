package io.github.redstoneparadox.cpu.scripting

import io.github.redstoneparadox.cpu.api.Cloneable
import io.github.redstoneparadox.cpu.util.LimitedQueue
import io.github.redstoneparadox.cpu.util.SynchronizedBox
import java.util.*
import kotlin.concurrent.schedule

class ComputerNetwork {
    private val frequencies: MutableMap<Int, Frequency> = hashMapOf()

    fun connect(frequencyID: Int): Pair<Frequency, Any> {
        val handle = Any()
        if (!frequencies.containsKey(frequencyID)) {
            frequencies[frequencyID] = Frequency(SynchronizedBox(hashMapOf()))
        }
        val frequency = frequencies[frequencyID]!!
        frequency.connections.borrow { it[handle] = LimitedQueue<Any>(16) }

        return Pair(frequency, handle)
    }

    fun disconnect(frequency: Frequency, handle: Any) {
        frequency.connections.borrow { it.remove(handle) }
    }

    fun tick() {
        for (entry in frequencies) {
            if (entry.value.connections.map { it.isEmpty() }) frequencies.remove(entry.key)
        }
    }

    class Frequency(val connections: SynchronizedBox<HashMap<Any, LimitedQueue<Any>>>) {
        fun send(any: Any, handle: Any) = connections.borrow {
            for (entry in it) {
                if (handle != entry.key) {
                    val toSend = when (any) {
                        is Boolean, is Byte, is Short, is Int, is Long, is Float, is Double, is Char, is String -> any
                        is Cloneable<*> -> any.clone()
                        else -> throw Exception()
                    }

                    entry.value.add(toSend)
                }
            }
        }

        fun receive(handle: Any, wait: Long): Any? {
            val queue = connections.map { it[handle] }
            if (queue != null) {
                var timedOut = false
                Timer().schedule(wait * 1000L) { timedOut = true }

                while (!timedOut) {
                    if (!queue.isEmpty()) {
                        return queue.remove()
                    }
                }
            }

            return null
        }
    }
}
