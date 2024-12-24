package puzzles.twenty15

import api.readInput
import model.Coordinate
import model.IntCoordinate
import solution.Solution
import utils.printAnswer
import kotlin.math.ceil
import kotlin.time.measureTimedValue

fun main() {
    val input = readInput(day = 6, year = 2015)

    val partOneAnswer = measureTimedValue { Day06(input).part1.solve() }
    partOneAnswer.printAnswer(label = "Part 1")

    val partTwoAnswer = measureTimedValue { Day06(input).part2.solve() }
    partTwoAnswer.printAnswer(label = "Part 2")
}

class Day06(input: List<String>) {
    companion object {
        private val coordinatesRegex = "\\d+,\\d+".toRegex()
    }

    private val instructions = input.map { line ->
        val coordinates = coordinatesRegex.findAll(line).map { it.value.split(",") }
            .map {
                Coordinate(
                    row = it[1].toInt(),
                    col = it[0].toInt(),
                )
            }
            .take(2)
            .toList()
        when (val instruction = coordinatesRegex.split(line)[0].trim()) {
            "turn on" -> Instruction.TurnOn(
                fromCoordinate = coordinates.first(),
                toCoordinate = coordinates.last(),
            )

            "turn off" -> Instruction.TurnOff(
                fromCoordinate = coordinates.first(),
                toCoordinate = coordinates.last(),
            )

            "toggle" -> Instruction.Toggle(
                fromCoordinate = coordinates.first(),
                toCoordinate = coordinates.last(),
            )

            else -> error("Unknown instruction: $instruction")
        }
    }

    private val fakeInstructions = listOf(
        Instruction.TurnOn(
            fromCoordinate = Coordinate(0, 0),
            toCoordinate = Coordinate(999, 999),
        ),
        Instruction.Toggle(
            fromCoordinate = Coordinate(0, 0),
            toCoordinate = Coordinate(0, 999),
        ),
        Instruction.TurnOff(
            fromCoordinate = Coordinate(499, 499),
            toCoordinate = Coordinate(500, 500),
        )
    )

    val part1 = Solution {
        val grid = LightGrid(1000, 1000)
        fakeInstructions.forEach { grid.invoke(it) }
        println(grid)
        grid.toString().count { it == '#' }
    }

    val part2 = Solution {
        0
    }

    private class LightGrid(private val height: Int, private val width: Int) : (Instruction) -> Unit {
        // The gird of boolean values can be represented by a list of Long values. Each long represents
        // 64 bits of binary data, which can tell us the state of 64 of the lights (on vs off).
        // the size is {grid size} / 64 rounded up to the next integer.
        // E.g. with height = 1000 and width = 1000 (size = 1,000,000); can be represented in 15625 longs.
        private val lightStates = MutableList<ULong>(ceil((height * width) / 64.0).toInt()) { 0u }

        private operator fun get(coordinate: IntCoordinate): LightState {
            val (index, position) = coordinate.lightIndex
            check(index in lightStates.indices && position in 0 until 64)
            val mask = 1u.toULong() shl (63 - position)
            return if (lightStates[index] and mask != 0u.toULong()) LightState.On else LightState.Off
        }

        private data class GridIndex(
            val index: Int,
            val position: Int,
        )

        private val IntCoordinate.lightIndex: GridIndex
            get() = ((row * height) + col).let { absolute ->
                GridIndex(
                    index = absolute / 64,
                    position = absolute % 64,
                )
            }

        private operator fun contains(coordinate: IntCoordinate): Boolean {
            return coordinate.row in 0 until height && coordinate.col in 0 until width
        }

        private fun IntRange.toULong(): ULong {
            val left = if (first == 0) ULong.MAX_VALUE else ((1.toULong() shl (64 - first)) - 1.toULong())
            val right = (1.toULong() shl (64 - last)) - 1.toULong()
            return left and right.inv()
        }

        override fun invoke(instruction: Instruction) {
            if (instruction.fromCoordinate !in this || instruction.toCoordinate !in this) {
                error("Index out of bounds: ${instruction.fromCoordinate} to ${instruction.toCoordinate}")
            }

            val gridIndexFrom = instruction.fromCoordinate.lightIndex
            val gridIndexTo = instruction.toCoordinate.lightIndex

            val maskCount = gridIndexTo.index - gridIndexFrom.index
            val masks: List<ULong> = when (maskCount + 1) {
                0 -> emptyList()
                1 -> listOf((gridIndexFrom.position..gridIndexTo.position).toULong())
                else -> buildList {
                    add((gridIndexFrom.position..64).toULong())
                    repeat(maskCount - 1) { add(ULong.MAX_VALUE) }
                    add((0..gridIndexTo.position + 1).toULong())
                }
            }

            for (index in masks.indices) {
                val lightStatesIndex = gridIndexFrom.index + index
                val mask = masks[index]
                lightStates[lightStatesIndex] = when (instruction) {
                    // toggle can be performed by applying a xor operator with the mask as-is.
                    is Instruction.Toggle -> lightStates[lightStatesIndex] xor mask
                    // turn off can be performed by applying an and operator with the inverted mask
                    is Instruction.TurnOff -> lightStates[lightStatesIndex] and mask.inv()
                    // turn on can be performed by applying an or operator with the mask as-is.
                    is Instruction.TurnOn -> lightStates[lightStatesIndex] or mask
                }
            }
        }

        override fun toString(): String = buildString {
            for (row in 0 until height) {
                for (col in 0 until width) {
                    when (this@LightGrid[Coordinate(row, col)]) {
                        LightState.On -> append("#")
                        LightState.Off -> append(".")
                    }
                }
                appendLine()
            }
        }.trimStart()
    }

    private enum class LightState {
        On,
        Off,
        ;
    }

    private sealed interface Instruction {
        val fromCoordinate: IntCoordinate
        val toCoordinate: IntCoordinate

        fun transform(state: LightState): LightState

        data class TurnOn(
            override val fromCoordinate: IntCoordinate,
            override val toCoordinate: IntCoordinate,
        ) : Instruction {
            override fun transform(state: LightState) = LightState.On
        }

        data class TurnOff(
            override val fromCoordinate: IntCoordinate,
            override val toCoordinate: IntCoordinate,
        ) : Instruction {
            override fun transform(state: LightState) = LightState.Off
        }

        data class Toggle(
            override val fromCoordinate: IntCoordinate,
            override val toCoordinate: IntCoordinate,
        ) : Instruction {
            override fun transform(state: LightState): LightState = when (state) {
                LightState.On -> LightState.Off
                LightState.Off -> LightState.On
            }
        }

    }
}