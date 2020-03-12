package io.github.redstoneparadox.cpu.scripting

import net.minecraft.nbt.CompoundTag

interface File<T> {
    var name: String
    val extension: String

    fun open(): T

    fun save(t: T)

    fun toNBT(): CompoundTag

    companion object {
        private val SUPPLIERS: MutableMap<String, (String) -> File<*>> = mutableMapOf()
        private val DESERIALIZERS: MutableMap<String, (CompoundTag) -> File<*>> = mutableMapOf()

        fun blank(name: String, extension: String): File<*> {
            return SUPPLIERS[extension]?.invoke(name) ?: throw Exception()
        }

        fun fromNBT(extension: String, nbt: CompoundTag): File<*> {
            return DESERIALIZERS[extension]?.invoke(nbt) ?: throw Exception()
        }

        fun addSupplier(extension: String, supplier: (String) -> File<*>) {
            SUPPLIERS[extension] = supplier
        }

        fun addDeserializer(extension: String, deserializer: (CompoundTag) -> File<*>) {
            DESERIALIZERS[extension] = deserializer
        }
    }
}