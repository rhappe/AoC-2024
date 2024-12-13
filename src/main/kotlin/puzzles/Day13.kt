package puzzles

import api.readInput
import model.Coordinate
import model.LongCoordinate
import utils.printAnswer
import kotlin.time.measureTimedValue

fun main() {
    val input = readInput(day = 13)

    val partOneAnswer = measureTimedValue { Day13.Part01.foo(input) }
    partOneAnswer.printAnswer(label = "Part 1")

    val partTwoAnswer = measureTimedValue { Day13.Part02.foo(input) }
    partTwoAnswer.printAnswer(label = "Part 2")
}

private object Day13 {
    object Part01 {
        fun foo(input: List<String>): Long {
            val machines = parseClawMachines(input, IncreaseDifficultyRule.None)
            return machines.sumOf { it.calculateMinPrice(buttonCostA = 3, buttonCostB = 1) ?: 0 }
        }
    }

    object Part02 {
        private const val PRIZE_LOCATION_MOVE_AMOUNT = 10000000000000
        fun foo(input: List<String>): Long {
            val rule = IncreaseDifficultyRule.MovePrizeBy(
                delta = Coordinate(PRIZE_LOCATION_MOVE_AMOUNT, PRIZE_LOCATION_MOVE_AMOUNT),
            )
            val machines = parseClawMachines(input, rule)
            return machines.sumOf { it.calculateMinPrice(buttonCostA = 3, buttonCostB = 1) ?: 0 }
        }
    }

    private fun parseClawMachines(input: List<String>, difficulty: IncreaseDifficultyRule): List<ClawMachine> {
        val filteredEmptyLines = input.filter { it.isNotEmpty() }
        check(filteredEmptyLines.size % 3 == 0)

        return buildList {
            for (index in filteredEmptyLines.indices step 3) {
                val machine = ClawMachine(
                    buttonA = parseButtonDeltas(filteredEmptyLines[index]),
                    buttonB = parseButtonDeltas(filteredEmptyLines[index + 1]),
                    prizeLocation = parsePrizeLocation(filteredEmptyLines[index + 2]),
                )
                add(machine.apply(difficulty))
            }
        }
    }

    private fun parseButtonDeltas(buttonString: String): ButtonDeltas {
        return buttonString.split(": ")[1].split(", ").let {
            ButtonDeltas(
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
        val buttonA: ButtonDeltas,
        val buttonB: ButtonDeltas,
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

        fun apply(rule: IncreaseDifficultyRule): ClawMachine = when (rule) {
            is IncreaseDifficultyRule.MovePrizeBy -> copy(
                prizeLocation = prizeLocation + rule.delta,
            )

            IncreaseDifficultyRule.None -> this
        }
    }

    sealed interface IncreaseDifficultyRule {
        data object None : IncreaseDifficultyRule
        data class MovePrizeBy(val delta: LongCoordinate) : IncreaseDifficultyRule
    }
}
