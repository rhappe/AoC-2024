package puzzles.twenty24

import api.readInput
import com.soberg.aoc.utlities.extensions.asyncSumOfBlocking
import kotlinx.coroutines.runBlocking
import utils.printAnswer
import kotlin.math.pow
import kotlin.time.measureTimedValue

fun main() = runBlocking {
    val input = readInput(day = 7)

    val partOneAnswer = measureTimedValue { Day07.Part01.getCorrectResultSum(input) }
    partOneAnswer.printAnswer(label = "Part 1")

    val partTwoAnswer = measureTimedValue { Day07.Part02.getCorrectResultSum(input) }
    partTwoAnswer.printAnswer(label = "Part 2")
}

private object Day07 {
    enum class Operator : (Long, Long) -> Long {
        ADD, MULTIPLY, CONCAT, ;

        override fun invoke(p1: Long, p2: Long): Long = when (this) {
            ADD -> p1 + p2
            MULTIPLY -> p1 * p2
            CONCAT -> (p1.toString() + p2.toString()).toLong()
        }
    }


    object Part01 {
        fun getCorrectResultSum(input: List<String>): Long {
            val equations = parseEquations(input)
            return equations.asyncSumOfBlocking { equation ->
                val result = equation.checkCalculation(Operator.ADD, Operator.MULTIPLY)
                if (result) equation.result else 0
            }
        }
    }

    object Part02 {
        fun getCorrectResultSum(input: List<String>): Long {
            val equations = parseEquations(input)
            return equations.asyncSumOfBlocking { equation ->
                val result = equation.checkCalculation(Operator.ADD, Operator.MULTIPLY, Operator.CONCAT)
                if (result) equation.result else 0
            }
        }
    }

    private fun Equation.checkCalculation(vararg supportedOperators: Operator): Boolean {
        val numOperators = parts.size - 1
        val combinations = (supportedOperators.size.toDouble().pow(numOperators) - 1).toInt()
        for (attempt in 0..combinations) {
            val operators = getOperatorsForAttempt(attempt, numOperators, supportedOperators.toList())
            val partsQueue = ArrayDeque(parts)
            var answer = partsQueue.removeFirst().toLong()
            operators.forEach {
                answer = it.invoke(answer, partsQueue.removeFirst().toLong())
            }
            if (answer == result) {
                return true
            }
        }
        return false
    }

    private fun getOperatorsForAttempt(
        attempt: Int,
        totalOperators: Int,
        supportedOperator: List<Operator>,
    ): List<Operator> {
        val paddedBaseNumber = attempt.toString(supportedOperator.size).padStart(totalOperators, '0')
        return paddedBaseNumber.map {
            when (it) {
                '0' -> supportedOperator[0]
                '1' -> supportedOperator[1]
                '2' -> supportedOperator[2]
                else -> error("Invalid operator identifier: $it")
            }
        }
    }


    private fun parseEquations(input: List<String>): List<Equation> = input.map { line ->
        line.split(": ").let { split ->
            Equation(
                result = split[0].toLong(),
                parts = split[1].split(" ").map { it.toInt() },
            )
        }
    }

    data class Equation(
        val result: Long,
        val parts: List<Int>,
    )
}