package model

import kotlin.math.max
import kotlin.math.min

sealed interface LineSegment {
    val points: Pair<IntCoordinate, IntCoordinate>

    sealed interface Level : LineSegment {
        infix fun intersects(other: Level): Boolean
    }

    data class Horizontal(
        val row: Int,
        val endCols: Pair<Int, Int>,
    ) : Level {
        internal val colsRange: IntRange by lazy {
            min(endCols.first, endCols.second)..max(endCols.first, endCols.second)
        }

        override val points: Pair<IntCoordinate, IntCoordinate> by lazy {
            Coordinate(row, endCols.first) to Coordinate(row, endCols.second)
        }

        override infix fun intersects(other: Level): Boolean {
            return when (other) {
                is Horizontal -> row == other.row && colsRange.intersect(other.colsRange).isNotEmpty()
                is Vertical -> row in other.rowsRange && other.col in colsRange
            }
        }
    }

    data class Vertical(
        val col: Int,
        val endRows: Pair<Int, Int>,
    ) : Level {
        internal val rowsRange: IntRange by lazy {
            min(endRows.first, endRows.second)..max(endRows.first, endRows.second)
        }

        override val points: Pair<IntCoordinate, IntCoordinate> by lazy {
            Coordinate(endRows.first, col) to Coordinate(endRows.second, col)
        }

        override infix fun intersects(other: Level): Boolean {
            return when (other) {
                is Horizontal -> col in other.colsRange && other.row in rowsRange
                is Vertical -> col == other.col && rowsRange.intersect(other.rowsRange).isNotEmpty()
            }
        }
    }

    data class Askew(
        override val points: Pair<IntCoordinate, IntCoordinate>,
    ) : LineSegment
}
