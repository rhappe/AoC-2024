package model

data class Coordinate(
    val row: Int,
    val col: Int,
) {
    operator fun plus(facingDirection: Direction) = when (facingDirection) {
        Direction.North -> copy(row = row - 1)
        Direction.East -> copy(col = col + 1)
        Direction.South -> copy(row = row + 1)
        Direction.West -> copy(col = col - 1)
    }

    operator fun minus(other: Coordinate): Coordinate {
        return Coordinate(
            row = row - other.row,
            col = col - other.col,
        )
    }

    operator fun plus(other: Coordinate): Coordinate {
        return Coordinate(
            row = row + other.row,
            col = col + other.col,
        )
    }
}
