package aoc.twenty15

import aoc.api.readInput
import kotlinx.serialization.json.*
import solution.Solution
import utils.printAnswer
import kotlin.time.measureTimedValue


fun main() {
    val input = readInput(day = 13, year = 2015)

    val partOneAnswer = measureTimedValue { Day13(input).part1.solve() }
    partOneAnswer.printAnswer(label = "Part 1")

    val partTwoAnswer = measureTimedValue { Day13(input).part2.solve() }
    partTwoAnswer.printAnswer(label = "Part 2")
}

class Day13(input: List<String>) {
    private val seatingEffects = input.map { SeatingEffect.parse(it) }

    val part1 = Solution {
        val distinctNames = seatingEffects.map { it.subject }.distinct()
        seatingArrangements(distinctNames).maxOf { calculateArrangementScore(it, seatingEffects) }
    }
    val part2 = Solution {
        val effects = seatingEffects + createAmbivalentActorEffects(
            names = seatingEffects.map { it.subject }.distinct(),
            newActorName = "Richard",
        )
        val distinctNames = effects.map { it.subject }.distinct()
        seatingArrangements(distinctNames).maxOf { calculateArrangementScore(it, effects) }
    }

    private fun seatingArrangements(names: List<String>): List<List<String>> {
        if (names.size == 1) {
            return listOf(names)
        }

        return buildList {
            for (name in names) {
                val otherNames = names.filter { it != name }
                for (others in seatingArrangements(otherNames)) {
                    add(listOf(name) + others)
                }
            }
        }
    }

    private data class SeatingEffect(
        val subject: String,
        val neighbor: String,
        val happiness: Int,
    ) {
        companion object {
            private val seatingEffectRegex =
                "^(.*) would (.*) (\\d*) happiness units by sitting next to (.*)\\.".toRegex()

            fun parse(value: String): SeatingEffect {
                return seatingEffectRegex.findAll(value).first().groups.filterNotNull().drop(1).let { groups ->
                    SeatingEffect(
                        subject = groups[0].value,
                        neighbor = groups[3].value,
                        happiness = when (groups[1].value) {
                            "gain" -> groups[2].value.toInt()
                            "lose" -> -groups[2].value.toInt()
                            else -> error("Invalid happiness type: ${groups[1]}")
                        }
                    )
                }
            }
        }
    }

    private fun calculateArrangementScore(arrangement: List<String>, effects: List<SeatingEffect>): Int {
        val pairs = buildList {
            arrangement.indices.forEach { index ->
                if (index == 0) {
                    add(arrangement[index] to arrangement.last())
                } else {
                    add(arrangement[index] to arrangement[index - 1])
                }

                if (index == arrangement.lastIndex) {
                    add(arrangement[index] to arrangement[0])
                } else {
                    add(arrangement[index] to arrangement[index + 1])
                }
            }
        }

        return pairs.fold(0) { acc, pair ->
            val effect = effects.single { it.subject == pair.first && it.neighbor == pair.second }
            acc + effect.happiness
        }
    }

    private fun createAmbivalentActorEffects(names: List<String>, newActorName: String): List<SeatingEffect> {
        return buildList {
            for (name in names) {
                add(SeatingEffect(name, newActorName, 0))
                add(SeatingEffect(newActorName, name, 0))
            }
        }
    }
}
