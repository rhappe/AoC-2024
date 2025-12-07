package aoc.twenty15

import aoc.api.readInput
import solution.Solution
import utils.printAnswer
import kotlin.time.measureTimedValue


fun main() {
    val input = readInput(day = 9, year = 2015)

    val partOneAnswer = measureTimedValue { Day09(input).part1.solve() }
    partOneAnswer.printAnswer(label = "Part 1")

    val partTwoAnswer = measureTimedValue { Day09(input).part2.solve() }
    partTwoAnswer.printAnswer(label = "Part 2")
}

class Day09(input: List<String>) {
    private val routes = parseRoutes(input)
    private val destinations = buildSet {
        routes.forEach {
            add(it.start)
            add(it.end)
        }
    }

    val part1 = Solution {
        val permutations = buildPermutations(destinations)
        val distances = calculateDistances(permutations, routes)
        distances.values.min()
    }
    val part2 = Solution {
        val permutations = buildPermutations(destinations)
        val distances = calculateDistances(permutations, routes)
        distances.values.max()
    }

    private data class Route(
        val start: String,
        val end: String,
        val distance: Int,
    )

    private fun parseRoutes(input: List<String>): List<Route> {
        return buildList {
            for (line in input) {
                val parts = line.split(" ")
                val start = parts[0]
                val end = parts[2]
                val distance = parts[4].toInt()

                add(Route(start, end, distance))
                add(Route(end, start, distance))
            }
        }
    }

    private fun buildPermutations(values: Set<String>): List<List<String>> {
        if (values.isEmpty()) {
            return listOf(values.toList())
        }
        return buildList {
            for (value in values) {
                val otherValues = values.filter { it != value }.toSet()
                for (others in buildPermutations(otherValues)) {
                    add(listOf(value) + others)
                }
            }
        }
    }

    private fun calculateDistances(permutations: List<List<String>>, routes: List<Route>): Map<List<String>, Int> {
        return buildMap {
            for (permutation in permutations) {
                var distance = 0
                for (index in permutation.indices.toList().dropLast(1)) {
                    val start = permutation[index]
                    val end = permutation[index + 1]
                    val route = routes.first { it.start == start && it.end == end }
                    distance += route.distance
                }
                put(permutation, distance)
            }
        }
    }
}
