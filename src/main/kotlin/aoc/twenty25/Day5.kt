package aoc.twenty25

import api.readInput
import solution.Solution
import utils.printAnswer
import kotlin.time.measureTimedValue

fun main() {
    val input = readInput(day = 5, year = 2025)

    val part1Answer = measureTimedValue { Day5(input).part1.solve() }
    part1Answer.printAnswer(label = "Part 1")

    val part2Answer = measureTimedValue { Day5(input).part2.solve() }
    part2Answer.printAnswer(label = "Part 2")
}

class Day5(input: List<String>) {
    private val ranges: List<ClosedRange<Long>>
    private val ids: List<Long>

    init {
        val splitIndex = input.indexOf("")
        ranges = input.subList(0, splitIndex).fold(emptyList<LongRange>()) { acc, value ->
            val range = value.split("-").let { (first, last) ->
                first.toLong()..last.toLong()
            }

            range mergeInto acc
        }

        ids = input.subList(splitIndex + 1, input.size).map {
            it.toLong()
        }
    }

    val part1 = Solution {
        ids.count { ranges.any { range -> it in range } }
    }

    val part2 = Solution {
        ranges.sumOf { it.endInclusive - it.start + 1 }
    }

    private infix fun LongRange.mergeInto(ranges: List<LongRange>): List<LongRange> {
        if (ranges.isEmpty()) {
            // if the ranges are empty, we can just go ahead and
            // create a new list with the receiver range.
            return listOf(this)
        }

        val intersections = ranges.filter { it intersects this }

        return when {
            intersections.isEmpty() -> buildList {
                addAll(ranges)
                add(this@mergeInto)
            }

            else -> buildList {
                // merge the intersection ranges into the receiver range
                val merged = intersections.fold(this@mergeInto) { acc, range ->
                    requireNotNull(acc mergeWith range)
                }
                // and add it to the list.
                add(merged)

                // then add the remaining already-visited ranges if they are not in the
                // set of intersection ranges already we found and merged.
                for (range in ranges) {
                    if (range !in intersections) {
                        add(range)
                    }
                }
            }
        }
    }

    private infix fun LongRange.intersects(other: LongRange): Boolean {
        return first in other || last in other || other.first in this || other.last in this
    }

    private infix fun LongRange.mergeWith(other: LongRange): LongRange? {
        // if they don't intersect, return null
        if (!(this intersects other)) return null
        // otherwise combine the two ranges
        return kotlin.math.min(first, other.first)..kotlin.math.max(last, other.last)
    }
}
