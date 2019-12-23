package io.github.redstoneparadox.cpu.block.entity

import io.github.redstoneparadox.cpu.api.Peripheral
import io.github.redstoneparadox.cpu.api.PeripheralBlockEntity
import io.github.redstoneparadox.cpu.api.PeripheralHandle
import io.github.redstoneparadox.cpu.scripting.ConnectionManager
import io.github.redstoneparadox.cpu.scripting.ConnectionManagerAccessor
import org.jetbrains.annotations.NotNull

class ModemBlockEntity: PeripheralBlockEntity(CpuBlockEntityTypes.MODEM) {
    var connection: ConnectionManager.Connection? = null
    var currentID: Int = 0;

    override fun getPeripheral(handle: PeripheralHandle): Peripheral<*> {
        return ModemPeripheral(this)
    }

    override fun getDefaultName(): String {
        return "modem"
    }

    fun open(id: Int) {
        if (world != null && !world!!.isClient) {
            val manager = (world as ConnectionManagerAccessor).connectionManager
            if (connection != null) manager.close(currentID)
            connection = manager.open(id)
            currentID = id;
        }
    }

    fun close() {
        if (world != null && !world!!.isClient) {
            val manager = (world as ConnectionManagerAccessor).connectionManager
            if (connection != null) manager.close(currentID)
        }
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
            wrapped?.connection?.send(any)
        }

        @Synchronized
        fun receive(timeout: Long): Any? {
            return wrapped?.connection?.receive(timeout)
        }
    }
}