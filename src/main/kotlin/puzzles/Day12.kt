package puzzles

import api.readInput
import model.Coordinate
import model.Direction
import model.Grid
import utils.mapValues
import utils.runTimedMeasurement
import kotlin.time.DurationUnit
import kotlin.time.measureTimedValue

fun main() {
    val input = readInput(day = 12)

    runTimedMeasurement("Part 1") { Day12.Part01.calculateFenceCost(input) }
    runTimedMeasurement("Part 2") { Day12.Part02.calculateFenceCost(input) }
    runTimedMeasurement("Part 2 w/ corners") { Day12.Part02ButUsingCorners.calculateFenceCost(input) }
}

private object Day12 {
    object Part01 {
        fun calculateFenceCost(input: List<String>): Int {
            val grid = Grid(input)
            val garden = Garden(grid)
            return garden.costByFenceCount
        }
    }

    object Part02 {
        fun calculateFenceCost(input: List<String>): Int {
            val grid = Grid(input)
            val garden = Garden(grid)
            return garden.costBySide
        }
    }

    object Part02ButUsingCorners {
        fun calculateFenceCost(input: List<String>): Int {
            val grid = Grid(input)
            val garden = Garden(grid)
            return garden.costBySideUsingCorners
        }
    }

    private data class Plot(
        val position: Coordinate,
        val fences: List<Direction>,
    ) {
        fun getCornerCount(region: Region): Int {
            val cornerChecks = listOf(
                isCorner(region, Direction.North, Direction.East),
                isCorner(region, Direction.South, Direction.East),
                isCorner(region, Direction.South, Direction.West),
                isCorner(region, Direction.North, Direction.West),
            )
            return cornerChecks.count { it }
        }

        private fun isCorner(region: Region, lateral: Direction, longitudinal: Direction): Boolean {
            val diagonalCheck = position + lateral + longitudinal
            val lateralCheck = position + lateral
            val longitudinalCheck = position + longitudinal

            val results = listOf(diagonalCheck, lateralCheck, longitudinalCheck).map { it in region }

            return when (results) {
                // if none of the checks are in the region, then this is a corner.
                listOf(false, false, false) -> true
                // if only the "unidirectional" checks are in the region, this is a corner.
                listOf(false, true, true) -> true
                // if only the "diagonal" cell is in the region, this is a corner.
                listOf(true, false, false) -> true
                // otherwise, this is not a corner.
                else -> false
            }
        }
    }

    private data class Region(
        val type: Char,
        val plots: List<Plot>,
    ) {
        operator fun contains(position: Coordinate) = position in plots.map { it.position }

        val costByFenceCount: Int = plots.size * plots.sumOf { it.fences.count() }

        val costBySide: Int by lazy {
            val sideCounts = listOf(
                countSides(Direction.North),
                countSides(Direction.East),
                countSides(Direction.South),
                countSides(Direction.West),
            )
            plots.size * sideCounts.sum()
        }

        /**
         * counts the number of sides in a given facing direction, for example:
         *   EEEEE
         *   EXXXX
         *   EEEEE
         *   EXXXX
         *   EEEEE
         *
         * The E region above has:
         *   - 3 sides to the north,
         *   - 6 sides to the east,
         *   - 3 sides to the south, and
         *   - 1 side to the west
         */
        private fun countSides(direction: Direction): Int {
            val plotsWithFence = plots.filter { direction in it.fences }
            val groups = when (direction) {
                Direction.North, Direction.South -> plotsWithFence
                    .groupBy { it.position.row }.values
                    .mapValues { it.position.col }
                    .map { it.sorted() }

                Direction.East, Direction.West -> plotsWithFence
                    .groupBy { it.position.col }.values
                    .mapValues { it.position.row }
                    .map { it.sorted() }
            }

            // calculate the distance between each of the plots for the given row or column
            // if distance = 1, then they are right next to each other. If distance > 1, then
            // they are separated by a plot of a different type of plant.
            // For example: AAABAACCA will have differences of 1, 1, 2, 1, 3
            // We will count a distinct "side" every time there is a distance > 1 (a "gap"), plus 1.
            // So the above example has 2 "gaps", plus a constant 1 = 3 distinct "sides."
            // Another example: AAAAAAAA has 0 "gaps", plus a constant 1 = 1 distinct "side."
            val differences = groups.map { group ->
                val differences = buildList {
                    for (index in group.lastIndex downTo 1) {
                        add(group[index] - group[index - 1])
                    }
                }
                differences.count { it > 1 } + 1
            }

            return differences.sum()
        }

        val costBySideUsingCorners: Int by lazy {
            plots.size * plots.sumOf { it.getCornerCount(this) }
        }
    }

    private data class Garden(val regions: List<Region>) {
        val costByFenceCount: Int = regions.sumOf { it.costByFenceCount }

        val costBySide: Int by lazy {
            regions.sumOf { it.costBySide }
        }

        val costBySideUsingCorners: Int by lazy {
            regions.sumOf { it.costBySideUsingCorners }
        }

        operator fun plus(region: Region): Garden = Garden(
            regions = regions + region,
        )
    }

    private fun Garden(grid: Grid<Char>) = GardenBuilder(grid).buildGarden()

    private class GardenBuilder(private val grid: Grid<Char>) {
        private val traversedPositions = mutableSetOf<Coordinate>()

        fun buildGarden(): Garden = Garden(
            regions = buildList {
                grid.forEachIndexed { position, type ->
                    val plots = buildRegionPlots(type, position)
                    if (plots.isNotEmpty()) {
                        this += Region(
                            type = type,
                            plots = plots,
                        )
                    }
                }
            },
        )

        private fun buildRegionPlots(type: Char, position: Coordinate): List<Plot> {
            if (position in traversedPositions || position !in grid || grid[position] != type) {
                return emptyList()
            }

            traversedPositions += position

            val currentPlot = Plot(
                position = position,
                fences = Direction.entries.filter { position + it !in grid || grid[position + it] != type },
            )

            val adjacent = Direction.entries
                .filter { it !in currentPlot.fences }
                .flatMap { buildRegionPlots(type, position + it) }

            return adjacent + currentPlot
        }
    }
}
