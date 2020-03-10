package io.github.redstoneparadox.cpu.block.entity

import io.github.redstoneparadox.cpu.api.Peripheral
import io.github.redstoneparadox.cpu.api.PeripheralBlockEntity
import io.github.redstoneparadox.cpu.api.PeripheralHandle
import io.github.redstoneparadox.cpu.scripting.ComputerNetwork
import io.github.redstoneparadox.cpu.scripting.ComputerNetworkAccessor
import org.jetbrains.annotations.NotNull

class ModemBlockEntity: PeripheralBlockEntity(CpuBlockEntityTypes.MODEM) {
    var handle: Any? = null
    var frequency: ComputerNetwork.Frequency? = null

    override fun getPeripheral(handle: PeripheralHandle): Peripheral<*> {
        return ModemPeripheral(this)
    }

    override fun getDefaultName(): String {
        return "modem"
    }

    fun open(id: Int) {
        if (world != null && !world!!.isClient) {
            val network = (world as ComputerNetworkAccessor).network
            if (handle != null && frequency != null) network.disconnect(frequency!!, handle!!)
            val pair = network.connect(id)
            frequency = pair.first
            handle = pair.second
        }
    }

    fun close() {
        if (world != null && !world!!.isClient) {
            val network = (world as ComputerNetworkAccessor).network
            if (frequency != null && handle != null) network.disconnect(frequency!!, handle!!)
            frequency = null
            handle = null
        }
    }

    fun send(any: Any) {
        if (frequency != null && handle != null) frequency!!.send(any, handle!!)
    }

    fun receive(wait: Long): Any? {
        if (frequency != null && handle != null) return frequency!!.receive(handle!!, wait)
        return null
    }

    class ModemPeripheral(wrapped: @NotNull ModemBlockEntity): Peripheral<ModemBlockEntity>(wrapped) {

        @Synchronized
        fun openConnection(id: Int) {
            wrapped?.open(id)
        }

        @Synchronized
        fun closeConnection() {
            wrapped?.close()
        }

        @Synchronized
        fun send(any: Any) {
            println("Sending a message!")
            wrapped?.send(any)
        }

        @Synchronized
        fun receive(wait: Long): Any? {
            println("Receiving what's mine!")
            return wrapped?.receive(wait)
        }
    }
}