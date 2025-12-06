package aoc.twenty24

import api.readInput
import model.*
import utils.printAnswer
import kotlin.time.measureTimedValue

fun main() {
    val input = readInput(day = 20)

    val partOneAnswer = measureTimedValue { Day20.Part01.getEffectiveCheatsCount(input) }
    partOneAnswer.printAnswer(label = "Part 1")

    val partTwoAnswer = measureTimedValue { Day20.Part02.getEffectiveCheatsCount(input) }
    partTwoAnswer.printAnswer(label = "Part 2")
}

object Day20 {
    object Part01 {
        fun getEffectiveCheatsCount(input: List<String>): Int {
            val raceTrack = parseRaceTrack(input)
            val cheats = raceTrack.getCheats(maxCheatDuration = 2)
            return cheats.count { it.savedDistance >= 100 }
        }
    }

    object Part02 {
        fun getEffectiveCheatsCount(input: List<String>): Int {
            val raceTrack = parseRaceTrack(input)
            val cheats = raceTrack.getCheats(maxCheatDuration = 20)
            return cheats.count { it.savedDistance >= 100 }
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
        )
    }

    private data class RaceTrack(
        val start: IntCoordinate,
        val end: IntCoordinate,
        val paths: Collection<IntCoordinate>,
    ) {
        private val shortestPath: List<Pair<IntCoordinate, Int>> by lazy {
            val graph = DirectedGraph(
                values = paths,
                neighborsBlock = { it inDirections Direction.cardinals.primaries },
            )
            // all nodes are distance 1 apart, so we can just use the index for distance
            graph.findShortestPath(start, end)
        }

        fun getCheats(maxCheatDuration: Int): List<Cheat> {
            return shortestPath.flatMapIndexed { index: Int, start ->
                shortestPath.subList(index + 1, shortestPath.size).toMap().mapNotNull { end ->
                    val realDistance = end.value - start.second
                    val cheatDistance = start.first distanceTo end.key
                    if (cheatDistance <= maxCheatDuration) {
                        Cheat(
                            start = start.first,
                            end = end.key,
                            savedDistance = realDistance - cheatDistance,
                        )
                    } else {
                        null
                    }
                }
            }
        }
    }

    private data class Cheat(
        val start: IntCoordinate,
        val end: IntCoordinate,
        val savedDistance: Int,
    )
}
