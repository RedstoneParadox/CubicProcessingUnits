package io.github.redstoneparadox.cpu.scripting

import net.minecraft.nbt.CompoundTag
import io.github.redstoneparadox.cpu.api.Cloneable
import net.minecraft.nbt.ListTag
import net.minecraft.nbt.StringTag

class Document(var name: String, var author: String): Cloneable<Document> {
    private val pages: MutableList<String> = mutableListOf()

    fun getPage(index: Int): String {
        return pages[index]
    }

    fun setPage(index: Int, contents: String) {
        pages[index] = contents
    }

    override fun clone(): Document {
        val newDocument = Document(name, author)
        for (value in pages.withIndex()) {
            newDocument.setPage(value.index, value.value)
        }
        return newDocument
    }

    fun toNBT(): CompoundTag {
        val nbt = CompoundTag()

        nbt.putString("name", name)
        nbt.putString("author", author)

        val pagesTag = ListTag()
        for (page in pages) {
            val pageTag = CompoundTag()
            pageTag.putString("text", page)
            pagesTag.add(pageTag)
        }
        nbt.put("pages", pagesTag)

        return nbt
    }

    companion object {
        fun fromNBT(nbt: CompoundTag): Document {
            val nameTag = nbt["name"]
            val authorTag = nbt["author"]
            val pagesTag = nbt["pages"]

            if (nameTag is StringTag && authorTag is StringTag && pagesTag is ListTag) {
                val document = Document(nameTag.asString(), authorTag.asString())

                var index = 0;
                for (pageTag in pagesTag) {
                    if (pageTag is CompoundTag) {
                        val textTag = pageTag["text"]
                        if (textTag is StringTag) {
                            document.setPage(index, textTag.asString())
                            index += 1
                        }
                    }
                }

                return document
            }

            return Document("", "")
        }
    }
}