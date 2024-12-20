package puzzles

import api.readInput
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import model.*
import utils.dijkstra
import utils.printAnswer
import kotlin.math.sign
import kotlin.time.DurationUnit
import kotlin.time.measureTimedValue

val fakeInput = """
    ###############
    #...#...#.....#
    #.#.#.#.#.###.#
    #S#...#.#.#...#
    #######.#.#.###
    #######.#.#...#
    #######.#.###.#
    ###..E#...#...#
    ###.#######.###
    #...###...#...#
    #.#####.#.###.#
    #.#...#.#.#...#
    #.#.#.#.#.#.###
    #...#...#...###
    ###############
""".trimIndent().split("\n")

fun main() {
    val input = readInput(day = 20)

    val partOneAnswer = measureTimedValue { Day20.Part01.getEffectiveCheatsCount(input) }
    partOneAnswer.printAnswer(label = "Part 1")

//    val partTwoAnswer = measureTimedValue { Day20.Part02.foo(input) }
//    partTwoAnswer.printAnswer(label = "Part 2")
}

private object Day20 {
    object Part01 {
        fun getEffectiveCheatsCount(input: List<String>): Int {
            val raceTrack = parseRaceTrack(input)
            val cheats = raceTrack.getCheats()
            return cheats.count { it.savedDistance >= 100 }
        }
    }

    object Part02 {
        fun foo(input: List<String>): Int {
            TODO()
        }
    }

    private fun parseRaceTrack(input: List<String>): RaceTrack {
        var start: IntCoordinate? = null
        var end: IntCoordinate? = null
        val paths = mutableListOf<IntCoordinate>()
        val walls = mutableListOf<IntCoordinate>()
        for (row in input.indices) {
            for (col in input[row].indices) {
                val coordinate = Coordinate(row, col)
                when (input[row][col]) {
                    '.' -> paths += coordinate
                    '#' -> walls += coordinate
                    'S' -> start = coordinate.also { paths += it }
                    'E' -> end = coordinate.also { paths += it }
                }
            }
        }
        if (start == null || end == null) {
            error("Could not find the start or end location.")
        }

        return RaceTrack(
            start = start,
            end = end,
            paths = paths.toList(),
            walls = walls.toList(),
        )
    }

    private data class Cheat(
        val savedDistance: Int,
    )

    private data class RaceTrack(
        val start: IntCoordinate,
        val end: IntCoordinate,
        val paths: Collection<IntCoordinate>,
        val walls: Collection<IntCoordinate>,
    ) {
        private val graph = DirectedGraph(
            values = paths,
            neighborsBlock = { it inDirections Direction.entries },
            distanceBlock = { _, _ -> 1 },
        )
        private val dijkstraResult by lazy {
            requireNotNull(graph.dijkstra(start)) {
                error("Could not complete course from ${start}.")
            }
        }
        private val shortestPath: ArrayDeque<DirectedGraph.Node<IntCoordinate>> by lazy {
            requireNotNull(dijkstraResult shortestPathTo graph.findNode(end)) {
                "Could not find a path from $start to $end"
            }
        }

        private fun DirectedGraph.Node<IntCoordinate>.checkDirection(direction: Direction): Int? {
            val wallCandidate = value + direction
            if (wallCandidate !in walls) {
                return null
            }

            val nodeCandidate = shortestPath.firstOrNull { it.value == wallCandidate + direction }
            if (nodeCandidate == null) {
                return null
            }

            return dijkstraResult.distances.let {
                it.getValue(nodeCandidate) - it.getValue(this) - 2
            }
        }

        fun getCheats(): Collection<Cheat> {
            val cheats = shortestPath.flatMap { node ->
                Direction.entries.mapNotNull { direction ->
                    node.checkDirection(direction)?.let {
                        Cheat(savedDistance = it)
                    }
                }
            }
            return cheats
        }
    }
}
