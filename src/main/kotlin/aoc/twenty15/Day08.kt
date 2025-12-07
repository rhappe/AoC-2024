package aoc.twenty15

import aoc.api.readInput
import solution.Solution
import utils.printAnswer
import kotlin.time.measureTimedValue


fun main() {
    val input = readInput(day = 8, year = 2015)

    val partOneAnswer = measureTimedValue { Day08(input).part1.solve() }
    partOneAnswer.printAnswer(label = "Part 1")

    val partTwoAnswer = measureTimedValue { Day08(input).part2.solve() }
    partTwoAnswer.printAnswer(label = "Part 2")
}

class Day08(input: List<String>) {
    val part1 = Solution {
        val totalEncodedLength = input.sumOf { it.encodedLength }
        val totalDecodedLength = input.sumOf { it.decodedLength }
        totalEncodedLength - totalDecodedLength
    }

    val part2 = Solution {
        val encodedInput = input.map { encode(it) }
        val originalEncodedLength = input.sumOf { it.encodedLength }
        val totalEncodedLength = encodedInput.sumOf { it.encodedLength }
        totalEncodedLength - originalEncodedLength
    }

    private val String.encodedLength: Int
        get() = length

    private val encodingRegex = "(\\\\x[a-f0-9]{2}|\\\\\"|\\\\\\\\|.)".toRegex()

    private val String.decodedLength: Int
        get() = encodingRegex.findAll(this).count { it.value != "\"" }

    private fun encode(string: String): String {
        val encoded = buildString {
            for (char in string) {
                if (char == '\\') {
                    append("\\\\")
                } else if (char == '"') {
                    append("\\\"")
                } else {
                    append(char)
                }
            }
        }

        return "\"$encoded\""
    }
}
