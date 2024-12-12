package utils

import kotlin.time.Duration
import kotlin.time.TimedValue
import kotlin.time.measureTimedValue

fun TimedValue<*>.printAnswer(
    label: String,
    formatDuration: (Duration) -> String = { it.formatSeconds(decimals = 4) },
) = println(
    message = "$label: $value; Duration: ${formatDuration(duration)}",
)

fun <T> runTimedMeasurement(
    label: String,
    formatDuration: (Duration) -> String = { it.formatSeconds(decimals = 4) },
    block: () -> T
) {
    measureTimedValue(block).printAnswer(label, formatDuration)
}