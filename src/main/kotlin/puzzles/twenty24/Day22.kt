package puzzles.twenty24

import api.readInput
import solution.Solution
import utils.printAnswer
import kotlin.time.measureTimedValue

fun main() {
    val input = readInput(day = 22)

    val partOneAnswer = measureTimedValue { Day22(input).part1.solve() }
    partOneAnswer.printAnswer(label = "Part 1")

    val partTwoAnswer = measureTimedValue { Day22(input).part2.solve() }
    partTwoAnswer.printAnswer(label = "Part 2")
}

class Day22(input: List<String>) {
    private val secrets = parseSecrets(input)
    val part1 = Solution {
        secrets.sumOf { it.next(2000).value }
    }

    val part2 = Solution {
        val resultCaches = secrets.map { it.getMaxRealPrice() }
        resultCaches.flatMap { it.keys }.distinct().maxOf { key ->
            resultCaches.sumOf { it[key] ?: 0 }
        }
    }

    private fun Secret.getMaxRealPrice(): Map<Sequence, Int> {
        var current = this
        val differences = ArrayDeque<Int>(emptyList())
        return buildMap {
            repeat(2000) {
                val next = current.next()
                if (differences.size == 4) {
                    differences.removeFirst()
                }
                differences += next.realPrice - current.realPrice

                if (differences.size == 4) {
                    getOrPut(Sequence.from(differences)) { next.realPrice }
                }

                current = next
            }
        }
    }

    private fun parseSecrets(input: List<String>): List<Secret> {
        return input.map { Secret(it.toLong()) }
    }

    data class Secret(val value: Long) {
        fun mix(value: Long): Secret {
            return Secret(value = value xor this.value)
        }

        fun prune(): Secret {
            return Secret(value = value % 16777216)
        }

        fun next(n: Int = 1): Secret {
            require(n >= 0) { "Secret number iteration must be positive." }
            var current = this
            repeat(n) {
                val first = current.mix(value = current.value * 64).prune()
                val second = first.mix(value = first.value / 32).prune()
                current = second.mix(value = second.value * 2048).prune()
            }
            return current
        }

        val realPrice: Int = value.toString().last().digitToInt()
    }

    data class Sequence(
        val first: Int,
        val second: Int,
        val third: Int,
        val fourth: Int,
    ) {
        companion object {
            fun from(values: Collection<Int>): Sequence {
                require(values.size == 4) { "Sequence list must only contain 4 values." }
                return values.iterator().let { iterator ->
                    Sequence(
                        first = iterator.next(),
                        second = iterator.next(),
                        third = iterator.next(),
                        fourth = iterator.next(),
                    )
                }
            }
        }
    }
}
