package puzzles

import api.readInput
import utils.printAnswer
import kotlin.time.DurationUnit
import kotlin.time.measureTimedValue

fun main() {
    val input = readInput(day = 16)

    val partOneAnswer = measureTimedValue { Day16.Part01.foo(input) }
    partOneAnswer.printAnswer(label = "Part 2")

    val partTwoAnswer = measureTimedValue { Day16.Part02.foo(input) }
    partTwoAnswer.printAnswer(label = "Part 2")
}

private object Day16 {
    object Part01 {
        fun foo(input: List<String>): Int {
            TODO()
        }
    }

    object Part02 {
        fun foo(input: List<String>): Int {
            TODO()
        }
    }
}
