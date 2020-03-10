package io.github.redstoneparadox.cpu.scripting

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.runBlocking
import java.util.*
import java.util.concurrent.Future
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException
import kotlin.concurrent.schedule

class ChannelFuture<V>(private val receiver: Channel<V>): Future<V> {
    private var done = false

    override fun isDone(): Boolean {
        if (!receiver.isEmpty) done = true
        return done
    }

    override fun get(): V = runBlocking { receiver.receive() }

    override fun get(timeout: Long, unit: TimeUnit): V = runBlocking {
        if (isCancelled) throw CancellationException()
        var timedOut = false
        Timer().schedule(unit.toMillis(timeout)) { timedOut = true }

        while (!timedOut) {
            if (!receiver.isEmpty) {
                done = true
                return@runBlocking receiver.receive()
            }
            if (isCancelled) {
                done = true
                throw CancellationException()
            }
        }

        done = true
        throw TimeoutException()
    }

    fun get(timeout: Long): V {
        return get(timeout, TimeUnit.MILLISECONDS)
    }

    override fun cancel(mayInterruptIfRunning: Boolean): Boolean {
        receiver.cancel()
        done = true
        return true
    }

    override fun isCancelled(): Boolean = receiver.isClosedForReceive

}