package puzzles

import api.readInput
import model.Coordinate
import model.Direction
import model.IntCoordinate
import utils.printAnswer
import kotlin.time.measureTimedValue

fun main() {
    val input = readInput(day = 15)

    val partOneAnswer = measureTimedValue { Day15.Part01.foo(input) }
    partOneAnswer.printAnswer(label = "Part 1")

    val partTwoAnswer = measureTimedValue { Day15.Part02.foo(input) }
    partTwoAnswer.printAnswer(label = "Part 2")
}

private object Day15 {
    object Part01 {
        fun foo(input: List<String>): Int {
            var warehouse = parseWarehouse(input)
            val moves = parseMoves(input)
            moves.forEach { warehouse = warehouse.moved(it) }
            return calculateBoxCoordinates(warehouse)
        }
    }

    object Part02 {
        fun foo(input: List<String>): Int {
            TODO()
        }
    }

    private fun parseWarehouse(input: List<String>): Warehouse {
        val emptyIndex = input.indexOfFirst { it == "" }
        val walls = mutableListOf<IntCoordinate>()
        val boxes = mutableListOf<IntCoordinate>()
        var robot: IntCoordinate? = null

        for (row in 0 until emptyIndex) {
            for (col in input[row].indices) {
                when (input[row][col]) {
                    '@' -> if (robot == null) robot = Coordinate(row, col) else error("Multiple robots found!")
                    'O' -> boxes += Coordinate(row, col)
                    '#' -> walls += Coordinate(row, col)
                }
            }
        }

        if (robot == null) {
            error("Could not find robot.")
        }

        return Warehouse(
            height = emptyIndex,
            width = input[0].length,
            robot = robot,
            walls = walls.toSet(),
            boxes = boxes.toSet(),
        )
    }

    private fun parseMoves(input: List<String>): List<Direction> {
        val emptyIndex = input.indexOfFirst { it == "" }
        val moveItems = input.subList(emptyIndex + 1, input.size)
        return moveItems.joinToString(separator = "").mapNotNull {
            when (it) {
                '^' -> Direction.North
                '>' -> Direction.East
                'v' -> Direction.South
                '<' -> Direction.West
                else -> null
            }
        }
    }

    private fun calculateBoxCoordinates(warehouse: Warehouse): Int {
        return warehouse.boxes.sumOf { it.row * 100 + it.col }
    }

    private data class Warehouse(
        val height: Int,
        val width: Int,
        val robot: IntCoordinate,
        val boxes: Set<IntCoordinate>,
        val walls: Set<IntCoordinate>,
    ) {
        fun moved(direction: Direction): Warehouse {
            var nextPosition = robot + direction
            if (nextPosition.isEmptySpace) {
                return copy(robot = robot + direction)
            }

            // iterate until we run into a wall.
            while (nextPosition !in walls) {
                if (nextPosition.isEmptySpace) {
                    val newRobotPosition = robot + direction
                    return copy(
                        robot = newRobotPosition,
                        boxes = boxes + nextPosition - newRobotPosition,
                    )
                }
                nextPosition += direction
            }

            // a wall was encountered without finding an empty space,
            // so nothing changes.
            return this
        }

        private val IntCoordinate.isEmptySpace: Boolean
            get() = robot != this && this !in boxes && this !in walls

        override fun toString(): String = buildString {
            for (row in 0 until height) {
                for (col in 0 until width) {
                    when (Coordinate(row, col)) {
                        robot -> append("@")
                        in walls -> append("#")
                        in boxes -> append("O")
                        else -> append(".")
                    }
                }
                appendLine()
            }
        }
    }
}