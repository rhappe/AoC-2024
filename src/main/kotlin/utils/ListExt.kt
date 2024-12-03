package utils

fun <T, R> Collection<List<T>>.mapValues(block: (T) -> R): List<List<R>> {
    return map { list -> list.map(block) }
}
