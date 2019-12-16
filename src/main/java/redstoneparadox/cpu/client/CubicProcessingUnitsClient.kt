package redstoneparadox.cpu.client


import net.fabricmc.fabric.api.client.screen.ScreenProviderRegistry
import redstoneparadox.cpu.id

fun init() {
    ScreenProviderRegistry.INSTANCE.registerFactory("cpu:cpu".id()) { syncID, id, player, buf ->
        null
    }
}