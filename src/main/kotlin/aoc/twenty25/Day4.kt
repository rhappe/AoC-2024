package aoc.twenty25

import aoc.api.readInput
import model.*
import solution.Solution
import utils.printAnswer
import java.util.concurrent.atomic.AtomicInteger
import kotlin.time.measureTimedValue


fun main() {
    val input = readInput(day = 4, year = 2025)

    val part1Answer = measureTimedValue { Day4(input).part1.solve() }
    part1Answer.printAnswer(label = "Part 1")

    val part2Answer = measureTimedValue { Day4(input).part2.solve() }
    part2Answer.printAnswer(label = "Part 2")
}

class Day4(input: List<String>) {
    private val printingDepartment = PrintingDepartment(
        initialFloor = Grid(input.map { it.toList() }) { _, char ->
            when (char) {
                '@' -> FloorTile.PaperRoll
                else -> FloorTile.Empty
            }
        }
    )

    val part1 = Solution { printingDepartment.removeAccessiblePaperRolls() }

    val part2 = Solution {
        AtomicInteger(printingDepartment.removeAccessiblePaperRolls()).apply {
            do {
                val removedCount = printingDepartment.removeAccessiblePaperRolls().also {
                    addAndGet(it)
                }
            } while (removedCount > 0)
        }.get()
    }

    private class PrintingDepartment(initialFloor: Grid<FloorTile>) {
        private var floor = initialFloor


        /**
         * Removes all the accessible paper rolls and returns how many paper rolls were removed. A paper roll is deemed
         * to be accessible if it has fewer than 4 adjacent tiles that are also occupied by a paper roll.
         */
        fun removeAccessiblePaperRolls(): Int {
            val accessibleTiles = floor.getAccessibleTiles()
            floor = floor.mapGridValues { coordinate, tile ->
                if (coordinate in accessibleTiles) FloorTile.Empty else tile
            }
            return accessibleTiles.size
        }

        companion object {
            /**
             * Gets a list of [IntCoordinate] instances that have an occupied tile that is accessible to a forklift. A tile
             * is deemed accessible if it has fewer than 4 adjacent tiles that are also occupied.
             */
            private fun Grid<FloorTile>.getAccessibleTiles(): List<IntCoordinate> {
                return filterCoordinatesIndexed { coordinate, tile ->
                    if (tile != FloorTile.PaperRoll) {
                        return@filterCoordinatesIndexed false
                    }

                    val adjacentTiles = coordinate inDirections Direction.cardinals
                    adjacentTiles.count { it in this && this[it] == FloorTile.PaperRoll } < 4
                }
            }
        }
    }

    private enum class FloorTile {
        PaperRoll,
        Empty,
    }
}
