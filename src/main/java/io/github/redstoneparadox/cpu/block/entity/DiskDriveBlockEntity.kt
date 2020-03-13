package io.github.redstoneparadox.cpu.block.entity

import io.github.redstoneparadox.cpu.api.Peripheral
import io.github.redstoneparadox.cpu.api.PeripheralBlockEntity
import io.github.redstoneparadox.cpu.api.PeripheralHandle
import io.github.redstoneparadox.cpu.item.FloppyDiskItem
import io.github.redstoneparadox.cpu.scripting.Folder
import net.minecraft.item.ItemStack
import net.minecraft.nbt.CompoundTag
import net.minecraft.util.ActionResult
import net.minecraft.util.Tickable
import org.jetbrains.annotations.NotNull

class DiskDriveBlockEntity: PeripheralBlockEntity(CpuBlockEntityTypes.DISK_DRIVE), Tickable {
    private var disk: ItemStack = ItemStack.EMPTY
    private var cachedFileSystem: Folder? = null
    var handle: PeripheralHandle? = null

    override fun getPeripheral(handle: PeripheralHandle): Peripheral<*> {
        this.handle = handle
        return DiskDrivePeripheral(this)
    }

    override fun getDefaultName(): String {
        return "disk drive"
    }

    override fun isConnected(): Boolean {
        return handle != null
    }

    fun isEmpty(): Boolean {
        return disk.isEmpty
    }

    fun insert(disk: ItemStack): Boolean {
        if (!this.disk.isEmpty) return false
        this.disk = disk.split(1)
        cachedFileSystem = if (this.disk.item is FloppyDiskItem) { FloppyDiskItem.createFileSystem(this.disk) } else { null }
        markDirty()
        return true
    }

    fun remove(): ItemStack {
        FloppyDiskItem.toTag(cachedFileSystem!!, disk.orCreateTag)
        cachedFileSystem = null
        markDirty()
        return disk.split(1)
    }

    override fun fromTag(tag: CompoundTag) {
        super.fromTag(tag)
        val diskTag = tag["disk"]
        if (diskTag is CompoundTag) {
            disk = ItemStack.fromTag(diskTag)
            cachedFileSystem = if (disk.item is FloppyDiskItem) { FloppyDiskItem.createFileSystem(disk) } else { null }
        }
    }

    override fun toTag(tag: CompoundTag): CompoundTag {
        super.toTag(tag)
        if (cachedFileSystem != null && disk.item is FloppyDiskItem) {
            FloppyDiskItem.toTag(cachedFileSystem!!, disk.orCreateTag)
            tag.put("disk", disk.toTag(CompoundTag()))
        }
        return tag
    }

    override fun tick() {
        if (cachedFileSystem?.isDirty() == true) {
            cachedFileSystem?.markClean()
            markDirty()
        }
    }

    class DiskDrivePeripheral(wrapped: @NotNull DiskDriveBlockEntity) : Peripheral<DiskDriveBlockEntity>(wrapped) {
        @Synchronized fun hasDisk(): Boolean {
            return wrapped?.disk != null
        }

        @Synchronized fun openFileSystem(): Folder? {
            return wrapped?.cachedFileSystem
        }
    }
}