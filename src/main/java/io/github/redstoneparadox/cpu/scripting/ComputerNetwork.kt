package io.github.redstoneparadox.cpu.scripting

import io.github.redstoneparadox.cpu.util.SynchronizedBox
import io.github.redstoneparadox.cpu.api.Cloneable
import io.github.redstoneparadox.cpu.util.LimitedQueue
import kotlinx.coroutines.runBlocking
import java.util.*
import kotlin.collections.HashMap

import kotlin.concurrent.schedule

class ComputerNetwork {
    private val frequencies: MutableMap<Int, Frequency> = hashMapOf()

    fun connect(frequencyID: Int): Pair<Frequency, Any>  {
        val handle = Any()
        if (!frequencies.containsKey(frequencyID)) {
            frequencies[frequencyID] = Frequency(hashMapOf())
        }
        val frequency = frequencies[frequencyID]!!
        frequency.connections[handle] = LimitedQueue<Any>(16)

        return Pair(frequency, handle)
    }

    fun disconnect(frequency: Frequency, handle: Any) {
        frequency.connections.remove(handle)
    }

    fun tick() {
        for (entry in frequencies) {
            if (entry.value.connections.isEmpty()) frequencies.remove(entry.key)
        }
    }

    class Frequency(val connections: HashMap<Any, Queue<Any>>) {
        fun send(any: Any, handle: Any) {
            for (entry in connections) {
                if (handle != entry.key) {
                    val toSend = when (any) {
                        is Boolean, is Byte, is Short, is Int, is Long, is Float, is Double, is Char, is String -> any
                        is Cloneable<*> -> any.clone()
                        else -> throw Exception()
                    }

                    entry.value.offer(toSend)
                }
            }
        }

        fun receive(handle: Any, wait: Long): Any? {
            val queue = connections[handle]
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
