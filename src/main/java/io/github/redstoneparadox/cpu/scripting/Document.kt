package io.github.redstoneparadox.cpu.scripting

import net.minecraft.nbt.CompoundTag
import io.github.redstoneparadox.cpu.api.Cloneable
import net.minecraft.nbt.ListTag
import net.minecraft.nbt.StringTag

class Document(var title: String, var author: String): Cloneable<Document> {
    private val pages: MutableList<String> = mutableListOf()

    fun getPage(index: Int): String {
        return pages[index]
    }

    fun setPage(index: Int, contents: String) {
        pages[index] = contents
    }

    fun addPage(contents: String) {
        pages.add(contents)
    }

    override fun clone(): Document {
        val newDocument = Document(title, author)
        for (value in pages.withIndex()) {
            newDocument.addPage(value.value)
        }
        return newDocument
    }

    fun toNBT(): CompoundTag {
        val nbt = CompoundTag()

        nbt.putString("title", title)
        nbt.putString("author", author)

        val pagesTag = ListTag()
        for (page in pages) {
            val pageTag = StringTag.of(page)
            pagesTag.add(pageTag)
        }
        nbt.put("pages", pagesTag)

        return nbt
    }

    companion object {
        fun fromNBT(nbt: CompoundTag): Document {
            val titleTag = nbt["title"]
            val authorTag = nbt["author"]
            val pagesTag = nbt["pages"]

            if (titleTag is StringTag && authorTag is StringTag && pagesTag is ListTag) {
                val document = Document(titleTag.asString(), authorTag.asString())

                for (pageTag in pagesTag) {
                    if (pageTag is StringTag) {
                        document.addPage(pageTag.asString())
                    }
                }

                return document
            }

            return Document("", "")
        }
    }
}