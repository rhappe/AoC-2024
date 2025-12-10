package aoc.twenty25

import aoc.api.readInput
import model.Coordinate
import model.IntCoordinate
import model.LineSegment
import model.lineTo
import solution.Solution
import utils.printAnswer
import kotlin.math.abs
import kotlin.time.measureTimedValue

fun main() {
    val input = readInput(day = 9, year = 2025)

    val part1Answer = measureTimedValue { Day9(input).part1.solve() }
    part1Answer.printAnswer(label = "Part 1")

    val part2Answer = measureTimedValue { Day9(input).part2.solve() }
    part2Answer.printAnswer(label = "Part 2")
}

class Day9(input: List<String>) {
    private val points = parsePoints(input)
    private val rectangles = parseDistinctRectangles(points)
    private val greenTileLoop = GreenTileLoop(points)

    val part1 = Solution { rectangles.maxOf { it.area } }
    val part2 = Solution { rectangles.sortedByDescending { it.area }.first { it in greenTileLoop }.area }


    private data class Rectangle(
        val origin: IntCoordinate,
        val width: Int,
        val height: Int,
    ) {
        val area: Long by lazy { width.toLong() * height }

        private val topLeft: IntCoordinate = origin
        private val topRight: IntCoordinate = origin.copy(col = origin.col + width - 1)
        private val bottomLeft: IntCoordinate = origin.copy(row = origin.row + height - 1)
        private val bottomRight: IntCoordinate = Coordinate(row = bottomLeft.row, col = topRight.col)

        val lineSegments: List<LineSegment> by lazy {
            listOf(
                topLeft lineTo topRight,
                topRight lineTo bottomRight,
                bottomRight lineTo bottomLeft,
                bottomLeft lineTo topLeft,
            )
        }
    }


    private fun parseDistinctRectangles(points: List<IntCoordinate>): List<Rectangle> {
        val pairs = buildList {
            for (index in points.indices) {
                val current = points[index]
                for (next in points.subList(index, points.size)) {
                    add(current to next)
                }
            }
        }

        return pairs.map { (a, b) ->
            val corners = listOf(a, b) + Coordinate(a.row, b.col) + Coordinate(b.row, a.col)
            Rectangle(
                origin = corners.sortedBy { it.row }.minBy { it.col },
                width = abs(a.col - b.col) + 1,
                height = abs(a.row - b.row) + 1,
            )
        }
    }

    private fun parsePoints(input: List<String>): List<IntCoordinate> {
        return input.map { parts ->
            parts.split(",").let {
                Coordinate(it[1].toInt(), it[0].toInt())
            }
        }
    }

    private class GreenTileLoop(points: List<IntCoordinate>) {
        private val lineSegments: List<LineSegment.Level> = parseLineSegments(points)

        operator fun contains(rectangle: Rectangle): Boolean {
            val horizontalLineSegments = lineSegments.filterIsInstance<LineSegment.Horizontal>()
            val verticalLineSegments = lineSegments.filterIsInstance<LineSegment.Vertical>()

            // get the rectangle line segments, but trim off the endpoints...
            // the endpoints of the rectangle line segments are allowed to touch
            // the line segments that form the green tile loop.
            val rectangleLineSegments = rectangle.lineSegments.filterIsInstance<LineSegment.Level>().map {
                when (it) {
                    is LineSegment.Horizontal -> LineSegment.Horizontal(
                        row = it.row,
                        endCols = (it.endCols.first + 1) to (it.endCols.second - 1),
                    )

                    is LineSegment.Vertical -> LineSegment.Vertical(
                        col = it.col,
                        endRows = (it.endRows.first + 1) to (it.endRows.second - 1),
                    )
                }
            }

            for (rectLineSegment in rectangleLineSegments) {
                val hasIntersection = when (rectLineSegment) {
                    is LineSegment.Horizontal -> verticalLineSegments.any {
                        rectLineSegment intersects it
                    }

                    is LineSegment.Vertical -> horizontalLineSegments.any {
                        rectLineSegment intersects it
                    }
                }

                if (hasIntersection) {
                    return false
                }
            }

            return true
        }

        companion object {
            private fun parseLineSegments(points: List<IntCoordinate>): List<LineSegment.Level> {
                val segments = buildList {
                    for (index in points.indices) {
                        if (index != points.lastIndex) {
                            add(points[index] lineTo points[index + 1])
                        }
                    }

                    add(points.last() lineTo points.first())
                }

                val askewSegments = segments.filterIsInstance<LineSegment.Askew>()
                if (askewSegments.isNotEmpty()) {
                    error("Set of points contained some askew segments: ${askewSegments.map { it.points }}")
                }

                return segments.filterIsInstance<LineSegment.Level>()
            }
        }
    }
}
