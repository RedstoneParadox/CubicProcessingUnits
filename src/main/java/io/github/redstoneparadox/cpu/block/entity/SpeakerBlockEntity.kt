package io.github.redstoneparadox.cpu.block.entity

import io.github.redstoneparadox.cpu.api.Peripheral
import io.github.redstoneparadox.cpu.api.PeripheralBlockEntity
import io.github.redstoneparadox.cpu.api.PeripheralHandle
import io.github.redstoneparadox.cpu.id
import io.github.redstoneparadox.cpu.networking.Packets
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.server.world.ServerWorld
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvent
import net.minecraft.sound.SoundEvents
import org.jetbrains.annotations.NotNull

class SpeakerBlockEntity : PeripheralBlockEntity(CpuBlockEntityTypes.SPEAKER) {
    var handle: PeripheralHandle? = null

    override fun getPeripheral(handle: PeripheralHandle?): Peripheral<*> {
        this.handle = handle
        return SpeakerPeripheral(this)
    }

    override fun getDefaultName(): String {
        return "speaker"
    }

    private fun playSound(id: String, volume: Float, pitch: Float) {
        (world as? ServerWorld)?.let { Packets.soundPacket(it, pos, id.id(), volume, pitch, true) }
    }

    class SpeakerPeripheral(wrapped: @NotNull SpeakerBlockEntity): Peripheral<SpeakerBlockEntity>(wrapped) {
        @Synchronized
        fun playSound(id: String, volume: Float, pitch: Float) {
            wrapped?.playSound(id, volume, pitch)
        }
    }
}