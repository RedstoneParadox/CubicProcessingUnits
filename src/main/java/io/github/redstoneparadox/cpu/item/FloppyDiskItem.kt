package io.github.redstoneparadox.cpu.item

import io.github.redstoneparadox.cpu.scripting.Folder
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import net.minecraft.text.Text
import net.minecraft.text.TranslatableText

class FloppyDiskItem: Item(Settings()) {

    override fun getName(): Text {
        return TranslatableText("item.cpu.floppy_disk")
    }

    companion object {
        fun createNBT(nbt: CompoundTag) {
            val fileNBT = CompoundTag()
            fileNBT.putString("name", "A")
            fileNBT.put("folders", ListTag())
            fileNBT.put("files", ListTag())

            nbt.put("filesystem", fileNBT)
        }

        fun createFileSystem(stack: ItemStack): Folder {
            if (!stack.orCreateTag.contains("filesystem")) createNBT(stack.tag!!)
            return Folder.fromNBT(stack.getSubTag("filesystem")!!)
        }

        fun toTag(folder: Folder, nbt: CompoundTag): CompoundTag {
            nbt.put("filesystem", folder.toNBT())
            return nbt
        }
    }
}