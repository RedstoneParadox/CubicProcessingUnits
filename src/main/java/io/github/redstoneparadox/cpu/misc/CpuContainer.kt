package io.github.redstoneparadox.cpu.misc

import net.minecraft.container.Container
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import io.github.redstoneparadox.cpu.block.entity.CpuBlockEntity
import io.github.redstoneparadox.cpu.client.networking.ClientPackets
import io.github.redstoneparadox.cpu.networking.Packets

class CpuContainer(private val world: World, private val pos: BlockPos, syncId: Int) : Container(null, syncId) {
    init {
        if (!world.isClient) Packets.listen(this)
    }

    override fun canUse(player: PlayerEntity): Boolean {
        return true
    }

    fun saveRemote(script: String) {
        if (world.isClient) ClientPackets.saveScriptPacket(syncId, script)
        save(script)
    }

    fun runRemote() {
        if (world.isClient) ClientPackets.runScriptPacket(syncId)
    }

    fun save(script: String) {
        val be = world.getBlockEntity(pos)
        if (be is CpuBlockEntity) be.save(script)
    }

    fun load(): String {
        val be = world.getBlockEntity(pos)
        if (be is CpuBlockEntity) return be.load()
        return  ""
    }

    fun run() {
        if (world.isClient) return
        val be = world.getBlockEntity(pos)
        if (be is CpuBlockEntity) be.run()
    }
}