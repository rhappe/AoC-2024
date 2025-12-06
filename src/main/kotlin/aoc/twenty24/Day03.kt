package aoc.twenty24

import api.readInput
import utils.mapValues

fun main() {
    val input = readInput(day = 3).joinToString(separator = "")

    val partOneResult = Day03.Part01.scanCorruptedMemory(input)
    println("Part 1: $partOneResult")

    val partTwoResult = Day03.Part02.scanCorruptedMemory(input)
    println("Part 2: $partTwoResult")
}

private object Day03 {
    object Part01 {
        fun scanCorruptedMemory(input: String): Int {
            return parseValues(input).sumOf { it.first * it.second }
        }
    }

    object Part02 {
        private const val DO_INSTRUCTION = "do()"
        private const val DONT_INSTRUCTION = "don't()"

        fun scanCorruptedMemory(input: String): Int = Part01.scanCorruptedMemory(
            input = filterBadInstructions(input),
        )

        private fun filterBadInstructions(input: String): String = buildString {
            var currentIndex = 0
            var dontIndex = input.indexOf(DONT_INSTRUCTION)

            while (currentIndex in input.indices && dontIndex in input.indices) {
                append(input.substring(currentIndex, dontIndex))
                // set the current index to the next do() instruction after the latest don't() index
                currentIndex = input.indexOf(DO_INSTRUCTION, startIndex = dontIndex)
                // and set the dontIndex to the next don't() instruction after the current index.
                dontIndex = input.indexOf(DONT_INSTRUCTION, startIndex = currentIndex)
            }

            if (currentIndex in input.indices) {
                append(input.substring(currentIndex))
            }
        }
    }


    private val MUL_REGEX = "mul\\([0-9]{1,3},[0-9]{1,3}\\)".toRegex()

    private fun parseValues(input: String): List<Pair<Int, Int>> {
        return MUL_REGEX.findAll(input).toList()
            .map { it.value.substring(4, it.value.length - 1).split(",") }
            .mapValues { it.toInt() }
            .map { it.first() to it.last() }
    }
}