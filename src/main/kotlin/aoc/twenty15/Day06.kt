package aoc.twenty15

import aoc.api.readInput
import model.Grid
import model.IntCoordinate
import solution.Solution
import utils.printAnswer
import kotlin.math.max
import kotlin.time.measureTimedValue

fun main() {
    val input = readInput(day = 6, year = 2015)

    val partOneAnswer = measureTimedValue { Day06(input).part1.solve() }
    partOneAnswer.printAnswer(label = "Part 1")

    val partTwoAnswer = measureTimedValue { Day06(input).part2.solve() }
    partTwoAnswer.printAnswer(label = "Part 2")
}

class Day06(input: List<String>) {
    private val instructions = parseInstructions(input)

    val part1 = Solution {
        val lightGrid: Grid<LightState> = Grid(rows = 1000, cols = 1000) { _, _ ->
            LightState.Off
        }

        val result = instructions.fold(lightGrid) { acc, instruction ->
            acc.mapGridValues { position, state ->
                if (position.isInSquareRange(instruction.start, instruction.end)) {
                    when (instruction.action) {
                        LightAction.TurnOn -> LightState.On
                        LightAction.TurnOff -> LightState.Off
                        LightAction.Toggle -> !state
                    }
                } else {
                    state
                }
            }
        }

        result.sumOf { row ->
            row.count { it == LightState.On }
        }
    }

    val part2 = Solution {
        val lightGrid: Grid<Int> = Grid(rows = 1000, cols = 1000) { _, _ -> 0 }
        val result = instructions.fold(lightGrid) { acc, instruction ->
            acc.mapGridValues { position, brightness ->
                if (position.isInSquareRange(instruction.start, instruction.end)) {
                    when (instruction.action) {
                        LightAction.TurnOn -> brightness + 1
                        LightAction.TurnOff -> max(brightness - 1, 0)
                        LightAction.Toggle -> brightness + 2
                    }
                } else {
                    brightness
                }
            }
        }

        result.sumOf { it.sum() }
    }

    enum class LightState {
        On,
        Off,
        ;

        operator fun not(): LightState = when (this) {
            On -> Off
            Off -> On
        }
    }

    private enum class LightAction {
        TurnOn,
        TurnOff,
        Toggle,
    }

    private data class LightInstruction(
        val start: IntCoordinate,
        val end: IntCoordinate,
        val action: LightAction,
    )

    companion object {
        private val rangeRegex = "\\d+,\\d+".toRegex()

        private fun parseInstructions(input: List<String>): List<LightInstruction> {
            return input.map { line ->
                val ranges = rangeRegex.findAll(line).map { it.value }.toList()

                LightInstruction(
                    start = ranges[0].split(",").let { IntCoordinate(it[0].toInt(), it[1].toInt()) },
                    end = ranges[1].split(",").let { IntCoordinate(it[0].toInt(), it[1].toInt()) },
                    action = when {
                        "turn on" in line -> LightAction.TurnOn
                        "turn of" in line -> LightAction.TurnOff
                        "toggle" in line -> LightAction.Toggle
                        else -> error("Could not parse light action: $line")
                    },
                )
            }
        }
    }

    private fun IntCoordinate.isInSquareRange(start: IntCoordinate, end: IntCoordinate): Boolean {
        return row >= start.row && col >= start.col && row <= end.row && col <= end.col
    }
}
