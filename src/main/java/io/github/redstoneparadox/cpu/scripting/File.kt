package io.github.redstoneparadox.cpu.scripting

import net.minecraft.nbt.CompoundTag

abstract class File<T> {
    abstract var name: String
    abstract val extension: String
    internal var dirty: Boolean = false

    abstract fun open(): T

    abstract fun save(t: T)

    internal abstract fun toNBT(): CompoundTag

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