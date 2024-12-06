package puzzles

import api.readInput

fun main() {
    val input = readInput(day = 6)

    val partOneAnswer = Day06.Part01.getDistinctCoordinatesCount(input)
    println("Part 1: $partOneAnswer")

    val partTwoAnswer = Day06.Part02.getInfiniteLoopCount(input)
    println("Part 2: $partTwoAnswer")
}

private object Day06 {
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

    private class InfiniteLoopException : Error("Infinite loop detected!")

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
            var count = 0
            val grid = input.map { it.toMutableList() }.toMutableList()
            for (row in grid.indices) {
                for (col in grid.indices) {
                    if (grid[row][col] == OBSTACLE || grid[row][col] in FacingDirection.characters) {
                        continue
                    }
                    val original = grid[row][col]
                    grid[row][col] = '#'

                    try {
                        getVisitedPositionVectors(grid)
                    } catch (error: InfiniteLoopException) {
                        count++
                    }

                    // revert back to the original value.
                    grid[row][col] = original
                }
            }
            return count
        }
    }

    private fun getVisitedPositionVectors(grid: List<List<Char>>): Set<Vector> {
        var characterPosition = getInitialCharacterPosition(grid)
        var finished = false
        return buildSet {
            while (!finished) {
                if (characterPosition in this) {
                    throw InfiniteLoopException()
                }
                add(characterPosition)
                val nextCoordinate = characterPosition.coordinate + characterPosition.direction
                if (nextCoordinate.row !in grid.indices || nextCoordinate.col !in grid[0].indices) {
                    finished = true
                } else if (grid[nextCoordinate] == OBSTACLE) {
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

    private data class Vector(
        val coordinate: Coordinate,
        val direction: FacingDirection,
    ) {
        fun rotated(): Vector {
            return copy(direction = direction.rotateClockwise())
        }
    }

    private data class Coordinate(
        val row: Int,
        val col: Int,
    ) {
        operator fun plus(facingDirection: FacingDirection): Coordinate = when (facingDirection) {
            FacingDirection.NORTH -> copy(row = row - 1)
            FacingDirection.EAST -> copy(col = col + 1)
            FacingDirection.SOUTH -> copy(row = row + 1)
            FacingDirection.WEST -> copy(col = col - 1)
        }
    }
}