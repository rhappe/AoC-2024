package puzzles.twenty25

import api.readInput
import solution.Solution
import utils.printAnswer
import java.util.concurrent.atomic.AtomicInteger
import kotlin.time.measureTimedValue

fun main() {
    val input = readInput(day = 1, year = 2025)

    val part1Answer = measureTimedValue { Day1(input).part1.solve() }
    part1Answer.printAnswer(label = "Part 1")
}

private class Day1(input: List<String>) {
    private val rotations = input.map { DialRotation.parse(it) }

    val part1 = Solution {
        val count = AtomicInteger(0)
        val dial = Dial(
            start = 50,
            range = 0..99,
        )

        for (rotation in rotations) {
            dial.turn(rotation.count, rotation.direction)
            if (dial.currentPosition == 0) {
                count.incrementAndGet()
            }
        }

        count.toInt()
    }

    private class Dial(start: Int, private val range: IntRange) {
        var currentPosition = start
            private set

        fun turn(count: Int, direction: DialDirection) {
            val newPosition = currentPosition + count * direction.multiplier
            currentPosition = newPosition.normalizedToRange()
        }

        private fun Int.normalizedToRange(): Int {
            var value = this
            while (value !in range) {
                if (value < range.first) {
                    value += range.last - range.first + 1
                } else {
                    value -= range.last - range.first + 1
                }
            }

            return value
        }
    }

    private data class DialRotation(
        val count: Int,
        val direction: DialDirection,
    ) {
        companion object {
            fun parse(string: String): DialRotation {
                val directionPart = string.first().uppercaseChar()
                val countPart = string.substring(1)

                return DialRotation(
                    count = countPart.toInt(),
                    direction = when (directionPart) {
                        'L' -> DialDirection.CounterClockwise
                        'R' -> DialDirection.Clockwise
                        else -> error("Invalid direction part: $directionPart")
                    }
                )
            }
        }
    }

    private enum class DialDirection(val multiplier: Int) {
        Clockwise(multiplier = 1),
        CounterClockwise(multiplier = -1),
    }
}