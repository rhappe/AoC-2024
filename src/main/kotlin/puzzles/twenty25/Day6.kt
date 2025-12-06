package puzzles.twenty25

import api.readInput
import solution.Solution
import utils.printAnswer
import kotlin.time.measureTimedValue

fun main() {
    val input = readInput(day = 6, year = 2025)

    val part1Answer = measureTimedValue { Day6(input).part1.solve() }
    part1Answer.printAnswer(label = "Part 1")

    val part2Answer = measureTimedValue { Day6(input).part2.solve() }
    part2Answer.printAnswer(label = "Part 2")
}

class Day6(input: List<String>) {
    private val untrimmedInput: List<String> = input.maxOf { it.length }.let { longestLength ->
        input.map { it + " ".repeat(longestLength - it.length) }
    }

    val part1 = MathWorksheet.fromRows(untrimmedInput).solution
    val part2 = MathWorksheet.fromCols(untrimmedInput).solution

    enum class MathOperation {
        Add,
        Multiply,
        ;

        operator fun invoke(vararg values: Long): Long {
            return values.drop(1).fold(values.first()) { acc, value ->
                when (this) {
                    Add -> acc + value
                    Multiply -> acc * value
                }
            }
        }

        companion object {
            fun fromChar(char: Char): MathOperation? = when (char) {
                '+' -> Add
                '*' -> Multiply
                else -> null
            }
        }
    }

    private data class MathProblem(val values: List<Long>, val operation: MathOperation) {
        val solution by lazy { operation.invoke(*values.toLongArray()) }
    }

    private class MathWorksheet(private val problems: List<MathProblem>) {
        val solution = Solution { problems.sumOf { it.solution } }

        companion object {
            fun fromRows(input: List<String>): MathWorksheet {
                val operators = input.last().mapNotNull { MathOperation.fromChar(it) }
                val rows = input.dropLast(1).map { line ->
                    line.split(" ")
                        .filter { it.isNotEmpty() }
                        .map { it.toLong() }
                }
                val cols: List<List<Long>> = buildList {
                    for (col in rows[0].indices) {
                        this += buildList {
                            for (row in rows.indices) {
                                add(rows[row][col])
                            }
                        }.toList()
                    }
                }

                return MathWorksheet(
                    problems = cols.mapIndexed { index, values ->
                        MathProblem(
                            values = values,
                            operation = operators[index],
                        )
                    }
                )
            }

            fun fromCols(input: List<String>): MathWorksheet {
                val cols: List<String> = buildList {
                    for (col in input[0].indices.reversed()) {
                        this += buildString {
                            for (row in input.indices) {
                                append(input[row][col])
                            }
                        }
                    }
                }
                val chunks: List<List<String>> = buildList {
                    val currentList = mutableListOf<String>()
                    cols.forEach {
                        if (it.isBlank()) {
                            add(currentList.toList())
                            currentList.clear()
                        } else {
                            currentList.add(it)
                        }
                    }
                    add(currentList.toList())
                }
                return MathWorksheet(
                    problems = chunks.map { column ->
                        column.filter { it.isNotBlank() }.let { rawValues ->
                            MathProblem(
                                values = rawValues.map {
                                    it.filter { char -> char.isDigit() }.toLong()
                                },
                                operation = rawValues.firstNotNullOf { MathOperation.fromChar(it.last()) },
                            )
                        }
                    },
                )
            }
        }
    }
}
