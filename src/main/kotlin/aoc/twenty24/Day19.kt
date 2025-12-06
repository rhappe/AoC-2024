package aoc.twenty24

import aoc.api.readInput
import utils.printAnswer
import kotlin.time.measureTimedValue

private fun main() {
    val input = readInput(day = 19)

    val partOneAnswer = measureTimedValue { Day19.Part01.foo(input) }
    partOneAnswer.printAnswer(label = "Part 1")

    val partTwoAnswer = measureTimedValue { Day19.Part02.foo(input) }
    partTwoAnswer.printAnswer(label = "Part 2")
}

private object Day19 {
    object Part01 {
        fun foo(input: List<String>): Int {
            val patterns = parsePatterns(input)
            val designs = parseDesigns(input)
            return designs.count { getPatternCombinationsCount(it, patterns) > 0 }
        }
    }

    object Part02 {
        fun foo(input: List<String>): Long {
            val patterns = parsePatterns(input)
            val designs = parseDesigns(input)
            return designs.sumOf { getPatternCombinationsCount(it, patterns) }
        }
    }

    private val validDesignsCache = mutableMapOf<String, Long>()

    private fun getPatternCombinationsCount(design: String, patterns: List<String>): Long {
        if (design.isEmpty()) {
            return 1
        } else if (design in validDesignsCache) {
            return validDesignsCache.getValue(design)
        }

        val matching = patterns.filter { design.startsWith(it) }
        if (matching.isEmpty()) return 0

        val total = matching.sumOf {
            getPatternCombinationsCount(
                design = design.removePrefix(it),
                patterns = patterns,
            )
        }
        validDesignsCache += (design to total)
        return total
    }


    private fun parsePatterns(input: List<String>): List<String> {
        return input.first().split(", ")
    }

    private fun parseDesigns(input: List<String>): List<String> {
        return input.subList(2, input.size)
    }
}