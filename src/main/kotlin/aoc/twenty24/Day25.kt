package aoc.twenty24

import aoc.api.readInput
import solution.Solution
import utils.printAnswer
import kotlin.time.measureTimedValue


fun main() {
    val input = readInput(day = 25)

    val partOneAnswer = measureTimedValue { Day25(input).part1.solve() }
    partOneAnswer.printAnswer(label = "Part 1")

    val partTwoAnswer = measureTimedValue { Day25(input).part2.solve() }
    partTwoAnswer.printAnswer(label = "Part 2")
}


class Day25(input: List<String>) {
    companion object {
        private const val SCHEMATIC_WIDTH = 5
        private const val SCHEMATIC_HEIGHT = 7

        private val emptyRow = ".".repeat(SCHEMATIC_WIDTH)
    }

    val schematics = parseSchematics(input)

    val part1 = Solution {
        getMatchingSchematics(schematics).count()
    }

    val part2 = Solution {

    }

    private fun getMatchingSchematics(schematics: List<Schematic>): List<Pair<Schematic.Key, Schematic.Lock>> {
        val keys = schematics.filterIsInstance<Schematic.Key>()
        val locks = schematics.filterIsInstance<Schematic.Lock>()
        return buildList {
            for (key in keys) {
                for (lock in locks) {
                    if (key fitsIn lock) {
                        add(key to lock)
                    }
                }
            }
        }
    }

    private fun parseSchematics(input: List<String>): List<Schematic> {
        return input.filter { it.isNotEmpty() }.windowed(SCHEMATIC_HEIGHT, SCHEMATIC_HEIGHT).map {
            when (emptyRow) {
                it.first() -> Schematic.Key(heights = parsePinHeights(it))
                it.last() -> Schematic.Lock(heights = parsePinHeights(it.reversed()))
                else -> error("Invalid schematic: $it")
            }
        }
    }

    private fun parsePinHeights(line: List<String>): List<Int> = buildList {
        for (col in line[0].indices) {
            row@ for (row in line.indices) {
                if (line[row][col] == '#') {
                    add(SCHEMATIC_HEIGHT - row - 1)
                    break@row
                }
            }
        }
    }

    sealed interface Schematic {
        data class Key(val heights: List<Int>) : Schematic {
            constructor(vararg heights: Int) : this(heights.toList())

            infix fun fitsIn(lock: Lock): Boolean {
                val diffs = heights.zip(lock.heights) { keyPin, lockPin -> keyPin + lockPin }
                return diffs.all { it <= 5 }
            }
        }

        data class Lock(val heights: List<Int>) : Schematic {
            constructor(vararg heights: Int) : this(heights.toList())
        }
    }
}