package aoc.twenty25

import api.readInput
import solution.Solution
import utils.printAnswer
import kotlin.time.measureTimedValue


fun main() {
    val input = readInput(day = 2, year = 2025).first()

    val part1Answer = measureTimedValue { Day2(input).part1.solve() }
    part1Answer.printAnswer(label = "Part 1")

    val part2Answer = measureTimedValue { Day2(input).part2.solve() }
    part2Answer.printAnswer(label = "Part 2")
}

class Day2(input: String) {
    private val ranges: List<LongRange> = input.split(",").map { rangeString ->
        rangeString.split("-").let {
            it.first().toLong()..it.last().toLong()
        }
    }

    val part1 = Solution {
        val validator = ChunkedProductIdValidator { if (it.length % 2 == 0) listOf(it.length / 2) else emptyList() }
        ranges.flatten().filter { !validator.isValid(it.toString()) }.sum()
    }

    val part2 = Solution {
        val validator = ChunkedProductIdValidator { (1..it.length / 2).toList() }
        ranges.flatten().filter { !validator.isValid(it.toString()) }.sum()
    }

    private class ChunkedProductIdValidator(private val chunkSizes: (String) -> List<Int>) : ProductIdValidator {
        override fun isValid(productId: String): Boolean {
            // only take chunk sizes that the product id string length divides into equally.
            val chunkSizes = chunkSizes(productId).filter { productId.length % it == 0 }
            // if there are no chunk sizes to split the product id into equally or if none of them
            // have a distinct repeating pattern for any of the chunk sizes, the product id is valid.
            return chunkSizes.isEmpty() || chunkSizes.none { productId.chunked(it).distinct().size == 1 }
        }
    }

    private fun interface ProductIdValidator {
        fun isValid(productId: String): Boolean
    }
}
