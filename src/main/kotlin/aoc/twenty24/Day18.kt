package aoc.twenty24

import api.readInput
import model.Coordinate
import model.Direction
import model.IntCoordinate
import utils.printAnswer
import kotlin.math.min
import kotlin.time.measureTimedValue

fun main() {
    val input = readInput(day = 18)

    val partOneAnswer = measureTimedValue { Day18.Part01.foo(input) }
    partOneAnswer.printAnswer(label = "Part 1")

    val partTwoAnswer = measureTimedValue { Day18.Part02.foo(input) }
    partTwoAnswer.printAnswer(label = "Part 2")

}

object Day18 {
    object Part01 {
        fun foo(input: List<String>): Int {
            val memorySpace = MemorySpace(input, height = 70, width = 70)
            memorySpace.dropNext(1024)
            return memorySpace.findPath().orEmpty().size
        }
    }

    object Part02 {
        fun foo(input: List<String>): IntCoordinate? {
            val firstBlockingByte = binarySearchFirstDroppedBlocker(
                memorySpace = MemorySpace(
                    input = input,
                    height = 70,
                    width = 70,
                ),
            )
            return firstBlockingByte
        }

        private fun binarySearchFirstDroppedBlocker(
            memorySpace: MemorySpace,
            range: IntRange = (0..memorySpace.dataQueue.size),
        ): IntCoordinate? {
            if (range.last < range.first) {
                return null
            }
            val center = (range.last + range.first) / 2
            if (center == 0) {
                return null
            }
            val copy = memorySpace.copy(dataQueue = ArrayDeque(memorySpace.dataQueue))
            val lastDropped = copy.dropNext(center) ?: run {
                error("Nothing was dropped...")
            }
            val result = copy.findPath()

            val candidates = if (result == null) {
                listOfNotNull(
                    lastDropped,
                    binarySearchFirstDroppedBlocker(
                        memorySpace = memorySpace,
                        // a blockage was not found, so we need to drop more bytes to find the blocked path,
                        // so shift the range towards the upper limit of the range.
                        range = (range.first until center),
                    )
                )
            } else {
                listOfNotNull(
                    binarySearchFirstDroppedBlocker(
                        memorySpace = memorySpace,// a blockage was found, but we need to see if we can find where the first blockage
                        // would occur, which might happen if we drop fewer bytes. So shift the range toward
                        // the lower limit of the range.
                        range = (center + 1..range.last),
                    )
                )

            }

            return candidates.minByOrNull { memorySpace.dataQueue.indexOf(it) }
        }
    }

    private data class MemorySpace(
        val dataQueue: ArrayDeque<IntCoordinate>,
        val height: Int,
        val width: Int,
    ) {
        constructor(input: List<String>, height: Int, width: Int) : this(
            dataQueue = ArrayDeque(
                elements = input.map { line ->
                    line.split(",").map { it.toInt() }.let { (col, row) ->
                        Coordinate(row, col)
                    }
                }
            ),
            height = height,
            width = width,
        )

        private val emptyData: List<IntCoordinate>
            get() = buildList {
                for (row in 0..height) {
                    for (col in 0..width) {
                        val coordinate = Coordinate(row, col)
                        if (coordinate !in fallenData) {
                            add(coordinate)
                        }
                    }
                }
            }

        private val fallenData = mutableListOf<IntCoordinate>()

        fun findPath(
            start: IntCoordinate = IntCoordinate(0, 0),
            end: IntCoordinate = IntCoordinate(height, width),
        ): List<IntCoordinate>? {
            val paths = emptyData
            val result = utils.dijkstra(paths, start) { path ->
                Direction.cardinals.primaries.map { (path + it) to 1 }.toSet()
            }
            return result shortestPathTo end
        }

        fun dropNext(bytes: Int = 1): IntCoordinate? {
            repeat(min(dataQueue.size, bytes)) {
                fallenData += dataQueue.removeFirst()
            }
            return fallenData.lastOrNull()
        }

        override fun toString(): String = buildString {
            for (row in 0..height) {
                for (col in 0..width) {
                    when (Coordinate(row, col)) {
                        in fallenData -> append("#")
                        IntCoordinate(0, 0) -> append("S")
                        IntCoordinate(height, width) -> append("E")
                        else -> append(".")
                    }
                }
                appendLine()
            }
        }
    }

    private fun printGrid(data: List<IntCoordinate>, height: Int, width: Int) {
        for (row in 0..height) {
            for (col in 0..width) {
                when (Coordinate(row, col)) {
                    in data -> print("#")
                    else -> print(".")
                }
            }
            println()
        }
    }
}