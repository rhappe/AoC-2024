package aoc.twenty15

import aoc.api.readInput
import solution.Solution
import utils.printAnswer
import kotlin.math.min
import kotlin.time.measureTimedValue


fun main() {
    val input = readInput(day = 14, year = 2015)

    val partOneAnswer = measureTimedValue { Day14(input).part1.solve() }
    partOneAnswer.printAnswer(label = "Part 1")

    val partTwoAnswer = measureTimedValue { Day14(input).part2.solve() }
    partTwoAnswer.printAnswer(label = "Part 2")
}

class Day14(input: List<String>, duration: Int = 2503) {
    private val reindeer = input.map { Reindeer.parse(it) }
    val part1 = Solution { reindeer.maxOf { it.getDistance(duration) } }
    val part2 = Solution { Race(reindeer, duration).start() }

    private data class Reindeer(
        val speed: Int,
        val stamina: Int,
        val cooldown: Int,
    ) {
        private val cyclePeriod = stamina + cooldown

        fun getDistance(elapsedTime: Int): Int {
            val fullCycles = elapsedTime / cyclePeriod

            val remainder = elapsedTime % cyclePeriod
            val partialCycle = min(remainder, stamina)

            return fullCycles * speed * stamina + partialCycle * speed
        }

        companion object {
            private val regex =
                "^(.*) can fly (\\d+) km/s for (\\d+) seconds, but then must rest for (\\d+) seconds\\.".toRegex()

            fun parse(value: String): Reindeer {
                return regex.findAll(value).first().groups.filterNotNull().drop(1).let { groups ->
                    Reindeer(
                        speed = groups[1].value.toInt(),
                        stamina = groups[2].value.toInt(),
                        cooldown = groups[3].value.toInt(),
                    )
                }
            }
        }
    }

    private class Race(
        private val reindeer: List<Reindeer>,
        private val duration: Int,
    ) {
        fun start(): Int {
            val scores = reindeer.associateWith { 0 }.toMutableMap()

            for (second in 1..duration) {
                val results = reindeer.groupBy { it.getDistance(second) }
                val furthest = results.keys.max()

                results[furthest]?.forEach { scores[it] = scores.getValue(it) + 1 }
            }

            return scores.values.max()
        }
    }
}
