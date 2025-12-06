package aoc.twenty24

import api.readInput
import model.Coordinate
import model.LongCoordinate
import utils.printAnswer
import kotlin.time.measureTimedValue

fun main() {
    val input = readInput(day = 13)

    val partOneAnswer = measureTimedValue { _root_ide_package_.aoc.twenty24.Day13.Part01.calculateMinCost(input) }
    partOneAnswer.printAnswer(label = "Part 1")

    val partTwoAnswer = measureTimedValue { _root_ide_package_.aoc.twenty24.Day13.Part02.calculateMinCost(input) }
    partTwoAnswer.printAnswer(label = "Part 2")
}

private object Day13 {
    object Part01 {
        fun calculateMinCost(input: List<String>): Long {
            val machines = _root_ide_package_.aoc.twenty24.Day13.parseClawMachines(
                input,
                _root_ide_package_.aoc.twenty24.Day13.IncreaseDifficultyRule.None
            )
            return machines.sumOf { it.calculateMinPrice(buttonCostA = 3, buttonCostB = 1) ?: 0 }
        }
    }

    object Part02 {
        private const val PRIZE_LOCATION_MOVE_AMOUNT = 10000000000000
        fun calculateMinCost(input: List<String>): Long {
            val rule = _root_ide_package_.aoc.twenty24.Day13.IncreaseDifficultyRule.MovePrizeBy(
                delta = Coordinate(
                    _root_ide_package_.aoc.twenty24.Day13.Part02.PRIZE_LOCATION_MOVE_AMOUNT,
                    _root_ide_package_.aoc.twenty24.Day13.Part02.PRIZE_LOCATION_MOVE_AMOUNT
                ),
            )
            val machines = _root_ide_package_.aoc.twenty24.Day13.parseClawMachines(input, rule)
            return machines.sumOf { it.calculateMinPrice(buttonCostA = 3, buttonCostB = 1) ?: 0 }
        }
    }

    private fun parseClawMachines(input: List<String>, difficulty: aoc.twenty24.Day13.IncreaseDifficultyRule): List<aoc.twenty24.Day13.ClawMachine> {
        val filteredEmptyLines = input.filter { it.isNotEmpty() }
        check(filteredEmptyLines.size % 3 == 0)

        return buildList {
            for (index in filteredEmptyLines.indices step 3) {
                val machine = _root_ide_package_.aoc.twenty24.Day13.ClawMachine(
                    buttonA = _root_ide_package_.aoc.twenty24.Day13.parseButtonDeltas(filteredEmptyLines[index]),
                    buttonB = _root_ide_package_.aoc.twenty24.Day13.parseButtonDeltas(filteredEmptyLines[index + 1]),
                    prizeLocation = _root_ide_package_.aoc.twenty24.Day13.parsePrizeLocation(filteredEmptyLines[index + 2]),
                )
                add(machine.apply(difficulty))
            }
        }
    }

    private fun parseButtonDeltas(buttonString: String): aoc.twenty24.Day13.ButtonDeltas {
        return buttonString.split(": ")[1].split(", ").let {
            _root_ide_package_.aoc.twenty24.Day13.ButtonDeltas(
                rowDelta = it[1].split("+")[1].toInt(), // y change
                colDelta = it[0].split("+")[1].toInt(), // x change
            )
        }
    }

    private fun parsePrizeLocation(prizeString: String): Coordinate<Long> {
        return prizeString.split(": ")[1].split(", ").let {
            Coordinate(
                row = it[1].split("=")[1].toLong(), // y value
                col = it[0].split("=")[1].toLong(), // x value
            )
        }
    }

    data class ButtonDeltas(
        val rowDelta: Int,
        val colDelta: Int,
    )

    data class ClawMachine(
        val buttonA: aoc.twenty24.Day13.ButtonDeltas,
        val buttonB: aoc.twenty24.Day13.ButtonDeltas,
        val prizeLocation: Coordinate<Long>,
    ) {
        // it's a system of linear equations...
        // buttonA.row * numPressA + buttonB.row * numPressB = prizeLocation.row
        // buttonA.col * numPressA + buttonB.col * numPressB = prizeLocation.col
        fun calculateMinPrice(buttonCostA: Int, buttonCostB: Int): Long? {
            val numPressB = (prizeLocation.col * buttonA.rowDelta - buttonA.colDelta * prizeLocation.row) /
                    (buttonB.colDelta * buttonA.rowDelta - buttonA.colDelta * buttonB.rowDelta)
            val numPressA = (prizeLocation.row - buttonB.rowDelta * numPressB) / buttonA.rowDelta

            return when {
                // instead of converting everything to floats, just check if the num presses can solve the
                // entire system of linear equations. If they don't, then there is no possible way to press
                // each button any integer number of times to reach the prize location.
                buttonA.rowDelta * numPressA + buttonB.rowDelta * numPressB != prizeLocation.row -> null
                buttonA.colDelta * numPressA + buttonB.colDelta * numPressB != prizeLocation.col -> null
                else -> numPressA * buttonCostA + numPressB * buttonCostB
            }
        }

        fun apply(rule: aoc.twenty24.Day13.IncreaseDifficultyRule): aoc.twenty24.Day13.ClawMachine = when (rule) {
            is aoc.twenty24.Day13.IncreaseDifficultyRule.MovePrizeBy -> copy(
                prizeLocation = prizeLocation + rule.delta,
            )

            _root_ide_package_.aoc.twenty24.Day13.IncreaseDifficultyRule.None -> this
        }
    }

    sealed interface IncreaseDifficultyRule {
        data object None : _root_ide_package_.aoc.twenty24.Day13.IncreaseDifficultyRule
        data class MovePrizeBy(val delta: LongCoordinate) : _root_ide_package_.aoc.twenty24.Day13.IncreaseDifficultyRule
    }
}
