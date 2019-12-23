package io.github.redstoneparadox.cpu.scripting

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.runBlocking
import java.util.*
import kotlin.concurrent.schedule

class ConnectionManager {

    private val connections: MutableMap<Int, Connection> = mutableMapOf()

    fun open(id: Int): Connection {
        var connection = connections[id]
        if (connection == null) {
            connection = Connection(Channel(Int.MAX_VALUE))
            connections[id] = connection
        }
        connection.count += 1
        return connection
    }

    fun close(id: Int) {
        val connection = connections[id]
        if (connection != null) {
            connection.count -= 1
            if (connection.count <= 0) {
                connection.close()
                connections.remove(id)
            }
        }
    }

    class Connection(private val channel: Channel<Any>) {
        var count = 0

        fun send(any: Any) = runBlocking {
            channel.send(any)
        }

        fun receive(wait: Long): Any? = runBlocking {
            var timeout = false
            Timer().schedule(wait) {timeout = true}
            while (true) {
                if (!channel.isEmpty) break
                if (timeout) return@runBlocking null
            }
            return@runBlocking channel.receive()
        }

        internal fun close() {
            channel.close()
        }
    }
}