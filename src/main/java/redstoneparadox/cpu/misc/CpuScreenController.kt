package redstoneparadox.cpu.misc

import io.github.cottonmc.cotton.gui.CottonCraftingController
import io.github.cottonmc.cotton.gui.widget.WGridPanel
import io.github.cottonmc.cotton.gui.widget.WTextField
import net.minecraft.container.BlockContext
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.recipe.RecipeType

class CpuScreenController(syncId: Int, playerInventory: PlayerInventory, context: BlockContext): CottonCraftingController(RecipeType.SMELTING, syncId, playerInventory, getBlockInventory(context), getBlockPropertyDelegate(context)) {

    init {
        val root = (rootPanel as WGridPanel)
        val textField = WTextField()
        textField.setSize(50, 20)

        root.add(WTextField(), 10, 10)

        rootPanel.validate(this)
    }
}