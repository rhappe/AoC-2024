package puzzles.twenty24

import api.readInput
import kotlin.math.absoluteValue

fun main() {
    val input = readInput(day = 2)

    val partOneResult = Part01.calculateSafeReportCount(input)
    println("Part 1: $partOneResult")

    val partTwoResult = Part02.calculateSafeReportCount(input)
    println("Part 2: $partTwoResult")
}

private object Part01 {
    fun calculateSafeReportCount(input: List<String>): Int {
        return parseInput(input).count { isSafeReport(it) }
    }
}

private object Part02 {
    fun calculateSafeReportCount(input: List<String>): Int {
        return parseInput(input).count { isSafeReport(it, removeDampening = true) }
    }
}

private fun parseInput(input: List<String>): List<List<Int>> {
    return input.map { reportData ->
        reportData.split(" ").map { it.toInt() }
    }
}

private fun isSafeReport(values: List<Int>, removeDampening: Boolean = false): Boolean {
    val differenceValues = buildList {
        for (i in 0 until (values.size - 1)) {
            add(values[i + 1] - values[i])
        }
    }

    // if all the values are positive or all of the values are negative, then all are increasing or decreasing
    // then, if all differ by 1-3, they are safe. Otherwise, unsafe.
    if (differenceValues.all { it > 0 } || differenceValues.all { it < 0 }) {
        if (differenceValues.all { it.absoluteValue in 1..3 }) {
            return true
        }
    }

    // if dampening should be removed, we will re-calculate the safety for the report for each value removed.
    if (removeDampening) {
        for (index in values.indices) {
            val dampenedValues = values.toMutableList().apply { removeAt(index) }
            if (isSafeReport(values = dampenedValues, removeDampening = false)) {
                return true
            }
        }
    }

    // if we got here, the report is unsafe.
    return false
}
