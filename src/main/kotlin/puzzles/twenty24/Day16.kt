package puzzles.twenty24

import model.Coordinate
import model.Direction
import model.IntCoordinate
import utils.DijkstraResult
import utils.dijkstra
import utils.printAnswer
import kotlin.time.measureTimedValue

private val fakeInput = """
    ###############
    #.......#....E#
    #.#.###.#.###.#
    #.....#.#...#.#
    #.###.#####.#.#
    #.#.#.......#.#
    #.#.#####.###.#
    #...........#.#
    ###.#.#####.#.#
    #...#.....#.#.#
    #.#.#.###.#.#.#
    #.....#...#.#.#
    #.###.#.#.#.#.#
    #S..#.....#...#
    ###############
""".trimIndent().split("\n")

val fake2 = """
    #################
    #...#...#...#..E#
    #.#.#.#.#.#.#.#.#
    #.#.#.#...#...#.#
    #.#.#.#.###.#.#.#
    #...#.#.#.....#.#
    #.#.#.#.#.#####.#
    #.#...#.#.#.....#
    #.#.#####.#.###.#
    #.#.#.......#...#
    #.#.###.#####.###
    #.#.#...#.....#.#
    #.#.#.#####.###.#
    #.#.#.........#.#
    #.#.#.#########.#
    #S#.............#
    #################
""".trimIndent().split("\n")

fun main() {
    val input = fake2//readInput(day = 16)

    val partOneAnswer = measureTimedValue { Day16.Part01.foo(input) }
    partOneAnswer.printAnswer(label = "Part 1")

    val partTwoAnswer = measureTimedValue { Day16.Part02.foo(input) }
    partTwoAnswer.printAnswer(label = "Part 2")
}

private object Day16 {
    object Part01 {
        fun foo(input: List<String>): Int {
            val maze = parseMaze(input)
            val endCandidates = maze.traverse().values.filter { it.key.first == maze.end }
            return endCandidates.minOf { it.value.distance }
        }
    }

    object Part02 {
        fun foo(input: List<String>): Int {
            val maze = parseMaze(input)
            val path = maze.traverse().getAllShortestPathsTo(
                end = maze.end to Direction.North,
                key = { it.first },
            ) { item ->
                Direction.cardinals.primaries.map { (item.first + it) to it }
                    .flatMap { adjacent ->
                        Direction.cardinals.primaries.filter { it != adjacent.second }.map { direction ->
                            adjacent.first to direction
                        }
                    }.toSet()
            }
            val minSize = path.minOf { it.size }
            return path.filter { it.size == minSize }.flatten().distinctBy { it.first }.count()
        }
    }

    private data class Maze(
        val paths: Set<IntCoordinate>,
        val walls: Set<IntCoordinate>,
        val start: IntCoordinate,
        val end: IntCoordinate,
    ) {
        val height = walls.maxOf { it.row }
        val width = walls.maxOf { it.col }

        private val directionalPaths = paths.flatMap { path ->
            Direction.cardinals.primaries.map { path to it }
        }

        fun traverse(): DijkstraResult<Pair<IntCoordinate, Direction>> {
            return dijkstra(directionalPaths, start to Direction.East) { (coordinate, direction) ->
                Direction.cardinals.primaries.map { (coordinate + it) to it }
                    .associateWith { if (it.second != direction) 1001 else 1 }
                    .toList()
                    .toSet()
            }
        }

        override fun toString(): String = buildString {
            for (row in 0..height) {
                for (col in 0..width) {
                    when (Coordinate(row, col)) {
                        in walls -> append("#")
                        in paths -> append(".")
                        start -> append("S")
                        end -> append("E")
                    }
                }
                appendLine()
            }
        }
    }

    private fun parseMaze(input: List<String>): Maze {
        val paths = mutableSetOf<IntCoordinate>()
        val walls = mutableSetOf<IntCoordinate>()
        var start: IntCoordinate? = null
        var end: IntCoordinate? = null

        for (row in input.indices) {
            for (col in input[row].indices) {
                val coordinate = Coordinate(row, col)
                when (input[row][col]) {
                    '#' -> walls.add(coordinate)
                    '.' -> paths.add(coordinate)
                    'S' -> start = coordinate
                    'E' -> end = coordinate
                }
            }
        }

        if (start == null || end == null) {
            error("Maze is missing either start or end.")
        }

        return Maze(
            paths = paths + start + end,
            walls = walls,
            start = start,
            end = end,
        )
    }
}
