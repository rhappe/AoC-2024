package aoc.twenty25

import aoc.api.readInput
import model.Direction
import model.Grid
import model.IntCoordinate
import solution.Solution
import utils.printAnswer
import kotlin.time.measureTimedValue

fun main() {
    val input = readInput(day = 7, year = 2025)

    input.forEach { println(it) }

    val part1Answer = measureTimedValue { Day7(input).part1.solve() }
    part1Answer.printAnswer(label = "Part 1")

    val part2Answer = measureTimedValue { Day7(input).part2.solve() }
    part2Answer.printAnswer(label = "Part 2")
}

class Day7(input: List<String>) {
    val part1 = Solution { TachyonGrid(input).computeSplitsCount() }
    val part2 = Solution { TachyonGrid(input).computeQuantumRoutes() }

    private class TachyonGrid(input: List<String>) {
        private val grid = Grid(input)
        private val startPosition = grid[START_KEY]

        private val cache = mutableMapOf<IntCoordinate, Long>()

        fun computeQuantumRoutes() = computeQuantumRoutes(
            position = startPosition,
        )

        private fun computeQuantumRoutes(position: IntCoordinate): Long {
            val next = position + Direction.South
            if (next !in grid) {
                return 1 // reached the end of a single path, so add one to the count
            }

            return cache.getOrPut(next) {
                if (grid[next] == '^') {
                    computeQuantumRoutes(next + Direction.West) + computeQuantumRoutes(next + Direction.East)
                } else {
                    computeQuantumRoutes(next)
                }
            }
        }

        fun computeSplitsCount(): Int {
            var count = 0
            var beamPositions = listOf(startPosition)
            do {
                val checkPositions = beamPositions
                    .map { it + Direction.South }
                    .filter { it in grid }
                val next = buildList {
                    for (position in checkPositions) {
                        if (grid[position] == '^') {
                            add(position + Direction.West)
                            add(position + Direction.East)
                        } else {
                            add(position)
                        }
                    }
                }

                if (next.isNotEmpty()) {
                    count += next.size - beamPositions.size
                }
                beamPositions = next.distinct().filter { it in grid }
            } while (beamPositions.isNotEmpty())
            return count
        }

        companion object {
            private const val START_KEY = 'S'
        }
    }
}
