package aoc.twenty24

import aoc.api.API_LINE_ITEM_SEPARATOR
import aoc.api.readInput
import kotlin.math.abs

fun main() {
    val input = readInput(day = 1)
    val inputList1 = mutableListOf<Int>()
    val inputList2 = mutableListOf<Int>()

    input.forEach { line ->
        line.split(API_LINE_ITEM_SEPARATOR).also {
            inputList1.add(it.first().toInt())
            inputList2.add(it.last().toInt())
        }
    }

    val part1Answer = part1(
        inputList1 = inputList1,
        inputList2 = inputList2,
    )
    println("Part 1: $part1Answer")

    val part2Answer = part2(
        inputList1 = inputList1,
        inputList2 = inputList2,
    )
    println("Part 2: $part2Answer")
}


private fun part1(inputList1: List<Int>, inputList2: List<Int>): Int {
    return inputList1.sorted().zip(inputList2.sorted()).sumOf { abs(it.second - it.first) }
}

private fun part2(inputList1: List<Int>, inputList2: List<Int>): Int {
    return inputList1.sumOf { line -> inputList2.count { it == line } * line }
}
