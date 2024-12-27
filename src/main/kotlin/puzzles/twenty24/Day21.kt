package puzzles.twenty24

import api.readInput
import model.Coordinate
import model.Grid
import model.IntCoordinate
import solution.Solution
import utils.printAnswer
import java.security.KeyPair
import kotlin.math.absoluteValue
import kotlin.time.measureTimedValue

fun main() {
    val input = readInput(day = 21)

    val partOneAnswer = measureTimedValue { Day21(input).part1.solve() }
    partOneAnswer.printAnswer(label = "Part 1")

    val partTwoAnswer = measureTimedValue { Day21(input).part2.solve() }
    partTwoAnswer.printAnswer(label = "Part 2")
}

class Day21(input: List<String>) {
    val part1 = Solution {
        input.sumOf { getShortestPathCost(code = it, depth = 2) }
    }

    val part2 = Solution {
        input.sumOf { getShortestPathCost(code = it, depth = 25) }
    }

    private val shortestPathCache = mutableMapOf<Pair<String, Int>, Long>()
    private val transitionsCache = mutableMapOf<Pair<Char, Char>, String>()

    private fun getShortestPathCost(code: String, depth: Int): Long {
        val shortestPathLength = findShortestPathCost(code, depth)
        return code.filter { it.isDigit() }.toLong() * shortestPathLength
    }

    private fun findShortestPathCost(
        code: String,
        depth: Int,
        keypad: Keypad = Keypad.Numeric,
    ): Long = shortestPathCache.getOrPut(code to depth) {
        "A$code".zipWithNext().sumOf { transition ->
            val shortestPath = transitionsCache.getOrPut(transition) {
                keypad[transition.first, transition.second]
            }

            when (depth) {
                0 -> shortestPath.length.toLong()
                else -> findShortestPathCost(shortestPath, depth - 1, Keypad.Directional)
            }
        }
    }

    sealed class Keypad {
        protected abstract val keypad: Grid<Char>

        operator fun get(from: Char, to: Char): String = buildString {
            val fromNode = keypad[from]
            val toNode = keypad[to]

            val (rowDelta, colDelta) = toNode - fromNode
            val horizontal: String = if (colDelta <= 0) {
                "<".repeat(colDelta.absoluteValue)
            } else {
                ">".repeat(colDelta.absoluteValue)
            }
            val vertical: String = if (rowDelta <= 0) {
                "^".repeat(rowDelta.absoluteValue)
            } else {
                "v".repeat(rowDelta.absoluteValue)
            }

            if (keypad[fromNode + Coordinate(rowDelta, 0)] == ' ') {
                append(horizontal + vertical)
            } else if (keypad[fromNode + Coordinate(0, colDelta)] == ' ') {
                append(vertical + horizontal)
            } else {
                append(
                    (horizontal + vertical).toList().sortedBy {
                        "<v>^".indexOf(it)
                    }.joinToString(separator = "")
                )
            }

            append('A')
        }

        data object Numeric : Keypad() {
            override val keypad = Grid(
                strings = listOf(
                    "789",
                    "456",
                    "123",
                    " 0A",
                ),
            )


        }

        data object Directional : Keypad() {
            override val keypad = Grid(
                strings = listOf(
                    " ^A",
                    "<v>"
                ),
            )
        }
    }
}
