package puzzles

import api.readInput
import kotlin.math.absoluteValue

fun main() {
    val input = readInput(day = 2)
    val reports = Day02.parseInput(input)
    println(reports)
    println(Day02.Part01.calculateSafeReportCount(reports))
}

private object Day02 {
    fun parseInput(input: List<String>): List<RedNosedReport> {
        return input.map { reportData ->
            RedNosedReport(
                values = reportData.split(" ").map { it.toInt() },
            )
        }
    }

    object Part01 {
        fun calculateSafeReportCount(reports: List<RedNosedReport>): Int {
            val safetyMeasurements = reports.map { it.calculateSafety() }
            return safetyMeasurements.count { it == RedNosedReportSafety.Safe }
        }
    }

    data class RedNosedReport(
        val values: List<Int>,
    )

    private fun RedNosedReport.calculateSafety(): RedNosedReportSafety {
        val differenceValues = buildList {
            for (i in 0 until (values.size - 1)) {
                add(values[i + 1] - values[i])
            }
        }

        // if all the values are positive or all of the values are negative, then all are increasing or decreasing
        // then, if all differ by 1-3, they are safe. Otherwise, unsafe.
        return when {
            (differenceValues.all { it > 0 } || differenceValues.all { it < 0 }) -> {
                if (differenceValues.all { it.absoluteValue in 1..3 }) {
                    RedNosedReportSafety.Safe
                } else {
                    RedNosedReportSafety.Unsafe
                }
            }

            else -> RedNosedReportSafety.Unsafe
        }
    }

    enum class RedNosedReportSafety {
        Safe,
        Unsafe,
    }
}
