package puzzles

import api.readInput
import utils.mapValues

fun main() {
    val input = readInput(day = 3)
    println(input)

    val partOneResult = Day03.Part01.scanCorruptedMemory(input)
    println("Part 1: $partOneResult")
}

private object Day03 {
    object Part01 {
        private val MUL_REGEX = "mul\\([0-9]{1,3},[0-9]{1,3}\\)".toRegex()

        fun scanCorruptedMemory(input: List<String>): Int {
            return input.map { parseValues(it) }.flatten().sumOf { it.first * it.second }
        }

        private fun parseValues(input: String): List<Pair<Int, Int>> {
            return MUL_REGEX.findAll(input).toList()
                .map { it.value.substring(4, it.value.length - 1).split(",") }
                .mapValues { it.toInt() }
                .map { it.first() to it.last() }
        }
    }
}