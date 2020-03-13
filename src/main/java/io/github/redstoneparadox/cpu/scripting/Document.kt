package io.github.redstoneparadox.cpu.scripting

import io.github.redstoneparadox.cpu.api.Cloneable
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import net.minecraft.nbt.StringTag
import net.minecraft.text.LiteralText
import net.minecraft.text.Text

class Document(var title: String, var author: String): Cloneable<Document> {
    private val pages: MutableList<Text> = mutableListOf()

    fun getPage(index: Int): String {
        return pages[index].asString()
    }

    fun setPage(index: Int, contents: String) {
        pages[index] = LiteralText(contents)
    }

    fun addPage(contents: String) {
        pages.add(LiteralText(contents))
    }

    internal fun addPage(contents: Text) {
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
            val pageTag = StringTag.of(Text.Serializer.toJson(page))
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
                        Text.Serializer.fromJson(pageTag.asString())?.let { document.addPage(it) }
                    }
                }

                return document
            }

            return Document("", "")
        }

        private fun getDesiredString(input: String): String {
            if (!input.startsWith("{\"text\":\"")) return input

            val actualText = input.removePrefix("{\"text\":\"")


            return input
        }
    }

    class DocumentFile(private var document: Document): File<Document>() {
        override var name: String
            get() = document.title
            set(value) { document.title = value }
        override val extension = "txt"

        override fun open(): Document {
            return document.clone()
        }

        override fun save(t: Document) {
            this.document = t
            name = document.title
            dirty = true
        }

        override fun toNBT(): CompoundTag {
            val nbt = CompoundTag()

            nbt.putString("extension", "txt")
            nbt.put("data", document.toNBT())

            return nbt
        }

        companion object {
            fun fromNBT(nbt: CompoundTag): File<*> {
                return DocumentFile(Document.fromNBT(nbt))
            }
        }
    }
}