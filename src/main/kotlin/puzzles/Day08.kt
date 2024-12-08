package puzzles

import api.readInput
import kotlin.time.DurationUnit
import kotlin.time.measureTimedValue

fun main() {
    val input = readInput(day = 8)

    val partOneAnswer = measureTimedValue { Day08.Part01.countAntiNodes(input) }
    println("Part 1: ${partOneAnswer.value}; Duration: ${partOneAnswer.duration.toString(DurationUnit.SECONDS, 4)}")

    val partTwoAnswer = measureTimedValue { Day08.Part02.countAntiNodes(input) }
    println("Part 1: ${partTwoAnswer.value}; Duration: ${partTwoAnswer.duration.toString(DurationUnit.SECONDS, 4)}")
}

private object Day08 {
    object Part01 {
        fun countAntiNodes(input: List<String>): Int {
            val grid = parseGrid(input)
            val nodesByFrequency = grid.nodes.groupBy { it.frequency }
            val found = mutableSetOf<Point>()
            var sum = 0
            nodesByFrequency.values.forEach { nodes ->
                for (index in nodes.indices) {
                    for (checkIndex in index + 1..nodes.lastIndex) {
                        val currentNodes = nodes[index] to nodes[checkIndex]
                        val checkPositions = getAntiNodePositions(
                            grid = grid,
                            nodes = currentNodes,
                            distance = 1
                        )
                        val antiNodePositions = checkPositions.filter {
                            it != currentNodes.first.position && it != currentNodes.second.position && it !in found
                        }
                        sum += antiNodePositions.count()
                        found += antiNodePositions
                    }
                }
            }
            return sum
        }
    }

    object Part02 {
        fun countAntiNodes(input: List<String>): Int {
            val grid = parseGrid(input)
            val nodesByFrequency = grid.nodes.groupBy { it.frequency }
            val found = mutableSetOf<Point>()
            var sum = 0
            nodesByFrequency.values.forEach { nodes ->
                for (index in nodes.indices) {
                    for (checkIndex in index + 1..nodes.lastIndex) {
                        val antiNodePositions = getAntiNodePositions(
                            grid = grid,
                            nodes = nodes[index] to nodes[checkIndex],
                        )

                        sum += antiNodePositions.count { it !in found }
                        found += antiNodePositions
                    }
                }
            }
            return sum
        }
    }

    private fun getAntiNodePositions(
        grid: Grid,
        nodes: Pair<Node, Node>,
        distance: Int = Int.MAX_VALUE,
    ): List<Point> = buildList {
        var distanceCounter = 0
        val delta = nodes.second.position - nodes.first.position
        var checkPosition = nodes.first.position
        while (checkPosition in grid && distanceCounter < distance) {
            add(checkPosition)
            if (checkPosition != nodes.first.position && checkPosition != nodes.second.position) {
                distanceCounter++
            }
            checkPosition += delta
        }

        distanceCounter = 0
        checkPosition = nodes.first.position - delta
        while (checkPosition in grid && distanceCounter < distance) {
            add(checkPosition)
            if (checkPosition != nodes.first.position && checkPosition != nodes.second.position) {
                distanceCounter++
            }
            checkPosition -= delta
        }
    }

    private fun parseGrid(input: List<String>): Grid = Grid(
        width = input[0].length,
        height = input.size,
        nodes = buildList {
            for (row in input.indices) {
                for (col in input[0].indices) {
                    if (input[row][col] != '.') {
                        this += Node(
                            frequency = input[row][col],
                            position = Point(row, col),
                        )
                    }
                }
            }
        },
    )

    private data class Grid(
        val width: Int,
        val height: Int,
        val nodes: List<Node>,
    ) {
        operator fun contains(point: Point): Boolean {
            return point.row in 0 until height && point.col in 0 until width
        }
    }

    private data class Node(
        val frequency: Char,
        val position: Point,
    )

    private data class Point(
        val row: Int,
        val col: Int,
    ) {
        operator fun minus(other: Point): Point {
            return Point(
                row = row - other.row,
                col = col - other.col,
            )
        }

        operator fun plus(other: Point): Point {
            return Point(
                row = row + other.row,
                col = col + other.col,
            )
        }
    }
}
