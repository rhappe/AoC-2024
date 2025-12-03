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

    val part2Answer = measureTimedValue { Day1(input).part2.solve() }
    part2Answer.printAnswer(label = "Part 2")
}

private class Day1(input: List<String>) {
    private val rotations = input.map { DialRotation.parse(it) }

    private val dial = Dial(
        start = 50,
        target = 0,
        range = 0..99,
    )

    val part1 = Solution {
        val count = AtomicInteger(0)

        for (rotation in rotations) {
            dial.turn(rotation.count, rotation.direction)
            if (dial.currentPosition == 0) {
                count.incrementAndGet()
            }
        }

        count.toInt()
    }

    val part2 = Solution {
        rotations.sumOf {
            dial.turn(it.count, it.direction)
        }
    }

    private class Dial(start: Int, private val target: Int, private val range: IntRange) {
        var currentPosition = start
            private set

        fun turn(count: Int, direction: DialDirection): Int {
            // calculate the distance to the target dial value
            val distanceToTarget: Int = when (direction) {
                DialDirection.Clockwise -> {
                    if (target >= currentPosition) {
                        currentPosition - target
                    } else {
                        range.count() - currentPosition
                    }
                }

                DialDirection.CounterClockwise -> {
                    if (target >= currentPosition) {
                        target - currentPosition
                    } else {
                        currentPosition - target
                    }
                }
            }

            // if it is possible to turn the dial to the target value given the rotation count,
            // attempt to do so, then try rotating the dial the remaining distance in the same direction.
            if (distanceToTarget != 0 && distanceToTarget < count) {
                return turn(distanceToTarget, direction) + turn(count - distanceToTarget, direction)
            }

            // if the dial is currently on the target value and rotating the dial count times would require
            // at least one full rotation, add 1 to the return value (passed the target value) and continue
            // turning the dial count - range times in the same direction.
            if (currentPosition == target && count > range.count()) {
                return 1 + turn(count - range.count(), direction)
            }

            // finally, rotate the dial the remaining number of times and set the current position
            // to the new normalized value.
            val moves = count % range.count()
            val newPosition = currentPosition + moves * direction.multiplier
            currentPosition = newPosition.normalizedToRange()

            // if we got this far, we already took care of extra passes of the target value. So if the resulting
            // current position is on the target value, return 1. Otherwise, return 0 (no target hits).
            return if (currentPosition == target) 1 else 0
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
            private const val CCW_INDICATOR = 'L'
            private const val CW_INDICATOR = 'R'

            fun parse(string: String): DialRotation {
                val directionPart = string.first().uppercaseChar()
                val countPart = string.substring(1)

                return DialRotation(
                    count = countPart.toInt(),
                    direction = when (directionPart) {
                        CCW_INDICATOR -> DialDirection.CounterClockwise
                        CW_INDICATOR -> DialDirection.Clockwise
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
