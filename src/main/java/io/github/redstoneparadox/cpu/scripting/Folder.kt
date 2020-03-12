package io.github.redstoneparadox.cpu.scripting

import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import net.minecraft.nbt.StringTag

class Folder private constructor(var name: String, private val parent: Folder? = null) {
    private val folders: MutableList<Folder> = mutableListOf()
    private val files: MutableList<File<*>> = mutableListOf()
    private var dirty = false

    internal fun isDirty(): Boolean {
        return dirty || folders.any { it.isDirty() } || files.any { it.dirty }
    }

    internal fun markClean() {
        dirty = false
        folders.forEach { it.markClean() }
        files.forEach { dirty = false }
    }

    fun hasParent(): Boolean = parent != null

    fun openParent(): Folder = parent ?: throw Exception()

    fun openSubfolder(name: String): Folder {
        var folder = folders.firstOrNull { it.name == name }
        if (folder == null) {
            dirty = true
            folder = Folder(name, this)
            folders.add(folder)
        }
        return folder
    }

    fun subfolders(): List<Folder> = folders.map { it }

    fun hasFile(name: String): Boolean = files.any { "${it.name}.${it.extension}" == name }

    fun getFile(name: String): File<*> {
        var file = files.firstOrNull { "${it.name}.${it.extension}" == name }
        if (file == null) {
            dirty = true
            val split = splitAtExtension(name)
            file = File.blank(split.first, split.second)
            files.add(file)
        }
        return file
    }

    internal fun toNBT(): CompoundTag {
        val nbt = CompoundTag()

        nbt.putString("name", name)

        val foldersNBT = ListTag()
        for (folder in folders) {
            foldersNBT.add(folder.toNBT())
        }
        nbt.put("folders", foldersNBT)

        val filesNBT = ListTag()
        for (file in files) {
            filesNBT.add(file.toNBT())
        }
        nbt.put("files", filesNBT)

        return nbt
    }

    private fun splitAtExtension(name: String): Pair<String, String> {
        var splitPoint = 0
        for (i in name.indices.reversed()) {
            if (name[i] == '.') {
                splitPoint = i
                break
            }
        }
        var actualName = ""
        var extension = ""
        var passedSplit = false
        for (i in name.indices) {
            if (!passedSplit) {
                if (i == splitPoint) passedSplit = true
                else actualName = "$actualName${name[i]}"
            }
            else extension = "$extension${name[i]}"
        }
        return Pair(actualName, extension)
    }

    companion object {
        fun createRootDirectory(): Folder {
            val root = Folder("C")
            val document = Document("Test Document", "RedstoneParadox")
            document.addPage("This document exists for the sake of testing out the file system.")
            root.files.add(Document.DocumentFile(document))
            return root
        }

        fun fromNBT(nbt: CompoundTag, parent: Folder? = null): Folder {
            val nameNBT = nbt["name"]
            val foldersNBT = nbt["folders"]
            val filesNBT = nbt["files"]

            if (nameNBT is StringTag && foldersNBT is ListTag && filesNBT is ListTag) {
                val folder = Folder(nameNBT.asString(), parent)

                for (folderNBT in foldersNBT) {
                    if (folderNBT is CompoundTag) {
                        folder.folders.add(fromNBT(folderNBT, folder))
                    }
                }

                for (fileNBT in filesNBT) {
                    if (fileNBT is CompoundTag) {
                        val extensionNBT = fileNBT["extension"]
                        val dataNBT = fileNBT["data"]

                        if (extensionNBT is StringTag && dataNBT is CompoundTag) {
                            folder.files.add(File.fromNBT(extensionNBT.asString(), dataNBT))
                        }
                    }
                }

                return folder
            }

            throw Exception()
        }
    }
}