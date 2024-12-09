package puzzles

import api.readInput
import kotlin.time.DurationUnit
import kotlin.time.measureTimedValue

fun main() {
    val input = readInput(day = 9).single()

    val partOneAnswer = measureTimedValue { Day09.Part01.foo(input) }
    println("Part 1: ${partOneAnswer.value}; Duration: ${partOneAnswer.duration.toString(DurationUnit.SECONDS, 4)}")

    val partTwoAnswer = measureTimedValue { Day09.Part02.foo(input) }
    println("Part 1: ${partTwoAnswer.value}; Duration: ${partTwoAnswer.duration.toString(DurationUnit.SECONDS, 4)}")
}

private object Day09 {
    object Part01 {
        fun foo(input: String): Long = Disk(input).refragmented().checksum
    }

    object Part02 {
        fun foo(input: String): Long {
            TODO()
        }
    }

    data class Disk(val blocks: List<DataBlock>) {
        constructor(diskMap: String) : this(
            blocks = buildList {
                diskMap.forEachIndexed { index, digit ->
                    val block = when {
                        index % 2 == 0 -> DataBlock.File(id = index / 2)
                        else -> DataBlock.FreeSpace
                    }

                    repeat(digit.digitToInt()) {
                        add(block)
                    }
                }
            },
        )

        override fun toString(): String = blocks.joinToString(separator = "") {
            when (it) {
                is DataBlock.File -> it.id.toString()
                DataBlock.FreeSpace -> "."
            }
        }

        fun refragmented(): Disk {
            val allFileBlocks = blocks.filterIsInstance<DataBlock.File>()
            val queue = ArrayDeque(blocks.filterIsInstance<DataBlock.File>())
            return Disk(
                blocks = buildList {
                    for (index in allFileBlocks.indices) {
                        when (val block = blocks[index]) {
                            is DataBlock.File -> add(block)
                            DataBlock.FreeSpace -> add(queue.removeLast())
                        }
                    }

                    while (size < blocks.size) {
                        add(DataBlock.FreeSpace)
                    }
                },
            )
        }

        val checksum: Long by lazy {
            val blockValues = blocks.mapIndexed { index, block ->
                when (block) {
                    is DataBlock.File -> index * block.id
                    DataBlock.FreeSpace -> 0
                }
            }
            blockValues.sumOf { it.toLong() }
        }
    }

    sealed interface DataBlock {
        data object FreeSpace : DataBlock

        data class File(val id: Int) : DataBlock
    }
}