package aoc.twenty15

import aoc.api.readInput
import solution.Solution
import utils.printAnswer
import kotlin.time.measureTimedValue

fun main() {
    val input = readInput(day = 7, year = 2015)

    val partOneAnswer = measureTimedValue { Day7(input).part1.solve() }
    partOneAnswer.printAnswer(label = "Part 1")

    val partTwoAnswer = measureTimedValue { Day7(input).part2(partOneAnswer.value).solve() }
    partTwoAnswer.printAnswer(label = "Part 2")
}

class Day7(private val input: List<String>) {
    val part1 = Solution {
        Circuit(input).calculateValue("a")
    }

    fun part2(partOneAnswer: Int) = Solution {
        Circuit(input, mapOf("b" to partOneAnswer)).calculateValue("a")
    }

    private class Circuit(input: List<String>, wires: Map<String, Int> = emptyMap()) {
        private val operations = input.map { Operation.parse(it) }
        private val wires = wires.toMutableMap()

        fun calculateValue(wireName: String): Int {
            return wires.getOrPut(wireName) {
                when (val operation = operations.single { it.destination.name == wireName }) {
                    is Operation.And -> getValue(operation.left) and getValue(operation.right)
                    is Operation.LShift -> getValue(operation.left) shl getValue(operation.right)
                    is Operation.Not -> getValue(operation.value).toUInt().inv().toInt()
                    is Operation.Or -> getValue(operation.left) or getValue(operation.right)
                    is Operation.RShift -> getValue(operation.left) ushr getValue(operation.right)
                    is Operation.Signal -> getValue(operation.value)
                }
            }
        }

        private fun getValue(value: Value): Int {
            return when (value) {
                is Value.Constant -> value.value
                is Value.Wire -> calculateValue(value.name)
            }
        }
    }

    sealed interface Value {
        data class Constant(val value: Int) : Value

        data class Wire(val name: String) : Value

        companion object {
            fun parse(string: String): Value {
                return string.toIntOrNull()?.let { Constant(it) } ?: Wire(string)
            }
        }
    }

    sealed interface Operation {
        val destination: Value.Wire

        data class Signal(
            val value: Value,
            override val destination: Value.Wire,
        ) : Operation

        data class And(
            val left: Value,
            val right: Value,
            override val destination: Value.Wire,
        ) : Operation

        data class Or(
            val left: Value,
            val right: Value,
            override val destination: Value.Wire,
        ) : Operation

        data class LShift(
            val left: Value,
            val right: Value,
            override val destination: Value.Wire,
        ) : Operation

        data class RShift(
            val left: Value,
            val right: Value,
            override val destination: Value.Wire,
        ) : Operation

        data class Not(
            val value: Value,
            override val destination: Value.Wire,
        ) : Operation

        companion object {
            private val signalRegex = "^(.+) -> (.+)".toRegex()
            private val andRegex = "(.+) AND (.+) -> (.+)".toRegex()
            private val orRegex = "(.+) OR (.+) -> (.+)".toRegex()
            private val notRegex = "NOT (.+) -> (.+)".toRegex()
            private val lShiftRegex = "(.+) LSHIFT (.+) -> (.+)".toRegex()
            private val rShiftRegex = "(.+) RSHIFT (.+) -> (.+)".toRegex()

            fun parse(string: String): Operation {
                val andMatch = andRegex.findAll(string).singleOrNull()
                if (andMatch != null) {
                    return And(
                        left = Value.parse(andMatch.groupValues[1]),
                        right = Value.parse(andMatch.groupValues[2]),
                        destination = Value.Wire(andMatch.groupValues[3]),
                    )
                }

                val orMatch = orRegex.findAll(string).singleOrNull()
                if (orMatch != null) {
                    return Or(
                        left = Value.parse(orMatch.groupValues[1]),
                        right = Value.parse(orMatch.groupValues[2]),
                        destination = Value.Wire(orMatch.groupValues[3]),
                    )
                }

                val notMatch = notRegex.findAll(string).singleOrNull()
                if (notMatch != null) {
                    return Not(
                        value = Value.parse(notMatch.groupValues[1]),
                        destination = Value.Wire(notMatch.groupValues[2]),
                    )
                }

                val lShiftMatch = lShiftRegex.findAll(string).singleOrNull()
                if (lShiftMatch != null) {
                    return LShift(
                        left = Value.parse(lShiftMatch.groupValues[1]),
                        right = Value.parse(lShiftMatch.groupValues[2]),
                        destination = Value.Wire(lShiftMatch.groupValues[3]),
                    )
                }

                val rShiftMatch = rShiftRegex.findAll(string).singleOrNull()
                if (rShiftMatch != null) {
                    return RShift(
                        left = Value.parse(rShiftMatch.groupValues[1]),
                        right = Value.parse(rShiftMatch.groupValues[2]),
                        destination = Value.Wire(rShiftMatch.groupValues[3]),
                    )
                }

                val signalMatch = signalRegex.findAll(string).singleOrNull()
                if (signalMatch != null) {
                    return Signal(
                        value = Value.parse(signalMatch.groupValues[1]),
                        destination = Value.Wire(signalMatch.groupValues[2]),
                    )
                }

                error("Could not parse string into operation: $string")
            }
        }
    }
}
