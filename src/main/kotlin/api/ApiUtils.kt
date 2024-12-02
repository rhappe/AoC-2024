package api

import com.soberg.kotlin.aoc.api.AdventOfCodeInputApi
import kotlin.io.path.Path
import kotlin.io.path.exists
import kotlin.io.path.readText

const val API_LINE_ITEM_SEPARATOR = "   "

fun AdventOfCodeInputApi(cacheDir: String = "input") = AdventOfCodeInputApi(
    cachingStrategy = AdventOfCodeInputApi.CachingStrategy.LocalTextFile(cacheDir),
)

fun readInput(day: Int): List<String> {
    val result = AdventOfCodeInputApi().blockingReadInput(
        year = 2024,
        day = day,
        sessionToken = readSessionToken(),
    )
    return result.getOrThrow()
}

private fun readSessionToken(): String {
    val secretTokenFile = Path(path = "session-token.secret")
    require(secretTokenFile.exists()) { "Session token file does not exist." }
    return secretTokenFile.readText().trim()
}