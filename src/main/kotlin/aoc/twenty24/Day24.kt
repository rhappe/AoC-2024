package aoc.twenty24

import aoc.api.readInput
import solution.Solution
import utils.printAnswer
import kotlin.time.measureTimedValue

fun main() {
    val input = readInput(day = 24)

    val partOneAnswer = measureTimedValue { Day24(input).part1.solve() }
    partOneAnswer.printAnswer(label = "Part 1")

    val partTwoAnswer = measureTimedValue { Day24(input).part2.solve() }
    partTwoAnswer.printAnswer(label = "Part 2")
}

class Day24(input: List<String>) {
    val part1 = Solution {
        parseLogicSystem(input).outputValue
    }

    val part2 = Solution { 0 }

    private fun parseLogicSystem(input: List<String>): LogicSystem {
        val (values, gates) = input.indexOf("").let {
            input.subList(0, it) to input.subList(it + 1, input.size)
        }

        return LogicSystem(
            nodes = gates.map { line ->
                val parts = line.split(" ").filter { it != "->" }
                LogicNode(
                    gate = when (val gate = parts[1]) {
                        "AND" -> LogicGate.And
                        "OR" -> LogicGate.Or()
                        "XOR" -> LogicGate.Or(exclusive = true)
                        else -> error("Invalid logic gate: $gate")
                    },
                    inputs = listOf(parts[0], parts[2]),
                    output = parts[3],
                )
            },
            inputValues = values.associate { line ->
                line.split(": ").let {
                    it[0] to it[1].toInt()
                }
            },
        )
    }

    class LogicSystem(
        private val nodes: List<LogicNode>,
        inputValues: Map<String, Int>,
    ) {
        private val values = inputValues.toMutableMap()

        init {
            nodes.filter { it.output.startsWith(prefix = "z") }.forEach { node ->
                values[node.output] = getWireValue(node)
            }
        }

        private fun getIOValue(type: Char): Long = values.filter { it.key.startsWith(prefix = type.toString()) }
            .toSortedMap().values
            .reversed()
            .joinToString(separator = "")
            .toLong(radix = 2)

        val inputValues: Pair<Long, Long> = getIOValue(type = 'x') to getIOValue(type = 'y')

        val outputValue: Long = getIOValue(type = 'z')

        private fun getWireValue(node: LogicNode): Int {
            if (node.output in values.keys) {
                return values.getValue(node.output)
            }

            val inputs = node.inputs.map { wire ->
                values[wire] ?: getWireValue(node = nodes.first { it.output == wire })
            }

            val result = node.gate.invoke(inputs)
            values[node.output] = result
            return result
        }
    }

    data class LogicNode(
        val gate: LogicGate,
        val inputs: List<String>,
        val output: String,
    )

    sealed interface LogicGate {
        fun invoke(inputs: List<Int>): Int

        data object And : LogicGate {
            override fun invoke(inputs: List<Int>): Int = inputs.reduce { current, next ->
                current and next
            }
        }

        data class Or(val exclusive: Boolean = false) : LogicGate {
            override fun invoke(inputs: List<Int>): Int = inputs.reduce { current, next ->
                if (exclusive) current xor next else current or next
            }
        }
    }
}