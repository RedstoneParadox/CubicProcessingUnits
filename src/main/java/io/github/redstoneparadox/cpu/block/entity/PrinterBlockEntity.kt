package io.github.redstoneparadox.cpu.block.entity

import io.github.redstoneparadox.cpu.api.Peripheral
import io.github.redstoneparadox.cpu.api.PeripheralBlockEntity
import io.github.redstoneparadox.cpu.api.PeripheralHandle
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.inventory.Inventory
import net.minecraft.inventory.SidedInventory
import net.minecraft.item.DyeItem
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.item.WrittenBookItem
import net.minecraft.util.DyeColor
import net.minecraft.util.math.Direction

class PrinterBlockEntity: PeripheralBlockEntity(CpuBlockEntityTypes.PRINTER), SidedInventory {
    var scanStack = ItemStack.EMPTY
    var paperStack = ItemStack.EMPTY
    var inkStack = ItemStack.EMPTY
    var outputStack = ItemStack.EMPTY

    override fun getPeripheral(handle: PeripheralHandle?): Peripheral<*> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getDefaultName(): String {
        return "printer"
    }

    override fun getInvStack(slot: Int): ItemStack {
        return when (slot) {
            0 -> scanStack
            1 -> paperStack
            2 -> inkStack
            3 -> outputStack
            else -> throw IndexOutOfBoundsException("Printer Inventory Size: 3, index: $slot")
        }
    }

    override fun clear() {
        scanStack = ItemStack.EMPTY
        paperStack = ItemStack.EMPTY
        inkStack = ItemStack.EMPTY
        outputStack = ItemStack.EMPTY
    }

    override fun setInvStack(slot: Int, stack: ItemStack) {
        when (slot) {
            0 -> scanStack = stack
            1 -> paperStack = stack
            2 -> inkStack = stack
            3 -> outputStack = stack
            else -> throw IndexOutOfBoundsException("Printer Inventory Size: 4, index: $slot")
        }
    }

    override fun removeInvStack(slot: Int): ItemStack {
        var stack = ItemStack.EMPTY
        when (slot) {
            0 -> {
                stack = scanStack
                scanStack = ItemStack.EMPTY
            }
            1 -> {
                stack = paperStack
                paperStack = ItemStack.EMPTY
            }
            2 -> {
                stack = inkStack
                inkStack = ItemStack.EMPTY
            }
            3 -> {
                stack = outputStack
                outputStack = ItemStack.EMPTY
            }
            else -> throw IndexOutOfBoundsException("Printer Inventory Size: 3, index: $slot")
        }
        return stack
    }

    override fun canPlayerUseInv(player: PlayerEntity): Boolean {
        return true
    }

    override fun getInvAvailableSlots(side: Direction): IntArray {
        return when (side) {
            Direction.UP -> arrayOf(0).toIntArray()
            Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST -> arrayOf(1, 2).toIntArray()
            Direction.DOWN -> arrayOf(3).toIntArray()
        }
    }

    override fun getInvSize(): Int {
        return 4
    }

    override fun canExtractInvStack(slot: Int, stack: ItemStack, dir: Direction): Boolean {
        return slot == 3 && dir == Direction.DOWN
    }

    override fun takeInvStack(slot: Int, amount: Int): ItemStack {
        return when (slot) {
            0 -> scanStack.split(amount)
            1 -> paperStack.split(amount)
            2 -> inkStack.split(amount)
            3 -> outputStack.split(amount)
            else -> throw IndexOutOfBoundsException("Printer Inventory Size: 3, index: $slot")
        }
    }

    override fun isInvEmpty(): Boolean {
        return scanStack.isEmpty && paperStack.isEmpty && inkStack.isEmpty && outputStack.isEmpty
    }

    override fun canInsertInvStack(slot: Int, stack: ItemStack, dir: Direction?): Boolean {
        return when (dir) {
            Direction.UP -> stack.item is WrittenBookItem && slot == 0
            Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST -> {
                val item = stack.item
                (item is DyeItem && slot == 1 && item.color == DyeColor.BLACK) || (item == Items.PAPER && slot == 2)
            }
            else -> false
        }
    }
}