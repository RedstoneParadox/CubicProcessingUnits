package redstoneparadox.cpu.client

import io.github.cottonmc.cotton.gui.client.CottonInventoryScreen
import net.minecraft.entity.player.PlayerEntity
import redstoneparadox.cpu.misc.CpuScreenController

class CpuScreen(container: CpuScreenController, player: PlayerEntity) : CottonInventoryScreen<CpuScreenController>(container, player) {
}