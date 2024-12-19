package puzzles

import api.readInput
import input.FakeInput
import model.Coordinate
import model.Direction
import model.IntCoordinate
import utils.WeightedNode
import utils.printAnswer
import kotlin.math.min
import kotlin.time.measureTimedValue

fun main() {
    val input = readInput(day = 18)

    val partOneAnswer = measureTimedValue { Day18.Part01.foo(input) }
    partOneAnswer.printAnswer(label = "Part 1")

}

object Day18 {
    object Part01 {
        fun foo(input: List<String>): Int {
            val memorySpace = MemorySpace(input, height = 70, width = 70)

            memorySpace.dropNext(1024)
            return memorySpace.findPath().size
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

        private val fallenData = mutableListOf<IntCoordinate>()

        fun findPath(
            start: IntCoordinate = IntCoordinate(0, 0),
            end: IntCoordinate = IntCoordinate(height, width),
        ): List<IntCoordinate> {
            val paths = buildList {
                for (row in 0..height) {
                    for (col in 0..width) {
                        val coordinate = Coordinate(row, col)
                        if (coordinate !in fallenData) {
                            add(coordinate)
                        }
                    }
                }
            }
            val result = utils.dijkstra(paths, start) { path ->
                Direction.entries.map { (path + it) to 1 }.toSet()
            }
            return result shortestPathTo end
        }

        fun dropNext(bytes: Int) {
            repeat(min(dataQueue.size, bytes)) {
                fallenData += dataQueue.removeFirst()
            }
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

    fun dijkstra() {

    }
}