package io.github.redstoneparadox.cpu.client


import io.github.redstoneparadox.cpu.client.networking.ClientPackets
import io.github.redstoneparadox.cpu.id
import io.github.redstoneparadox.cpu.misc.ComputerContainer
import io.github.redstoneparadox.cpu.misc.PrinterContainer
import io.github.redstoneparadox.oaktree.client.gui.ScreenBuilder
import io.github.redstoneparadox.oaktree.client.gui.control.*
import io.github.redstoneparadox.oaktree.client.gui.style.ColorStyleBox
import io.github.redstoneparadox.oaktree.client.gui.style.TextureStyleBox
import io.github.redstoneparadox.oaktree.client.gui.style.Theme
import io.github.redstoneparadox.oaktree.client.gui.util.ControlAnchor
import io.github.redstoneparadox.oaktree.client.gui.util.RGBAColor
import net.fabricmc.fabric.api.client.screen.ScreenProviderRegistry

@Suppress("unused")
fun init() {
    ScreenProviderRegistry.INSTANCE.registerFactory("cpu:computer".id()) { syncID, id, player, buf ->
        ScreenBuilder(computerControlTree())
            .container(
                ComputerContainer(
                    player.world,
                    buf.readBlockPos(),
                    syncID
                )
            )
            .theme(Theme.vanilla())
            .buildContainerScreen<ComputerContainer>()
    }
    ScreenProviderRegistry.INSTANCE.registerFactory("cpu:printer".id()) { syncID, id, player, buf ->
        ScreenBuilder(printerControlTree())
            .container(
                PrinterContainer(
                    player.world,
                    buf.readBlockPos(),
                    player.inventory,
                    syncID
                )
            )
            .theme(Theme.vanilla())
            .buildContainerScreen<ComputerContainer>()
    }
    ClientPackets.registerPackets()
}

fun computerControlTree(): Control<*> {
    val textEdit = TextEditControl()
        .size(280f, 160f)
        .anchor(ControlAnchor.CENTER)
        .maxLines(15).shadow(true)
        .id("text_edit")
    return SplitPanelControl()
        .size(300f, 200f)
        .splitSize(180f)
        .anchor(ControlAnchor.CENTER)
        .verticalSplit(true)
        .id("base")
        .child(
            textEdit
        )
        .child(
            GridPanelControl()
                .rows(1)
                .columns(3)
                .expand(true)
                .child(
                    ButtonControl()
                        .size(50f, 10f)
                        .anchor(ControlAnchor.CENTER)
                        .defaultStyle(ColorStyleBox(RGBAColor.red()))
                        .heldStyle(ColorStyleBox(RGBAColor(0.7f, 0f, 0f)))
                        .onClick { gui, control ->
                            val container = gui.screenContainer
                            if (container.isPresent && container.get() is ComputerContainer) {
                                (container.get() as ComputerContainer).save(textEdit.text)
                            }
                        }
                )
                .child(
                    ButtonControl()
                        .size(50f, 10f)
                        .anchor(ControlAnchor.CENTER)
                        .defaultStyle(ColorStyleBox(RGBAColor.green()))
                        .heldStyle(ColorStyleBox(RGBAColor(0f, 0.7f, 0.0f)))
                        .onClick { gui, control ->
                            val container = gui.screenContainer
                            if (container.isPresent && container.get() is ComputerContainer) {
                                textEdit.text = (container.get() as ComputerContainer).load()
                            }
                        }
                )
                .child(
                    ButtonControl()
                        .size(50f, 10f)
                        .anchor(ControlAnchor.CENTER)
                        .defaultStyle(ColorStyleBox(RGBAColor.blue()))
                        .heldStyle(ColorStyleBox(RGBAColor(0f, 0f, 0.7f)))
                        .onClick { gui, control ->
                            val container = gui.screenContainer
                            if (container.isPresent && container.get() is ComputerContainer) {
                                (container.get() as ComputerContainer).run()
                            }
                        }
                )
        )
}

fun printerControlTree(): Control<*> {
    return SplitPanelControl()
        .id("base")
        .size(176f, 166f)
        .splitSize(77f)
        .verticalSplit(true)
        .anchor(ControlAnchor.CENTER)
        .child(
            // Control()
            SplitPanelControl()
                .size(100f, 40f)
                .splitSize(72f)
                .anchor(ControlAnchor.CENTER)
                .child(
                    GridPanelControl()
                        .expand(true)
                        .rows(2)
                        .columns(3)
                        .child(
                            Control(),
                                    /*
                            LabelControl()
                                .text("copy")
                                .expand(true),

                                     */
                            0, 0
                        )
                        .child(
                            Control(),
                            /*
                            LabelControl()
                                .text("print")
                                .expand(true),

                             */
                            1, 0
                        )
                        .child(
                            Control(),
                            0, 1
                        )
                        .child(
                            ItemSlotControl(36 + 0)
                                .defaultStyle(
                                    TextureStyleBox("oaktree:textures/gui/ui.png")
                                        .drawOrigin(18, 0)
                                        .fileDimensions(256f, 256f)
                                        .textureSize(18, 18)
                                        .scale(1f)
                                )
                                .anchor(ControlAnchor.CENTER)
                                .size(18f, 18f),
                            0, 2
                        )
                        .child(
                            ItemSlotControl(36 + 1)
                                .defaultStyle(
                                    TextureStyleBox("oaktree:textures/gui/ui.png")
                                        .drawOrigin(18, 0)
                                        .fileDimensions(256f, 256f)
                                        .textureSize(18, 18)
                                        .scale(1f)
                                )
                                .anchor(ControlAnchor.CENTER)
                                .size(18f, 18f),
                            1, 1
                        )
                        .child(
                            ItemSlotControl(36 + 2)
                                .defaultStyle(
                                    TextureStyleBox("oaktree:textures/gui/ui.png")
                                        .drawOrigin(18, 0)
                                        .fileDimensions(256f, 256f)
                                        .textureSize(18, 18)
                                        .scale(1f)
                                )
                                .anchor(ControlAnchor.CENTER)
                                .size(18f, 18f),
                            1, 2
                        )
                )
                .child(
                    ItemSlotControl(36 + 3)
                        .defaultStyle(
                            TextureStyleBox("oaktree:textures/gui/ui.png")
                                .drawOrigin(18, 0)
                                .fileDimensions(256f, 256f)
                                .textureSize(18, 18)
                                .scale(1f)
                        )
                        .anchor(ControlAnchor.CENTER)
                        .size(18f, 18f)
                )
        )
        .child(
            SplitPanelControl()
                .size(176f, 83f)
                .splitSize(62f)
                .verticalSplit(true)
                .child(
                    GridPanelControl()
                        .size(9 * 18f, 3 * 18f)
                        .rows(3)
                        .columns(9)
                        .anchor(ControlAnchor.CENTER)
                        .position(0f, -1f)
                        .cells { t1, t2, index ->
                            ItemSlotControl(index + 9)
                                .defaultStyle(
                                    TextureStyleBox("oaktree:textures/gui/ui.png")
                                        .drawOrigin(18, 0)
                                        .fileDimensions(256f, 256f)
                                        .textureSize(18, 18)
                                        .scale(1f)
                                )
                                .anchor(ControlAnchor.CENTER)
                                .size(18f, 18f)
                        }
                )
                .child(
                    GridPanelControl()
                        .size(9 * 18f, 18f)
                        .rows(1)
                        .columns(9)
                        .anchor(ControlAnchor.CENTER)
                        .position(0f, 2f)
                        .cells { t1, t2, index ->
                            ItemSlotControl(index)
                                .defaultStyle(
                                    TextureStyleBox("oaktree:textures/gui/ui.png")
                                        .drawOrigin(18, 0)
                                        .fileDimensions(256f, 256f)
                                        .textureSize(18, 18)
                                        .scale(1f)
                                )
                                .anchor(ControlAnchor.CENTER)
                                .size(18f, 18f)
                        }
                )
        )
}
