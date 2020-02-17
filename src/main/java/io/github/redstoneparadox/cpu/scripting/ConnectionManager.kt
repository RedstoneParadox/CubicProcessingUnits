package io.github.redstoneparadox.cpu.scripting

import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.broadcast
import kotlinx.coroutines.runBlocking
import java.util.*
import kotlin.concurrent.schedule

class ConnectionManager {

    private val connections: MutableMap<Int, Connection> = mutableMapOf()

    private val broadcasters: MutableMap<Int, BroadcastChannel<Any>> = mutableMapOf()

    fun newOpen(id: Int): NewConnection {
        var broadcaster = broadcasters[id]
        if (broadcaster == null) {
            broadcaster = BroadcastChannel(255)
            broadcasters[id] = broadcaster
        }
        return NewConnection(broadcaster)
    }

    fun newClose(id: Int) {

    }

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

    class NewConnection(private val sender: BroadcastChannel<Any>) {
        private val receiver = sender.openSubscription()

        fun send(any: Any) = runBlocking {
            sender.send(any)
        }

        fun receive(maxWait: Long): Any? = runBlocking {
            var timeout = false
            Timer().schedule(maxWait) { timeout = true }
            while (true) {
                if (!receiver.isEmpty) break
                else return@runBlocking null
            }
            return@runBlocking receiver.receive()
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