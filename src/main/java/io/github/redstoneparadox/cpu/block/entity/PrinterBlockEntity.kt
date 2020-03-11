package io.github.redstoneparadox.cpu.block.entity

import io.github.redstoneparadox.cpu.api.Peripheral
import io.github.redstoneparadox.cpu.api.PeripheralBlockEntity
import io.github.redstoneparadox.cpu.api.PeripheralHandle
import io.github.redstoneparadox.cpu.item.CpuItems
import io.github.redstoneparadox.cpu.scripting.ChannelFuture
import io.github.redstoneparadox.cpu.scripting.Document
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.runBlocking
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.inventory.Inventories
import net.minecraft.inventory.SidedInventory
import net.minecraft.item.DyeItem
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.item.WrittenBookItem
import net.minecraft.nbt.CompoundTag
import net.minecraft.util.DefaultedList
import net.minecraft.util.DyeColor
import net.minecraft.util.Tickable
import net.minecraft.util.math.Direction
import org.jetbrains.annotations.NotNull
import java.util.*
import java.util.concurrent.Future

class PrinterBlockEntity: PeripheralBlockEntity(CpuBlockEntityTypes.REDSTONE_ADAPTER), SidedInventory, Tickable {
    var handle: PeripheralHandle? = null

    var scanStack = ItemStack.EMPTY
    var paperStack = ItemStack.EMPTY
    var inkStack = ItemStack.EMPTY
    var outputStack = ItemStack.EMPTY

    private val tasks: Queue<Task> = ArrayDeque()
    private var currentTask: Task? = null
    private var timeRemaining = 0

    override fun getPeripheral(handle: PeripheralHandle): Peripheral<*> {
        this.handle = handle
        return PrinterPeripheral(this)
    }

    override fun getDefaultName(): String {
        return "printer"
    }

    override fun tick() {
        val task = currentTask
        if (task != null) {
            if (timeRemaining == 0) {
                if (outputStack.isEmpty) {
                    if (task is Task.Print) {
                        val printed = ItemStack(CpuItems.PRINTED_DOCUMENT)
                        printed.tag = task.document.toNBT()
                        printed.tag?.putInt("generation", 1)
                        setInvStack(3, printed)
                        currentTask = null
                    }
                    else if (task is Task.Scan) {
                        val scanned = scanStack.item
                        if (scanned is WrittenBookItem) {
                            val tag = scanStack.tag
                            val document = Document.fromNBT(tag!!)
                            runBlocking { task.sender.send(document) }
                            currentTask = null
                            setInvStack(3, scanStack)
                            removeInvStack(0)
                        }
                    }
                }
            }
            else {
                if (task is Task.Scan && scanStack.item !is WrittenBookItem) {
                    currentTask = null
                    task.sender.cancel()
                }

                timeRemaining -= 1
            }
        }
        else if (tasks.isNotEmpty()) {
            val task = tasks.peek()

            if (task is Task.Print && paperStack.item == Items.PAPER && inkStack.item == Items.BLACK_DYE) {
                takeInvStack(1, 1)
                takeInvStack(2, 1)
                currentTask = tasks.remove()
                timeRemaining = 60
            }
            else if (task is Task.Scan) {
                if (scanStack.item !is WrittenBookItem) {
                    task.sender.cancel()
                    tasks.remove()
                }
                else {
                    currentTask = tasks.remove()
                    timeRemaining = 60
                }
            }
        }
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

    override fun fromTag(tag: CompoundTag) {
        super.fromTag(tag)
        val list = DefaultedList.ofSize(4, ItemStack.EMPTY)
        Inventories.fromTag(tag, list)
        scanStack = list[0]
        paperStack = list[1]
        inkStack = list[2]
        outputStack = list[3]
    }

    override fun toTag(tag: CompoundTag): CompoundTag {
        super.toTag(tag)
        val list = DefaultedList.copyOf(ItemStack.EMPTY, scanStack, paperStack, inkStack, outputStack)
        Inventories.toTag(tag, list, true)
        return tag
    }

    fun scan(): Future<Document> {
        val channel = Channel<Document>(Channel.CONFLATED)
        tasks.add(Task.Scan(channel))
        return ChannelFuture(channel)
    }

    fun print(document: Document) {
        tasks.add(Task.Print(document))
    }

    sealed class Task {
        class Scan(val sender: Channel<Document>): Task()
        class Print(val document: Document): Task()
    }

    class PrinterPeripheral(wrapped: @NotNull PrinterBlockEntity) : Peripheral<PrinterBlockEntity>(wrapped) {
        @Synchronized
        fun scan(): Future<Document> {
            if (wrapped != null) return wrapped!!.scan()

            throw Exception()
        }

        @Synchronized
        fun print(document: Document) {
            wrapped?.print(document)
        }
    }
}