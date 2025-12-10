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
        val colsRange: IntRange,
    ) : Level {
        constructor(row: Int, endCols: Pair<Int, Int>) : this(
            row = row,
            colsRange = min(endCols.first, endCols.second)..max(endCols.first, endCols.second),
        )

        override val points: Pair<IntCoordinate, IntCoordinate> by lazy {
            Coordinate(row, colsRange.first) to Coordinate(row, colsRange.last)
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
        val rowsRange: IntRange,
    ) : Level {
        constructor(col: Int, endRows: Pair<Int, Int>) : this(
            col = col,
            rowsRange = min(endRows.first, endRows.second)..max(endRows.first, endRows.second),
        )

        override val points: Pair<IntCoordinate, IntCoordinate> by lazy {
            Coordinate(rowsRange.first, col) to Coordinate(rowsRange.last, col)
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
