package io.github.redstoneparadox.cpu.block.entity

import io.github.redstoneparadox.cpu.api.Peripheral
import io.github.redstoneparadox.cpu.api.PeripheralBlockEntity
import io.github.redstoneparadox.cpu.api.PeripheralHandle
import io.github.redstoneparadox.cpu.id
import io.github.redstoneparadox.cpu.networking.Packets
import net.minecraft.server.world.ServerWorld
import org.jetbrains.annotations.NotNull

class SpeakerBlockEntity : PeripheralBlockEntity(CpuBlockEntityTypes.SPEAKER) {
    var handle: PeripheralHandle? = null
    var volume: Float = 0.0f

    override fun getPeripheral(handle: PeripheralHandle): Peripheral<*> {
        this.handle = handle
        return SpeakerPeripheral(this)
    }

    override fun getDefaultName(): String {
        return "speaker"
    }

    override fun isConnected(): Boolean {
        return handle != null
    }

    private fun playSound(id: String, pitch: Float) {
        (world as? ServerWorld)?.let { Packets.soundPacket(it, pos, id.id(), volume, pitch, true) }
    }

    class SpeakerPeripheral(wrapped: @NotNull SpeakerBlockEntity): Peripheral<SpeakerBlockEntity>(wrapped) {
        @Synchronized
        fun playSound(id: String, pitch: Float) {
            wrapped?.playSound(id, pitch)
        }

        @Synchronized
        fun getVolume(): Float {
            return wrapped?.volume ?: 0.0f
        }

        @Synchronized
        fun setVolume(volume: Float) {
            wrapped?.volume = volume
        }
    }
}