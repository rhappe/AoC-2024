package puzzles

import api.readInput
import kotlinx.coroutines.flow.*
import model.Coordinate
import model.Direction
import model.Grid
import model.IntCoordinate
import solution.Solution
import utils.printAnswer
import kotlin.math.absoluteValue
import kotlin.time.measureTimedValue

fun main() {
    val input = readInput(day = 21)

    val partOneAnswer = measureTimedValue { Day21(input).part1.solve() }
    partOneAnswer.printAnswer(label = "Part 1")

    val partTwoAnswer = measureTimedValue { Day21(input).part2.solve() }
    partTwoAnswer.printAnswer(label = "Part 2")
}

class Day21(input: List<String>) {
    companion object {
        fun getKeypad(target: String, numRobots: Int): Keypad {
            val numeric = Keypad.Numeric(target)
            val middlemen = buildList<Keypad> {
                repeat(numRobots - 1) {
                    val prev = if (isEmpty()) numeric else last()
                    add(Keypad.Directional(prev))
                }
            }
            return Keypad.Directional(middlemen.lastOrNull() ?: numeric)
        }
    }

    val part1 = Solution {
        input.sumOf {
            getKeypad(target = it, numRobots = 2).complexityScore
        }
    }

    val part2 = Solution {
        0
    }

    sealed class Keypad {
        protected abstract val keypad: Grid<Char>

        private val rootSequence: String by lazy {
            when (this) {
                is Directional -> replica.rootSequence
                is Numeric -> sequence
            }
        }

        val complexityScore: Int by lazy {
            rootSequence.filter { it.isDigit() }.toInt() * directionalSequence.length
        }

        val directionalSequence: String by lazy {
            var currentNode = keypad['A']
            val sequence = when (this) {
                is Directional -> replica.directionalSequence
                is Numeric -> sequence
            }

            val queue = ArrayDeque(sequence.toList())

            buildString {
                while (queue.isNotEmpty()) {
                    val nextNode = keypad[queue.removeFirst()]
                    val (rowDelta, colDelta) = nextNode - currentNode
                    val horizontal: String = if (colDelta <= 0) {
                        "<".repeat(colDelta.absoluteValue)
                    } else {
                        ">".repeat(colDelta.absoluteValue)
                    }
                    val vertical: String = if (rowDelta <= 0) {
                        "^".repeat(rowDelta.absoluteValue)
                    } else {
                        "v".repeat(rowDelta.absoluteValue)
                    }

                    if (keypad[currentNode + Coordinate(rowDelta, 0)] == ' ') {
                        append(horizontal + vertical)
                    } else if (keypad[currentNode + Coordinate(0, colDelta)] == ' ') {
                        append(vertical + horizontal)
                    } else {
                        append(
                            (horizontal + vertical).toList().sortedBy {
                                "<v>^".indexOf(it)
                            }.joinToString("")
                        )
                    }

                    currentNode = nextNode

                    append('A')
                }
            }
        }

        data class Numeric(val sequence: String) : Keypad() {
            override val keypad = Grid(
                strings = listOf(
                    "789",
                    "456",
                    "123",
                    " 0A",
                ),
            )
        }

        data class Directional(val replica: Keypad) : Keypad() {
            override val keypad = Grid(
                strings = listOf(
                    " ^A",
                    "<v>"
                ),
            )
        }
    }
}
