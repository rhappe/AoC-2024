package aoc.twenty24

import aoc.api.readInput
import model.CardinalDirection
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
            var warehouse = parseWarehouse(input, entityWidth = 1)
            val moves = parseMoves(input)
            moves.forEach { warehouse = warehouse.moved(it) }
            return calculateBoxCoordinates(warehouse)
        }
    }

    object Part02 {
        fun foo(input: List<String>): Int {
            var warehouse = parseWarehouse(input, entityWidth = 2)
            val moves = parseMoves(input)
            moves.forEach { warehouse = warehouse.moved(it) }
            return calculateBoxCoordinates(warehouse)
        }
    }

    private fun parseWarehouse(input: List<String>, entityWidth: Int): Warehouse {
        val emptyIndex = input.indexOfFirst { it == "" }
        val walls = mutableListOf<IntCoordinate>()
        val boxes = mutableListOf<IntCoordinate>()
        var robot: IntCoordinate? = null

        for (row in 0 until emptyIndex) {
            for (col in input[row].indices) {
                when (input[row][col]) {
                    '@' -> if (robot == null) {
                        robot = Coordinate(row, col * entityWidth)
                    } else {
                        error("Multiple robots found!")
                    }

                    'O' -> boxes += Coordinate(row, col * entityWidth)
                    '#' -> walls += Coordinate(row, col * entityWidth)
                }
            }
        }

        if (robot == null) {
            error("Could not find robot.")
        }

        return Warehouse(
            height = emptyIndex,
            width = input[0].length,
            robot = WarehouseEntity(
                position = robot,
                width = 1,
            ),
            walls = walls.map {
                WarehouseEntity(
                    position = it,
                    width = entityWidth,
                )
            },
            boxes = boxes.map {
                WarehouseEntity(
                    position = it,
                    width = entityWidth,
                )
            },
            entityWidth = entityWidth,
        )
    }

    private fun parseMoves(input: List<String>): List<CardinalDirection.Primary> {
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
        return warehouse.boxes.sumOf { it.position.row * 100 + it.position.col }
    }

    private data class WarehouseEntity(
        val position: IntCoordinate,
        val width: Int,
    ) {
        val coverage = List(width) { position.copy(col = position.col + it) }

        infix operator fun contains(position: IntCoordinate): Boolean {
            return position in coverage
        }

        fun moved(direction: Direction): WarehouseEntity = copy(
            position = position + direction,
        )

        fun getAdjacentPositions(direction: CardinalDirection.Primary): List<IntCoordinate> = when (direction) {
            CardinalDirection.Primary.North, CardinalDirection.Primary.South -> coverage.map { it + direction }
            CardinalDirection.Primary.East -> listOf(coverage.last() + direction)
            CardinalDirection.Primary.West -> listOf(coverage.first() + direction)
        }
    }

    private infix operator fun Collection<WarehouseEntity>.contains(position: IntCoordinate): Boolean {
        return any { position in it }
    }

    private data class Warehouse(
        val height: Int,
        val width: Int,
        val robot: WarehouseEntity,
        val boxes: List<WarehouseEntity>,
        val walls: List<WarehouseEntity>,
        val entityWidth: Int = 1,
    ) {
        private val entities = boxes + walls

        fun moved(direction: CardinalDirection.Primary): Warehouse {
            val movedRobot = robot.moved(direction)
            val nextEntity = entities.firstOrNull { movedRobot.position in it }
            if (nextEntity == null) {
                // we can't find an entity at this position, then the robot can just move there.
                return copy(robot = movedRobot)
            }

            val movableEntities = getMovableEntities(nextEntity, direction).toSet()
            val movedEntities = movableEntities.map { it.moved(direction) }
            val isInWalls = movedEntities.flatMap { it.coverage }.any { it in walls }
            return if (movedEntities.isEmpty() || isInWalls) {
                this
            } else {
                copy(
                    boxes = boxes - movableEntities + movedEntities,
                    robot = movedRobot,
                )
            }
        }

        private fun getMovableEntities(
            entity: WarehouseEntity,
            direction: CardinalDirection.Primary,
        ): List<WarehouseEntity> = when (entity.position) {
            // if the current entity is a wall, it can't be moved, and we should not move it.
            in walls -> emptyList()
            // if the current entity is a box, we can move it, and we should try to find what else it'll push.
            in boxes -> {
                val adjacentEntities = entity.getAdjacentPositions(direction).mapNotNull { position ->
                    entities.firstOrNull { position in it }
                }

                adjacentEntities.flatMap { getMovableEntities(it, direction) } + entity
            }
            // otherwise, it's an empty space, which really shouldn't happen since the position we're checking
            // is derived from a warehouse entity...
            else -> error(
                "Could not find a warehouse entity at the specified position: ${entity.position}\n\n$this",
            )
        }

        override fun toString(): String = buildString {
            for (row in 0 until height) {
                var col = 0
                while (col < width * entityWidth) {
                    when (Coordinate(row, col)) {
                        in robot -> {
                            append("@")
                            col++
                        }

                        in walls -> {
                            append("#".repeat(entityWidth))
                            col += entityWidth
                        }

                        in boxes -> {
                            when (entityWidth) {
                                1 -> append("0")
                                2 -> append("[]")
                                else -> {
                                    append("[")
                                    append("=".repeat(entityWidth - 2))
                                    append("]")
                                }
                            }
                            col += entityWidth
                        }

                        else -> {
                            append(".")
                            col++
                        }
                    }
                }
                appendLine()
            }
        }
    }
}
