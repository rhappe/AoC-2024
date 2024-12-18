package puzzles

import api.readInput
import utils.printAnswer
import kotlin.math.pow
import kotlin.time.measureTimedValue

fun main() {
    val input = readInput(day = 17)

    val partOneAnswer = measureTimedValue { Day17.Part01.foo(input) }
    partOneAnswer.printAnswer(label = "Part 1")

    val partTwoAnswer = measureTimedValue { Day17.Part02.foo(input) }
    partTwoAnswer.printAnswer(label = "Part 2")
}

private object Day17 {
    object Part01 {
        fun foo(input: List<String>): String {
            val completed = parseInput(input).runProgram()
            return completed.output.joinToString(",")
        }
    }

    object Part02 {
        fun foo(input: List<String>): Long? {
            val computer = parseInput(input)
            val registerAValue = reverseEngineerMinRegisterAValue(computer) ?: return null

            // check if it's correct
            val checkResult = computer.copy(registerA = registerAValue).runProgram()
            check(checkResult.output == computer.program) {
                """
                The register A value $registerAValue does not generate the original program sequence.
                Expected: ${computer.program.joinToString(separator = ",")}
                Actual: ${computer.output.joinToString(separator = ",")}
                """.trimIndent()
            }
            return registerAValue
        }

        private fun findPossibleStartingValue(
            previousRegisterAValue: Long,
            computer: Computer,
            expectedOutput: Int,
        ): List<Long> = buildList {
            // take the previous register a value and multiply it by 8. This effectively shifts the
            // bits left by 3 (shl 3 would work too). This works because we are working with a 3 bit
            // number, so anything beyond the first 3 bits doesn't matter for the current iteration.
            // so multiply by 8 each iteration and find a value that generates the desired output.
            val contenders = (0L until 8L).map { (previousRegisterAValue * 8) + it }
            contenders.forEach { contender ->
                val result = computer.copy(registerA = contender).runProgram()
                if (result.output.first() == expectedOutput) {
                    add(contender)
                }
            }
        }

        private fun reverseEngineerMinRegisterAValue(computer: Computer): Long? {
            var startingValueCandidates = listOf(0L)

            computer.program.reversed().forEach { instruction ->
                // use the previous candidate values to build a list of new candidate values
                startingValueCandidates = startingValueCandidates.flatMap { value ->
                    findPossibleStartingValue(value, computer, instruction)
                }
            }

            return startingValueCandidates.sorted().firstOrNull {
                computer.copy(registerA = it).runProgram().output == computer.program
            }
        }
    }

    private fun parseInput(input: List<String>): Computer {
        val registerAString = input[0]
        val registerBString = input[1]
        val registerCString = input[2]
        val programString = input[4]

        return Computer(
            registerA = registerAString.split(": ")[1].toLong(),
            registerB = registerBString.split(": ")[1].toLong(),
            registerC = registerCString.split(": ")[1].toLong(),
            program = programString.split(": ")[1].split(",").map(String::toInt),
        )
    }

    private data class Computer(
        val registerA: Long,
        val registerB: Long,
        val registerC: Long,
        val program: List<Int>,
        val pointer: Int = 0,
        val output: List<Int> = emptyList(),
    ) {
        private val currentInstruction: Instruction? = when (pointer) {
            in program.indices -> Instruction(
                operator = Operator(program[pointer]),
                operand = Operand(program[pointer + 1]),
            )

            else -> null
        }

        fun runProgram(): Computer {
            return currentInstruction?.invoke(this)?.runProgram() ?: this
        }

        override fun toString(): String = buildString {
            append("Output: ${output.joinToString(separator = ",")}")
        }
    }

    private data class Instruction(
        val operator: Operator,
        val operand: Operand,
    ) {
        operator fun invoke(computer: Computer): Computer {
            return operator.invoke(computer, operand)
        }
    }

    private fun Operator(value: Int): Operator = when (value) {
        0 -> Operator.Adv
        1 -> Operator.Bxl
        2 -> Operator.Bst
        3 -> Operator.Jnz
        4 -> Operator.Bxc
        5 -> Operator.Out
        6 -> Operator.Bdv
        7 -> Operator.Cdv
        else -> error("Invalid operator: $value")
    }

    private fun Operand(value: Int): Operand = when (value) {
        in 0..3 -> Operand.Literal(value)
        4 -> Operand.RegisterA(value)
        5 -> Operand.RegisterB(value)
        6 -> Operand.RegisterC(value)
        else -> error("Invalid operand: $value")
    }

    private sealed interface Operator {
        fun invoke(computer: Computer, operand: Operand): Computer

        // opcode 0
        data object Adv : Operator {
            override fun invoke(computer: Computer, operand: Operand): Computer {
                val numerator = computer.registerA
                val denominator = 2.0.pow(operand.comboValue(computer).toDouble())
                return computer.copy(
                    registerA = (numerator / denominator).toLong(),
                    pointer = computer.pointer + 2,
                )
            }
        }

        // opcode 1
        data object Bxl : Operator {
            override fun invoke(computer: Computer, operand: Operand): Computer {
                return computer.copy(
                    registerB = computer.registerB xor operand.literal.toLong(),
                    pointer = computer.pointer + 2,
                )
            }
        }

        // opcode 2
        data object Bst : Operator {
            override fun invoke(computer: Computer, operand: Operand): Computer {
                return computer.copy(
                    registerB = operand.comboValue(computer) % 8,
                    pointer = computer.pointer + 2,
                )
            }
        }

        // opcode 3
        data object Jnz : Operator {
            override fun invoke(computer: Computer, operand: Operand): Computer {
                return when {
                    computer.registerA == 0L -> computer.copy(pointer = computer.pointer + 2)
                    else -> computer.copy(pointer = operand.literal)
                }
            }
        }

        // opcode 4
        data object Bxc : Operator {
            override fun invoke(computer: Computer, operand: Operand): Computer {
                return computer.copy(
                    registerB = computer.registerB xor computer.registerC,
                    pointer = computer.pointer + 2,
                )
            }
        }

        // opcode 5
        data object Out : Operator {
            override fun invoke(computer: Computer, operand: Operand): Computer {
                return computer.copy(
                    pointer = computer.pointer + 2,
                    output = computer.output + (operand.comboValue(computer) % 8).toInt(),
                )
            }
        }

        // opcode 6
        data object Bdv : Operator {
            override fun invoke(computer: Computer, operand: Operand): Computer {
                val numerator = computer.registerA
                val denominator = 2.0.pow(operand.comboValue(computer).toDouble())
                return computer.copy(
                    registerB = (numerator / denominator).toLong(),
                    pointer = computer.pointer + 2,
                )
            }

        }

        // opcode 7
        data object Cdv : Operator {
            override fun invoke(computer: Computer, operand: Operand): Computer {
                val numerator = computer.registerA
                val denominator = 2.0.pow(operand.comboValue(computer).toDouble())
                return computer.copy(
                    registerC = (numerator / denominator).toLong(),
                    pointer = computer.pointer + 2,
                )
            }
        }
    }

    private sealed interface Operand {
        val literal: Int

        fun comboValue(computer: Computer): Long

        data class Literal(override val literal: Int) : Operand {
            override fun comboValue(computer: Computer): Long {
                return literal.toLong()
            }
        }

        data class RegisterA(override val literal: Int) : Operand {
            override fun comboValue(computer: Computer): Long {
                return computer.registerA
            }
        }

        data class RegisterB(override val literal: Int) : Operand {
            override fun comboValue(computer: Computer): Long {
                return computer.registerB
            }
        }

        data class RegisterC(override val literal: Int) : Operand {
            override fun comboValue(computer: Computer): Long {
                return computer.registerC
            }
        }
    }
}
