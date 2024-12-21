package utils

import kotlinx.coroutines.*

suspend fun <T, R> List<T>.mapDeferred(block: suspend (T) -> R): List<R> {
    return map { item ->
        coroutineScope {
            async { block(item) }
        }
    }.awaitAll()
}

suspend fun <T, R> List<T>.flatMapDeferred(block: suspend (T) -> List<R>): List<R> {
    return map { item ->
        coroutineScope {
            async(Dispatchers.Default) {
                block(item)
            }
        }
    }.awaitAll().flatten()
}

