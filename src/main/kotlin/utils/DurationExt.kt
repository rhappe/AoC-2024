package utils

import kotlin.time.Duration
import kotlin.time.DurationUnit

fun Duration.formatSeconds(decimals: Int = 2): String {
    return toString(DurationUnit.SECONDS, decimals)
}
