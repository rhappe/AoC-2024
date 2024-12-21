package model

import kotlin.math.absoluteValue

typealias IntCoordinate = Coordinate.Integers
typealias LongCoordinate = Coordinate.Longs

sealed interface Coordinate<V : Number> {
    val row: V
    val col: V

    operator fun plus(facingDirection: Direction): Coordinate<V>
    operator fun minus(other: Coordinate<V>): Coordinate<V>
    operator fun plus(other: Coordinate<V>): Coordinate<V>

    /**
     * Calculates the manhattan distance:
     * (x1 - x2).absoluteValue + (y1 - y2).absoluteValue
     */
    infix fun distanceTo(other: Coordinate<V>): V

    data class Integers(override val row: Int, override val col: Int) : Coordinate<Int> {
        override fun plus(facingDirection: Direction): IntCoordinate = when (facingDirection) {
            Direction.North -> copy(row = row - 1)
            Direction.East -> copy(col = col + 1)
            Direction.South -> copy(row = row + 1)
            Direction.West -> copy(col = col - 1)
        }

        override operator fun minus(other: Coordinate<Int>): IntCoordinate {
            return Integers(
                row = row - other.row,
                col = col - other.col,
            )
        }

        override operator fun plus(other: Coordinate<Int>): IntCoordinate {
            return Integers(
                row = row + other.row,
                col = col + other.col,
            )
        }

        override fun distanceTo(other: Coordinate<Int>): Int {
            return (this - other).let { it.row.absoluteValue + it.col.absoluteValue }
        }
    }

    data class Longs(override val row: Long, override val col: Long) : Coordinate<Long> {
        override fun plus(facingDirection: Direction): LongCoordinate = when (facingDirection) {
            Direction.North -> copy(row = row - 1)
            Direction.East -> copy(col = col + 1)
            Direction.South -> copy(row = row + 1)
            Direction.West -> copy(col = col - 1)
        }

        override operator fun minus(other: Coordinate<Long>): LongCoordinate {
            return Longs(
                row = row - other.row,
                col = col - other.col,
            )
        }

        override operator fun plus(other: Coordinate<Long>): LongCoordinate {
            return Longs(
                row = row + other.row,
                col = col + other.col,
            )
        }

        // abs(x1-x2) + abs(y1-y2)
        override fun distanceTo(other: Coordinate<Long>): Long {
            return (this - other).let { it.row.absoluteValue + it.col.absoluteValue }
        }
    }
}

val <V : Number> Coordinate<V>.x: V
    get() = this.col

val <V : Number> Coordinate<V>.y: V
    get() = this.row

fun Coordinate(row: Int, col: Int) = Coordinate.Integers(row, col)

fun Coordinate(row: Long, col: Long) = Coordinate.Longs(row, col)

inline infix fun <V : Number, reified T : Coordinate<V>> T.inDirections(
    directions: Collection<Direction>
): Collection<T> = directions.map { (this + it) as T }
