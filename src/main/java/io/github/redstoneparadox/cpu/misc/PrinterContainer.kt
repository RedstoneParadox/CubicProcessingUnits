package io.github.redstoneparadox.cpu.misc

import io.github.redstoneparadox.cpu.block.entity.PrinterBlockEntity
import io.github.redstoneparadox.cpu.networking.Packets
import io.github.redstoneparadox.oaktree.networking.OakTreeNetworking
import net.minecraft.container.Container
import net.minecraft.container.Slot
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.inventory.Inventory
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.item.WrittenBookItem
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

class PrinterContainer(private val world: World, private val pos: BlockPos, val playerInventory: PlayerInventory, syncId: Int): Container(null, syncId) {
    private val inventory: Inventory

    init {
        if (!world.isClient) {
            Packets.listen(this)
            OakTreeNetworking.addContainerForSyncing(this)
        }
        val be = world.getBlockEntity(pos)
        if (be !is PrinterBlockEntity) throw Exception()
        checkContainerSize(be, 4)
        inventory = be

        for (i in 0..35) {
            addSlot(Slot(playerInventory, i, 0, 0))
        }

        addSlot(PredicateSlot(inventory, 0, 0, 0) { it.item is WrittenBookItem })
        addSlot(PredicateSlot(inventory, 1, 0, 0) { it.item == Items.PAPER })
        addSlot(PredicateSlot(inventory, 2, 0, 0) { it.item == Items.BLACK_DYE})
        addSlot(OutputSlot(inventory, 3, 0, 0))
    }

    override fun canUse(player: PlayerEntity?): Boolean {
        return true
    }

    class PredicateSlot(inventory: Inventory, invSlot: Int, xPosition: Int, yPosition: Int, val predicate: (ItemStack) -> Boolean): Slot(inventory, invSlot, xPosition, yPosition) {
        override fun canInsert(stack: ItemStack): Boolean {
            return predicate(stack)
        }
    }

    class OutputSlot(inventory: Inventory?, invSlot: Int, xPosition: Int, yPosition: Int): Slot(inventory, invSlot, xPosition, yPosition) {
        override fun canInsert(stack: ItemStack?): Boolean {
            return false
        }
    }
}