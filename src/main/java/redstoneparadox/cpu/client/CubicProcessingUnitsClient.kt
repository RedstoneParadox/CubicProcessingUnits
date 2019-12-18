package redstoneparadox.cpu.client


import io.github.redstoneparadox.oaktree.client.gui.ScreenBuilder
import io.github.redstoneparadox.oaktree.client.gui.control.ButtonControl
import io.github.redstoneparadox.oaktree.client.gui.control.Control
import io.github.redstoneparadox.oaktree.client.gui.control.SplitBoxControl
import io.github.redstoneparadox.oaktree.client.gui.control.TextEditControl
import io.github.redstoneparadox.oaktree.client.gui.style.ColorStyleBox
import io.github.redstoneparadox.oaktree.client.gui.util.ControlAnchor
import io.github.redstoneparadox.oaktree.client.gui.util.RGBAColor
import net.fabricmc.fabric.api.client.screen.ScreenProviderRegistry
import redstoneparadox.cpu.id
import redstoneparadox.cpu.misc.CpuContainer

fun init() {
    ScreenProviderRegistry.INSTANCE.registerFactory("cpu:cpu".id()) { syncID, id, player, buf ->
        ScreenBuilder(cpuGUI())
            .container(CpuContainer(syncID))
            .buildContainerScreen<CpuContainer>()
    }
}


fun cpuGUI(): Control<*> {
    return SplitBoxControl()
        .size(200f, 200f)
        .splitPercent(90f)
        .setVertical(true)
        .anchor(ControlAnchor.CENTER)
        .firstChild(
            TextEditControl()
            .size(180f, 160f)
            .anchor(ControlAnchor.CENTER)
            .maxLines(15).shadow(true)
            .defaultStyle(ColorStyleBox(RGBAColor.black(), RGBAColor(0.7f, 0.7f, 0.7f), 5f))
        )
        .secondChild(
            SplitBoxControl()
                .expand(true)
                .firstChild(
                    ButtonControl()
                        .size(80f, 15f)
                        .anchor(ControlAnchor.CENTER)
                        .defaultStyle(ColorStyleBox(RGBAColor.red()))
                        .heldStyle(ColorStyleBox(RGBAColor(0.7f, 0f, 0f)))
                )
                .secondChild(
                    ButtonControl()
                        .size(80f, 15f)
                        .anchor(ControlAnchor.CENTER)
                        .defaultStyle(ColorStyleBox(RGBAColor.blue()))
                        .heldStyle(ColorStyleBox(RGBAColor(0f, 0f, 0.7f)))
                )
        )
}