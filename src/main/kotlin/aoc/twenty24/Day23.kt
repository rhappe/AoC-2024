package aoc.twenty24

import aoc.api.readInput
import model.DirectedGraph
import solution.Solution
import utils.printAnswer
import kotlin.time.measureTimedValue

fun main() {
    val input = readInput(day = 23)

    val partOneAnswer = measureTimedValue { Day23(input).part1.solve() }
    partOneAnswer.printAnswer(label = "Part 1")

    val partTwoAnswer = measureTimedValue { Day23(input).part2.solve() }
    partTwoAnswer.printAnswer(label = "Part 2")
}

class Day23(input: List<String>) {
    private val networkMap = NetworkMap(input)

    val part1 = Solution {
        networkMap.findLanParties(maxSize = 3).filter { it.size == 3 }.count { lanParty ->
            lanParty.any { it.startsWith(char = 't') }
        }
    }

    val part2 = Solution {
        networkMap.findLanParties().maxBy { it.size }.password
    }

    data class NetworkMap(
        val connections: List<Connection>,
        val networks: List<String> = connections.flatMap { listOf(it.left, it.right) }.distinct(),
    ) : Map<String, String> by connections.associate({ it.left to it.right }) {
        constructor(input: List<String>) : this(
            connections = input.map {
                it.split("-").let { (left, right) ->
                    Connection(left, right)
                }
            },
        )

        private fun createGraph() = DirectedGraph(
            values = networks,
            neighborsBlock = { node ->
                connections.filter { node in it }.flatMap { it - node }
            },
        )

        fun findLanParties(maxSize: Int = Int.MAX_VALUE): List<LanParty> {
            val graph = createGraph()
            val cliques = graph.findCliques(maxSize)
            return cliques.map { loop ->
                LanParty(loop.map { it.value }.toSet())
            }
        }
    }

    data class Connection(
        val left: String,
        val right: String,
    ) : Set<String> by setOf(left, right)

    data class LanParty(val networks: Set<String>) : Set<String> by networks {
        val password: String = networks.sorted().joinToString(",")
    }
}
