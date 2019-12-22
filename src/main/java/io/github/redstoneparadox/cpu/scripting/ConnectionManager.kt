package io.github.redstoneparadox.cpu.scripting

import kotlinx.coroutines.channels.Channel
import java.util.*
import kotlin.concurrent.schedule

class ConnectionManager {

    private val connections: MutableMap<Int, Connection> = mutableMapOf()

    fun open(id: Int): Connection {
        val connection = if (connections.containsKey(id)) { connections[id] as Connection } else { connections.put(id, Connection(Channel(Int.MAX_VALUE))) as Connection }
        connection.count += 1
        return connection
    }

    fun close(id: Int) {
        if (connections.containsKey(id)) {
            val connection = connections[id] as Connection
            connection.count -= 1
            if (connection.count <= 0) connection.close()
        }
    }

    class Connection(private val channel: Channel<Any>) {
        var count = 0

        suspend fun send(any: Any) {
            channel.send(any)
        }

        suspend fun receive(wait: Long): Any? {
            var timeout = false
            Timer().schedule(wait) {timeout = true}
            while (true) {
                if (!channel.isEmpty) break
                if (timeout) return null
            }
            return channel.receive()
        }

        internal fun close() {
            channel.close()
        }
    }
}