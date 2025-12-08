package aoc.twenty15

import aoc.api.readInput
import kotlinx.serialization.json.*
import solution.Solution
import utils.printAnswer
import kotlin.time.measureTimedValue


fun main() {
    val input = readInput(day = 12, year = 2015).first()

    val partOneAnswer = measureTimedValue { Day12(input).part1.solve() }
    partOneAnswer.printAnswer(label = "Part 1")

    val partTwoAnswer = measureTimedValue { Day12(input).part2.solve() }
    partTwoAnswer.printAnswer(label = "Part 2")
}

class Day12(input: String) {
    private val json = Json.Default.parseToJsonElement(input)

    val part1 = Solution { parseNumericValues(json) }
    val part2 = Solution { parseNumericValues(json, "red") }

    private fun parseNumericValues(element: JsonElement, vararg ignoredValues: String): Int {
        return when (element) {
            is JsonArray -> element.sumOf { parseNumericValues(it, *ignoredValues) }
            is JsonObject -> when {
                element.containsAnyStringValue(*ignoredValues) -> 0
                else -> element.values.sumOf { parseNumericValues(it, *ignoredValues) }
            }

            is JsonPrimitive -> element.intOrNull ?: 0
            JsonNull -> 0
        }
    }

    private fun JsonObject.containsAnyStringValue(vararg values: String): Boolean {
        return this@containsAnyStringValue.values.filterIsInstance<JsonPrimitive>().any {
            it.contentOrNull in values
        }
    }
}
