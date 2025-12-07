package aoc.twenty15

import aoc.api.readInput
import solution.Solution
import utils.printAnswer
import kotlin.time.measureTimedValue


fun main() {
    val input = readInput(day = 10, year = 2015).first()

    val partOneAnswer = measureTimedValue { Day10(input).part1.solve() }
    partOneAnswer.printAnswer(label = "Part 1")

    val partTwoAnswer = measureTimedValue { Day10(input).part2.solve() }
    partTwoAnswer.printAnswer(label = "Part 2")
}

class Day10(input: String) {
    private val digitCountRegex = "(.)\\1*".toRegex()

    val part1 = Solution { solve(input, depth = 40) }
    val part2 = Solution { solve(input, depth = 50) }

    private fun solve(input: String, depth: Int): Int {
        var result = input
        repeat(depth) {
            val matches = digitCountRegex.findAll(result)
            val groups = matches.map { it.groups }.toList()
            result = groups.fold("") { acc, group ->
                val count = requireNotNull(group[0]).value.length
                val character = requireNotNull(group[1]).value
                acc + "$count$character"
            }
        }
        return result.length
    }
}
