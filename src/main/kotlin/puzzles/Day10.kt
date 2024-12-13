package puzzles

import api.readInput
import model.Coordinate
import model.IntCoordinate
import kotlin.time.DurationUnit
import kotlin.time.measureTimedValue

fun main() {
    val input = readInput(day = 10)

    val partOneAnswer = measureTimedValue { Day10.Part01.countDistinctTrails(input) }
    println("Part 1: ${partOneAnswer.value}; Duration: ${partOneAnswer.duration.toString(DurationUnit.SECONDS, 4)}")

    val partTwoAnswer = measureTimedValue { Day10.Part02.countAllTrails(input) }
    println("Part 1: ${partTwoAnswer.value}; Duration: ${partTwoAnswer.duration.toString(DurationUnit.SECONDS, 4)}")
}

private object Day10 {
    object Part01 {
        fun countDistinctTrails(input: List<String>): Int {
            val trails = buildList {
                for (row in input.indices) {
                    for (col in input[0].indices) {
                        addAll(getDistinctTrails(input, row, col))
                    }
                }
            }
            return trails.distinct().count()
        }
    }

    private fun getDistinctTrails(input: List<String>, row: Int, col: Int): List<Trail> {
        val endpoints = getDistinctEndpoints(input, row, col, level = 0)
        return endpoints.map { endpoint ->
            Trail(
                start = Coordinate(row, col),
                end = endpoint,
            )
        }
    }

    private fun getDistinctEndpoints(input: List<String>, row: Int, col: Int, level: Int): List<IntCoordinate> {
        val value = input[row][col].digitToIntOrNull() ?: return emptyList()
        if (value != level) {
            return emptyList()
        } else if (value == 9) {
            return listOf(Coordinate(row, col))
        }

        val nextPositions = listOf(
            row + 1 to col,
            row - 1 to col,
            row to col + 1,
            row to col - 1,
        ).filter {
            it.first in input.indices && it.second in input[0].indices
        }

        return nextPositions.flatMap { getDistinctEndpoints(input, it.first, it.second, level + 1) }
    }

    object Part02 {
        fun countAllTrails(input: List<String>): Int {
            val trails = buildList {
                for (row in input.indices) {
                    for (col in input[0].indices) {
                        addAll(getDistinctTrails(input, row, col))
                    }
                }
            }
            return trails.count()
        }
    }

    private data class Trail(
        val start: IntCoordinate,
        val end: IntCoordinate,
    )
}
