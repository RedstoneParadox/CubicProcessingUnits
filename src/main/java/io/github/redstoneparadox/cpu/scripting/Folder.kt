package io.github.redstoneparadox.cpu.scripting

class Folder private constructor(var name: String, private val parent: Folder? = null) {
    private val subfolders: MutableList<Folder> = mutableListOf()
    private val files: MutableList<File<*>> = mutableListOf()

    fun hasParent(): Boolean = parent != null

    fun openParent(): Folder = parent ?: throw Exception()

    fun openSubfolder(name: String): Folder {
        var folder = subfolders.firstOrNull { it.name == name }
        if (folder == null) {
            folder = Folder(name, this)
            subfolders.add(folder)
        }
        return folder
    }

    fun subfolders(): List<Folder> = subfolders.map { it }

    fun hasFile(name: String): Boolean = files.any { "${it.name}.${it.extension}" == name }

    fun getFile(name: String): File<*> {
        var file = files.firstOrNull { "${it.name}.${it.extension}" == name }
        if (file == null) {
            val split = splitAtExtension(name)
            file = File.blank(split.first, split.second)
        }
        return file
    }

    private fun splitAtExtension(name: String): Pair<String, String> {
        var splitPoint = 0;
        for (i in name.indices.reversed()) {
            if (name[i] == '.') splitPoint = i; break
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
        fun createDefaultFileSystem(): Folder {
            val root = Folder("C")
            val document = Document("Test Document", "RedstoneParadox")
            document.addPage("This document exists for the sake of testing out the file system.")

            return root
        }
    }
}