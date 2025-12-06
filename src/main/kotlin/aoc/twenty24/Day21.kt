package aoc.twenty24

import aoc.api.readInput
import model.Coordinate
import model.Direction
import model.Grid
import model.IntCoordinate
import solution.Solution
import utils.printAnswer
import kotlin.time.measureTimedValue

fun main() {
    val input = readInput(day = 21)

    val partOneAnswer = measureTimedValue { Day21(input).part1.solve() }
    partOneAnswer.printAnswer(label = "Part 1")

    val partTwoAnswer = measureTimedValue { Day21(input).part2.solve() }
    partTwoAnswer.printAnswer(label = "Part 2")
}

class Day21(input: List<String>) {
    private val shortestPathCache = mutableMapOf<Pair<String, Int>, Long>()

    val part1 = Solution {
        input.sumOf { getShortestPathCost(code = it, depth = 2) }
    }

    val part2 = Solution {
        input.sumOf { getShortestPathCost(code = it, depth = 25) }
    }


    private fun getShortestPathCost(code: String, depth: Int): Long {
        return code.filter { it.isDigit() }.toLong() * findShortestPathCost(code, depth)
    }

    private fun findShortestPathCost(
        code: String,
        depth: Int,
        keypad: Keypad = Keypad.Numeric,
    ): Long = shortestPathCache.getOrPut(code to depth) {
        "A$code".zipWithNext().sumOf { transition ->
            keypad[transition.first, transition.second].minOf {
                when (depth) {
                    0 -> it.length.toLong()
                    else -> findShortestPathCost(it, depth - 1, Keypad.Directional)
                }
            }
        }
    }

    sealed class Keypad {
        protected abstract val keypad: Grid<Char>

        operator fun get(start: Char, end: Char): List<String> = buildPathsInDirection(
            current = keypad[start],
            delta = keypad[end] - keypad[start],
            transform = { it + 'A' }, // add 'A' to the end of each path.
        )

        private fun buildPathsInDirection(
            current: IntCoordinate,
            delta: IntCoordinate,
            transform: (String) -> String,
        ): List<String> {
            when {
                // can't navigate over the empty key space
                keypad[current] == ' ' -> return emptyList()
                // base case, nowhere to move, so no directions to add.
                delta == Coordinate(0, 0) -> return listOf(transform(""))
            }

            val vertical = when {
                delta.row < 0 -> buildPathsInDirection(
                    current = current + Direction.North,
                    delta = delta + Direction.South,
                    transform = { "^$it" },
                )

                delta.row > 0 -> buildPathsInDirection(
                    current = current + Direction.South,
                    delta = delta + Direction.North,
                    transform = { "v$it" },
                )

                else -> emptyList()
            }


            val horizontal = when {
                (delta.col < 0) -> buildPathsInDirection(
                    current = current + Direction.West,
                    delta = delta + Direction.East,
                    transform = { "<$it" },
                )

                (delta.col > 0) -> buildPathsInDirection(
                    current = current + Direction.East,
                    delta = delta + Direction.West,
                    transform = { ">$it" },
                )

                else -> emptyList()
            }

            return (horizontal + vertical).map(transform).distinct()
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
