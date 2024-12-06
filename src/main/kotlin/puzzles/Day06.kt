package puzzles

import api.readInput
import kotlinx.coroutines.*
import utils.formatSeconds
import utils.printAnswer
import java.util.concurrent.atomic.AtomicInteger
import kotlin.time.measureTimedValue

fun main() = runBlocking {
    val input = readInput(day = 6)

    val partOneAnswer = measureTimedValue { Day06.Part01.getDistinctCoordinatesCount(input) }
    partOneAnswer.printAnswer(part = 1)

    val partTwoAnswer = measureTimedValue { Day06.Part02.getInfiniteLoopCount(input) }
    partTwoAnswer.printAnswer(part = 2)

    val totalTime = partOneAnswer.duration + partTwoAnswer.duration
    println("Total elapsed time: ${totalTime.formatSeconds()}")
}

private object Day06 {
    object Part01 {
        fun getDistinctCoordinatesCount(input: List<String>): Int {
            val vectors = getVisitedPositionVectors(
                grid = input.map { it.toList() },
            )
            return vectors.distinctBy { it.coordinate }.count()
        }
    }

    object Part02 {
        fun getInfiniteLoopCount(input: List<String>): Int {
            val count = AtomicInteger()
            val grid = input.map { it.toList() }
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

    private fun CoroutineScope.launchGridCalculation(grid: List<List<Char>>, block: (List<List<Char>>) -> Unit) {
        launch {
            block(grid)
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
        grid: List<List<Char>>,
        vararg additionalObstacles: Coordinate,
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

    private operator fun <T> List<List<T>>.get(coordinate: Coordinate): T {
        return this[coordinate.row][coordinate.col]
    }

    private operator fun List<List<Any>>.contains(coordinate: Coordinate): Boolean {
        return coordinate.row in indices && coordinate.col in this[0].indices
    }

    private const val OBSTACLE = '#'

    private enum class FacingDirection(val character: Char) {
        NORTH('^'),
        EAST('>'),
        SOUTH('v'),
        WEST('<'),
        ;

        fun rotateClockwise(): FacingDirection = when (this) {
            NORTH -> EAST
            EAST -> SOUTH
            SOUTH -> WEST
            WEST -> NORTH
        }

        companion object {
            val characters = entries.map { it.character }.toSet()
            fun fromCharacter(character: Char) = entries.first { it.character == character }
        }
    }

    private data class Vector(val coordinate: Coordinate, val direction: FacingDirection) {
        fun rotated(): Vector {
            return copy(direction = direction.rotateClockwise())
        }
    }

    private data class Coordinate(val row: Int, val col: Int) {
        operator fun plus(facingDirection: FacingDirection): Coordinate = when (facingDirection) {
            FacingDirection.NORTH -> copy(row = row - 1)
            FacingDirection.EAST -> copy(col = col + 1)
            FacingDirection.SOUTH -> copy(row = row + 1)
            FacingDirection.WEST -> copy(col = col - 1)
        }
    }

    private class InfiniteLoopException : Error("Infinite loop detected!")
}