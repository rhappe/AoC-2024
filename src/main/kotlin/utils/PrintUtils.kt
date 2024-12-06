package utils

import kotlin.time.TimedValue

fun TimedValue<*>.printAnswer(part: Int) {
    println("Part $part: $value in ${duration.formatSeconds()}")
}
