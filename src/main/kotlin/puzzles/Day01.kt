package puzzles

private data class LocationsData(
    val list1: List<Int>,
    val list2: List<Int>,
)

fun main() {
    val locationData = LocationsData(
        list1 = listOf(3, 4, 2, 1, 3, 3),
        list2 = listOf(4, 3, 5, 3, 9, 3),
    )

    val sortedList1 = locationData.list1.sorted()
    val sortedList2 = locationData.list2.sorted()

    val differences = sortedList1.zip(sortedList2).map { it.second - it.first }
    val sum = differences.sum()

    println("Sum: $sum")
}
