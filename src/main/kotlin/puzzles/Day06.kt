package puzzles

import api.readInput
import kotlinx.coroutines.*
import model.Coordinate
import model.Direction
import model.Grid
import model.IntCoordinate
import utils.formatSeconds
import utils.printAnswer
import java.util.concurrent.atomic.AtomicInteger
import kotlin.time.measureTimedValue

fun main() = runBlocking {
    val input = readInput(day = 6)

    val partOneAnswer = measureTimedValue { Day06.Part01.getDistinctCoordinatesCount(input) }
    partOneAnswer.printAnswer(label = "Part 1")

    val partTwoAnswer = measureTimedValue { Day06.Part02.getInfiniteLoopCount(input) }
    partTwoAnswer.printAnswer(label = "Part 2")

    val totalTime = partOneAnswer.duration + partTwoAnswer.duration
    println("Total elapsed time: ${totalTime.formatSeconds()}")
}

private object Day06 {
    object Part01 {
        fun getDistinctCoordinatesCount(input: List<String>): Int {
            val vectors = getVisitedPositionVectors(Grid(input))
            return vectors.distinctBy { it.coordinate }.count()
        }
    }

    object Part02 {
        fun getInfiniteLoopCount(input: List<String>): Int {
            val count = AtomicInteger()
            val grid = Grid(input)
            runBlocking(Dispatchers.Default) {
                for (row in grid.indices) {
                    for (col in grid.indices) {
                        if (grid[row][col] == OBSTACLE || grid[row][col] in FacingDirection.characters) {
                            continue
                        }

                        launch {
                            try {
                                getVisitedPositionVectors(grid, Coordinate(row, col))
                            } catch (error: InfiniteLoopException) {
                                count.incrementAndGet()
                            }
                        }
                    }
                }
            }
            return count.get()
        }
    }

    /**
     * Gets the visited position vectors by moving the existing character around the grid and recording the coordinates
     * visited along with the facing direction (i.e. coordinates + direction = vector).
     *
     * Infinite loops are detected if we attempt to record the same position vector twice. In this case, an
     * [InfiniteLoopException] will be thrown.
     *
     * The reason we record vectors instead of just the visited coordinate is mainly for infinite loop detection. The
     * character can visit the same coordinate twice, but if they visit the same coordinate with the same facing
     * direction multiple times, then they are just continuing along a path that they already followed and will forever
     * continue to do so.
     */
    @Throws(InfiniteLoopException::class)
    private fun getVisitedPositionVectors(
        grid: Grid<Char>,
        vararg additionalObstacles: IntCoordinate,
    ): Set<Vector> {
        var characterPosition = getInitialCharacterPosition(grid)
        var finished = false
        return buildSet {
            while (!finished) {
                if (characterPosition in this) {
                    throw InfiniteLoopException()
                }
                add(characterPosition)
                val nextCoordinate = characterPosition.coordinate + characterPosition.direction
                if (nextCoordinate !in grid) {
                    finished = true
                } else if (grid[nextCoordinate] == OBSTACLE || nextCoordinate in additionalObstacles) {
                    characterPosition = characterPosition.rotated()
                } else {
                    characterPosition = characterPosition.copy(coordinate = nextCoordinate)
                }
            }
        }
    }

    private fun getInitialCharacterPosition(grid: List<List<Char>>): Vector {
        for (row in grid.indices) {
            for (col in grid[0].indices) {
                if (grid[row][col] in FacingDirection.characters) {
                    return Vector(
                        coordinate = Coordinate(
                            row = row,
                            col = col,
                        ),
                        direction = FacingDirection.fromCharacter(
                            character = grid[row][col],
                        ),
                    )
                }
            }
        }

        error("Could not find character starting position in grid.")
    }

    private operator fun <T> List<List<T>>.get(coordinate: IntCoordinate): T {
        return this[coordinate.row][coordinate.col]
    }

    private operator fun List<List<Any>>.contains(coordinate: IntCoordinate): Boolean {
        return coordinate.row in indices && coordinate.col in this[0].indices
    }

    private const val OBSTACLE = '#'

    object FacingDirection {
        private val Direction.character: Char
            get() = when (this) {
                Direction.North -> '^'
                Direction.East -> '>'
                Direction.South -> 'v'
                Direction.West -> '<'
            }

        val characters = Direction.entries.map { it.character }.toSet()

        fun fromCharacter(character: Char) = Direction.entries.first { it.character == character }
    }

    private data class Vector(val coordinate: IntCoordinate, val direction: Direction) {
        fun rotated(): Vector {
            return copy(direction = direction.rotateClockwise())
        }
    }

    private class InfiniteLoopException : Error("Infinite loop detected!")
}