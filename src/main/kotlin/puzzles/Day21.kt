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
    partOneAnswer.printAnswer(label = "Part 2")

    val partTwoAnswer = measureTimedValue { Day21(input).part2.solve() }
    partTwoAnswer.printAnswer(label = "Part 2")
}

class Day21(input: List<String>) {
    val part1 = Solution {
        0
    }

    val part2 = Solution {
        0
    }

    companion object {
        val sequenceCache = mutableMapOf<String, String>()
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
            rootSequence.filter { it.isDigit() }.toInt() * getDirectionalSequence().length
        }

        private fun <T> List<T>.permutations(): List<List<T>> {
            return if (this.size == 1) listOf(this)
            else this.flatMap { item -> (this - item).permutations().map { listOf(item) } }
        }

        fun getDirectionalSequence(): String {
            val sequence = when (this) {
                is Directional -> replica.getDirectionalSequence()
                is Numeric -> getDirectionalSequence(sequence)
            }

            return getDirectionalSequence(sequence)
        }

        private fun getDirectionalSequence(sequence: String): String {
            return sequenceCache.getOrPut(sequence) {
                sequence.fold("") { value, char ->
                    char.toString()
                } + 'A'

                val queue = ArrayDeque(sequence.toList())
                ""
            }
        }

//        val directionalSequence: String by lazy {
//            var currentNode = keypad['A']
//            val sequence = when (this) {
//                is Directional -> replica.directionalSequence
//                is Numeric -> sequence
//            }
//
//            val queue = ArrayDeque(sequence.toList())
//
//            buildString {
//                while (queue.isNotEmpty()) {
//                    val nextNode = keypad[queue.removeFirst()]
//                    val (rowDelta, colDelta) = nextNode - currentNode
//                    val horizontal: String = if (colDelta <= 0) {
//                        "<".repeat(colDelta.absoluteValue)
//                    } else {
//                        ">".repeat(colDelta.absoluteValue)
//                    }
//                    val vertical: String = if (rowDelta <= 0) {
//                        "^".repeat(rowDelta.absoluteValue)
//                    } else {
//                        "v".repeat(rowDelta.absoluteValue)
//                    }
//
//                    if (keypad[currentNode + Coordinate(rowDelta, 0)] == ' ') {
//                        append(vertical + horizontal)
//                    } else if (keypad[currentNode + Coordinate(0, colDelta)] == ' ') {
//                        append(horizontal + vertical)
//                    } else {
//                        append(horizontal + vertical)
//                    }
//
//                    currentNode = nextNode
//
//                    append('A')
//                }
//            }
//        }

        fun typeSequence(sequence: String): String = buildString {
            var currentNode = keypad['A']
            for (button in sequence) {
                when (button) {
                    '^' -> currentNode += Direction.North
                    '>' -> currentNode += Direction.East
                    'v' -> currentNode += Direction.South
                    '<' -> currentNode += Direction.West
                    'A' -> append(keypad[currentNode])
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
