package puzzles.twenty24

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
        fun foo(input: String): Long {
            val blocks = parseToBlocks(input)
            val refragmented = refragment(blocks)
            return calculateChecksum(refragmented)
        }

        private fun refragment(blocks: List<DataBlock>): List<DataBlock> {
            val allFileBlocks = blocks.filterIsInstance<DataBlock.File>()
            val queue = ArrayDeque(blocks.filterIsInstance<DataBlock.File>())
            return buildList {
                for (index in allFileBlocks.indices) {
                    when (val block = blocks[index]) {
                        is DataBlock.File -> add(block)
                        DataBlock.FreeSpace -> add(queue.removeLast())
                    }
                }

                while (size < blocks.size) {
                    add(DataBlock.FreeSpace)
                }
            }
        }
    }

    object Part02 {
        fun foo(input: String): Long {
            val chunks = parseToChunks(input)
            val compacted = compact(chunks)
            val blocks = convertToBlocks(compacted)
            return calculateChecksum(blocks)
        }

        private fun compact(chunks: List<DataChunk>): List<DataChunk> {
            val newChunks = chunks.toMutableList()
            val reversedFiles = chunks.filterIsInstance<DataChunk.File>().reversed()
            reversedFiles.forEach { file ->
                val firstFreeIndex = newChunks.indexOfFirst { it is DataChunk.FreeSpace && file.size <= it.size }
                val dataIndex = newChunks.indexOfLast { it == file }
                if (firstFreeIndex in newChunks.indices && firstFreeIndex < dataIndex) {
                    val freeChunk = newChunks[firstFreeIndex]
                    newChunks[firstFreeIndex] = file
                    newChunks[dataIndex] = DataChunk.FreeSpace(file.size)
                    val remainingSpace = freeChunk.size - file.size
                    if (remainingSpace > 0) {
                        newChunks.add(firstFreeIndex + 1, DataChunk.FreeSpace(remainingSpace))
                    }
                }
            }
            return newChunks
        }
    }

    private fun parseToBlocks(input: String): List<DataBlock> {
        val chunks = parseToChunks(input)
        return convertToBlocks(chunks)
    }

    private fun parseToChunks(input: String): List<DataChunk> = input.mapIndexed { index, digit ->
        when {
            index % 2 == 0 -> DataChunk.File(id = index / 2, size = digit.digitToInt())
            else -> DataChunk.FreeSpace(size = digit.digitToInt())
        }
    }

    private fun convertToBlocks(chunks: List<DataChunk>): List<DataBlock> = buildList {
        chunks.forEach { chunk ->
            when (chunk) {
                is DataChunk.File -> {
                    repeat(chunk.size) {
                        add(DataBlock.File(id = chunk.id))
                    }
                }

                is DataChunk.FreeSpace -> {
                    repeat(chunk.size) {
                        add(DataBlock.FreeSpace)
                    }
                }
            }
        }
    }

    private fun calculateChecksum(blocks: List<DataBlock>): Long {
        val blockValues = blocks.mapIndexed { index, block ->
            when (block) {
                is DataBlock.File -> index * block.id
                DataBlock.FreeSpace -> 0
            }
        }
        return blockValues.sumOf { it.toLong() }
    }

    sealed interface DataChunk {
        val size: Int

        data class File(val id: Int, override val size: Int) : DataChunk
        data class FreeSpace(override val size: Int) : DataChunk
    }

    sealed interface DataBlock {
        data object FreeSpace : DataBlock

        data class File(val id: Int) : DataBlock
    }
}