package redstoneparadox.cpu.misc

import net.minecraft.container.Container
import net.minecraft.entity.player.PlayerEntity

class CpuContainer(syncId: Int) : Container(null, syncId) {
    override fun canUse(player: PlayerEntity): Boolean {
        return true
    }
}