package puzzles.twenty25

import api.readInput
import solution.Solution
import utils.printAnswer
import kotlin.time.measureTimedValue

fun main() {
    val input = readInput(day = 3, year = 2025)

    val part1Answer = measureTimedValue { Day3(input).part1.solve() }
    part1Answer.printAnswer(label = "Part 1")

    val part2Answer = measureTimedValue { Day3(input).part2.solve() }
    part2Answer.printAnswer(label = "Part 2")
}

class Day3(input: List<String>) {
    private val batteryBanks = input.map { BatteryBank(it) }

    val part1 = Solution {
        batteryBanks.sumOf { it.maxJoltage(digits = 2) }
    }

    val part2 = Solution {
        batteryBanks.sumOf { it.maxJoltage(digits = 12) }
    }

    data class BatteryBank(val batteryJoltages: List<Int>) {
        constructor(batteryJoltages: String) : this(
            batteryJoltages = batteryJoltages.map { it.digitToInt() },
        )

        fun maxJoltage(digits: Int): Long {
            require(batteryJoltages.size >= digits) {
                "There are fewer batteries than requested joltage digits"
            }

            var startIndex = 0
            val result = buildString {
                repeat(digits) { digit ->
                    val joltages = batteryJoltages.drop(startIndex).dropLast(digits - digit - 1)
                    val nextIndex = joltages.indexOfMax()
                    append(joltages[nextIndex])
                    startIndex += nextIndex + 1
                }
            }

            return if (result.isEmpty()) 0 else result.toLong()
        }
    }

    companion object {
        private fun List<Int>.indexOfMax(): Int = max().let { maxValue ->
            indexOfFirst { it == maxValue }
        }
    }
}