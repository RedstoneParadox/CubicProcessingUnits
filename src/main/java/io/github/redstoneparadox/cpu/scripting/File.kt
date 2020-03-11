package io.github.redstoneparadox.cpu.scripting

import io.github.redstoneparadox.cpu.api.Cloneable

interface File<T> {
    var name: String
    val extension: String

    fun open(): T

    fun save(t: T)

    companion object {
        private val SUPPLIERS: MutableMap<String, (String) -> File<*>> = mutableMapOf()

        fun blank(name: String, extension: String): File<*> {
            return SUPPLIERS[extension]?.invoke(name) ?: throw Exception()
        }

        fun addSupplier(extension: String, supplier: (String) -> File<*>) {
            SUPPLIERS[extension] = supplier
        }
    }
}