package aoc.twenty24

import api.readInput
import model.Coordinate
import model.IntCoordinate
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
            val found = mutableSetOf<IntCoordinate>()
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
            val found = mutableSetOf<IntCoordinate>()
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
    ): List<IntCoordinate> = buildList {
        var distanceCounter = 0
        val delta = nodes.second.position - nodes.first.position
        addAll(
            generateSequence(nodes.first.position) { it + delta }
                .takeWhile { it in grid && distanceCounter < distance }
                .onEach { if (it != nodes.first.position && it != nodes.second.position) distanceCounter++ }
        )

        distanceCounter = 0
        addAll(
            generateSequence(nodes.first.position - delta) { it - delta }
                .takeWhile { it in grid && distanceCounter < distance }
                .onEach { if (it != nodes.first.position && it != nodes.second.position) distanceCounter++ }
        )
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
                            position = Coordinate(row, col),
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
        operator fun contains(point: IntCoordinate): Boolean {
            return point.row in 0 until height && point.col in 0 until width
        }
    }

    private data class Node(
        val frequency: Char,
        val position: IntCoordinate,
    )
}
