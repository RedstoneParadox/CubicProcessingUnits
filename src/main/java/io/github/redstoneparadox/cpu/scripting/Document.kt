package io.github.redstoneparadox.cpu.scripting

import io.github.redstoneparadox.cpu.api.Cloneable

class Document(var name: String): Cloneable<Document> {
    private val pages: MutableList<String> = mutableListOf()

    fun getPage(index: Int): String {
        return pages[index]
    }

    fun setPage(index: Int, contents: String) {
        pages[index] = contents
    }

    override fun clone(): Document {
        val newBook = Document(name)
        for (value in pages.withIndex()) {
            newBook.setPage(value.index, value.value)
        }
        return newBook
    }
}