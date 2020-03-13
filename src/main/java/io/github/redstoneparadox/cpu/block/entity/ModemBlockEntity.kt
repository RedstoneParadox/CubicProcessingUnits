package io.github.redstoneparadox.cpu.block.entity

import io.github.redstoneparadox.cpu.api.Peripheral
import io.github.redstoneparadox.cpu.api.PeripheralBlockEntity
import io.github.redstoneparadox.cpu.api.PeripheralHandle
import io.github.redstoneparadox.cpu.scripting.ComputerNetwork
import io.github.redstoneparadox.cpu.scripting.ComputerNetworkAccessor
import org.jetbrains.annotations.NotNull

class ModemBlockEntity: PeripheralBlockEntity(CpuBlockEntityTypes.MODEM) {
    var handle: PeripheralHandle? = null
    var networkHandle: Any? = null
    var frequency: ComputerNetwork.Frequency? = null

    override fun getPeripheral(handle: PeripheralHandle): Peripheral<*> {
        this.handle = handle
        return ModemPeripheral(this)
    }

    override fun getDefaultName(): String {
        return "modem"
    }

    override fun isConnected(): Boolean {
        return handle != null
    }

    fun open(id: Int) {
        if (world != null && !world!!.isClient) {
            val network = (world as ComputerNetworkAccessor).network
            if (networkHandle != null && frequency != null) network.disconnect(frequency!!, networkHandle!!)
            val pair = network.connect(id)
            frequency = pair.first
            networkHandle = pair.second
        }
    }

    fun close() {
        if (world != null && !world!!.isClient) {
            val network = (world as ComputerNetworkAccessor).network
            if (frequency != null && networkHandle != null) network.disconnect(frequency!!, networkHandle!!)
            frequency = null
            networkHandle = null
        }
    }

    fun send(any: Any) {
        if (frequency != null && networkHandle != null) frequency!!.send(any, networkHandle!!)
    }

    fun receive(wait: Long): Any? {
        if (frequency != null && networkHandle != null) return frequency!!.receive(networkHandle!!, wait)
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