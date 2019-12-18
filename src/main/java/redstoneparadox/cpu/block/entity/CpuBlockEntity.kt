package redstoneparadox.cpu.block.entity

import net.minecraft.block.entity.BlockEntity
import net.minecraft.nbt.CompoundTag

class CpuBlockEntity : BlockEntity(CpuBlockEntityTypes.CPU) {
    private var script: String = ""

    fun save(script: String) {
        this.script = script
        markDirty()
    }

    fun load(): String {
        return script
    }

    override fun fromTag(tag: CompoundTag) {
        if (tag.contains("script")) script = tag.getString("script")
        super.fromTag(tag)
    }

    override fun toTag(tag: CompoundTag): CompoundTag {
        tag.putString("script", script)
        return super.toTag(tag)
    }
}