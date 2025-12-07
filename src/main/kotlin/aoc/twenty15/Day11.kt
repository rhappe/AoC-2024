package aoc.twenty15

import aoc.api.readInput
import solution.Solution
import utils.printAnswer
import kotlin.time.measureTimedValue


fun main() {
    val input = readInput(day = 11, year = 2015).first()

    val partOneAnswer = measureTimedValue { Day11(input).part1.solve() }
    partOneAnswer.printAnswer(label = "Part 1")

    val partTwoAnswer = measureTimedValue { Day11(input).part2.solve() }
    partTwoAnswer.printAnswer(label = "Part 2")
}

class Day11(input: String) {
    val part1 = Solution { findNextValidPassword(input) }
    val part2 = Solution { findNextValidPassword(findNextValidPassword(input)) }

    private fun findNextValidPassword(password: String): String {
        var current = incrementPassword(password)
        while (!isValidPassword(current)) {
            current = incrementPassword(current)
        }
        return current
    }

    private fun incrementPassword(password: String): String {
        return when (val leastSignificant = password.last()) {
            'z' -> incrementPassword(password.dropLast(1)) + 'a'
            else -> password.dropLast(1) + (leastSignificant + 1)
        }
    }

    private fun isValidPassword(password: String): Boolean {
        if (!password.hasStraight(length = 3)) {
            return false
        }

        if ('i' in password || 'l' in password || 'o' in password) {
            return false
        }

        val pairs = pairRegex.findAll(password).mapNotNull { it.groups.first()?.value }.distinct()
        return pairs.count() >= 2
    }

    private fun String.hasStraight(length: Int): Boolean {
        for (index in dropLast(length).indices) {
            if (this[index] == this[index + 1] - 1 && this[index] == this[index + 2] - 2) {
                return true
            }
        }
        return false
    }

    companion object {
        private val pairRegex = "(.)\\1".toRegex()
    }
}
