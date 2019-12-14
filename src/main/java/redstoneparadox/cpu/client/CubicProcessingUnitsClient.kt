package redstoneparadox.cpu.client

import io.github.cottonmc.cotton.gui.client.CottonInventoryScreen
import net.fabricmc.fabric.api.client.screen.ScreenProviderRegistry
import net.minecraft.container.BlockContext
import redstoneparadox.cpu.id
import redstoneparadox.cpu.misc.CpuScreenController

fun init() {
    ScreenProviderRegistry.INSTANCE.registerFactory("cpu:cpu".id()) { syncID, id, player, buf ->
        CottonInventoryScreen<CpuScreenController>(
            CpuScreenController(syncID, player.inventory, BlockContext.create(player.world, buf.readBlockPos())),
            player
        )
    }
}