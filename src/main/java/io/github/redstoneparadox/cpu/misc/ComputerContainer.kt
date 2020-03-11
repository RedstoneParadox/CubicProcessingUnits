package io.github.redstoneparadox.cpu.misc

import net.minecraft.container.Container
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import io.github.redstoneparadox.cpu.block.entity.ComputerBlockEntity
import io.github.redstoneparadox.cpu.client.networking.ClientPackets
import io.github.redstoneparadox.cpu.networking.Packets

class ComputerContainer(private val world: World, private val pos: BlockPos, syncId: Int) : Container(null, syncId) {
    init {
        if (!world.isClient) Packets.listen(this)
    }

    override fun canUse(player: PlayerEntity): Boolean {
        return true
    }

    fun save(script: String) {
        if (world.isClient) ClientPackets.saveScriptPacket(syncId, script)
        val be = world.getBlockEntity(pos)
        if (be is ComputerBlockEntity) be.save(script)
    }

    fun saveRemote(script: String) {
        if  (world.isClient) return
        world.server?.execute {
            val be = world.getBlockEntity(pos)
            if (be is ComputerBlockEntity) be.save(script)
        }
    }

    fun load(): String {
        val be = world.getBlockEntity(pos)
        if (be is ComputerBlockEntity) return be.load()
        return  ""
    }

    fun run() {
        if (world.isClient) ClientPackets.runScriptPacket(syncId)
    }

    fun runRemote() {
        if  (world.isClient) return
        world.server?.execute {
            val be = world.getBlockEntity(pos)
            if (be is ComputerBlockEntity) be.run()
        }
    }
}