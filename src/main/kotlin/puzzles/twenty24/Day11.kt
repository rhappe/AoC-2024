package puzzles.twenty24

import api.readInput
import kotlin.time.DurationUnit
import kotlin.time.measureTimedValue

fun main() {
    val input = readInput(day = 11).single()

    val partOneAnswer = measureTimedValue { Day11.Part01.getResultingStonesCount(input) }
    println("Part 1: ${partOneAnswer.value}; Duration: ${partOneAnswer.duration.toString(DurationUnit.SECONDS, 4)}")

    val partTwoAnswer = measureTimedValue { Day11.Part02.getResultingStonesCount(input) }
    println("Part 2: ${partTwoAnswer.value}; Duration: ${partTwoAnswer.duration.toString(DurationUnit.SECONDS, 4)}")
}

private object Day11 {
    object Part01 {
        fun getResultingStonesCount(input: String): Long {
            val pluto = Pluto(input)
            return pluto.countStonesAfterBlinks(times = 25)
        }
    }

    object Part02 {
        fun getResultingStonesCount(input: String): Long {
            val pluto = Pluto(input)
            return pluto.countStonesAfterBlinks(times = 75)
        }
    }

    private class Pluto(private val initialStones: List<Long>) {
        constructor(initialStones: String) : this(initialStones.split(" ").map { it.toLong() })

        // keep track of the stone value paired with the number of starting blinks
        // mapped to the number of stones that resulted after said blinks have occurred.
        private val trackedStones = mutableMapOf<Pair<Long, Int>, Long>()

        fun countStonesAfterBlinks(times: Int): Long {
            return initialStones.sumOf { countStonesAfterBlinks(it, times) }
        }

        private fun countStonesAfterBlinks(stoneValue: Long, remaining: Int): Long {
            val trackedValue = trackedStones[stoneValue to remaining]
            val stonesCount = when {
                trackedValue != null -> trackedValue
                remaining == 0 -> 1L
                stoneValue == 0L -> countStonesAfterBlinks(
                    stoneValue = 1L,
                    remaining = remaining - 1,
                )

                stoneValue.toString().length % 2 == 0 -> {
                    val splitStones = listOf(
                        stoneValue.toString().substring(0, stoneValue.toString().length / 2).toLong(),
                        stoneValue.toString().substring(stoneValue.toString().length / 2).toLong(),
                    )
                    splitStones.sumOf {
                        countStonesAfterBlinks(stoneValue = it, remaining = remaining - 1)
                    }
                }

                else -> countStonesAfterBlinks(
                    stoneValue = stoneValue * 2024,
                    remaining = remaining - 1,
                )
            }

            trackedStones[stoneValue to remaining] = stonesCount
            return stonesCount
        }
    }
}