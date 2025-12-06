package aoc.twenty15

import aoc.api.readInput
import solution.Solution
import utils.printAnswer
import kotlin.math.abs
import kotlin.time.measureTimedValue

fun main() {
    val input = readInput(day = 5, year = 2015)

    val partOneAnswer = measureTimedValue { Day5(input).part1.solve() }
    partOneAnswer.printAnswer(label = "Part 1")

    val partTwoAnswer = measureTimedValue { Day5(input).part2.solve() }
    partTwoAnswer.printAnswer(label = "Part 2")
}

class Day5(input: List<String>) {
    val part1: Solution<Int> = Part1(input)

    private class Part1(private val input: List<String>) : Solution<Int> {
        companion object {
            private const val VOWELS = "aeiou"
        }

        private val invalidStrings = listOf("ab", "cd", "pq", "xy")
        private val doubleLettersRegex = "(.)\\1+".toRegex()

        private fun isNice(value: String): Boolean {
            val vowelsCount = value.count { it in VOWELS }
            if (vowelsCount < 3) {
                return false
            }

            if (invalidStrings.any { it in value }) {
                return false
            }

            return value.contains(doubleLettersRegex)
        }

        override fun solve(): Int = input.count { isNice(it) }
    }

    val part2: Solution<Int> = Part2(input)

    private class Part2(private val input: List<String>) : Solution<Int> {
        private val anyDoubleLettersRegex = "..".toRegex()
        private val separatedDoubleLettersRegex = "(.).\\1".toRegex()

        override fun solve(): Int {
            return input.count { isNice(it) }
        }

        private fun isNice(value: String): Boolean {
            val twoNonOverlapping =
                hasNonOverlappingDoubleLetters(value) || hasNonOverlappingDoubleLetters(value.drop(1))
            val separatedDoubleLetters = value.contains(separatedDoubleLettersRegex)
            return twoNonOverlapping && separatedDoubleLetters
        }

        private fun hasNonOverlappingDoubleLetters(value: String): Boolean {
            val matches = anyDoubleLettersRegex.findAll(value).map {
                it.range.first to it.value
            }.toList()
            val matchesOffByOne = anyDoubleLettersRegex.findAll(value.drop(1)).map {
                it.range.first + 1 to it.value
            }.toList()

            if (matches.size > matches.distinctBy { it.second }.size) {
                return true
            }

            if (matchesOffByOne.size > matchesOffByOne.distinctBy { it.second }.size) {
                return true
            }

            return matches.any { match ->
                val other = matchesOffByOne.filter { it.second == match.second }
                other.any { abs(it.first - match.first) > 1 }
            }
        }
    }
}