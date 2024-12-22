package model

class Grid<T>(data: List<List<T>>) : List<List<T>> by data {
    private val valueIndex: Map<T, IntCoordinate> by lazy {
        mapValuesIndexed { coordinate, item ->
            item to coordinate
        }.toMap()
    }

    operator fun get(value: T): IntCoordinate {
        return valueIndex.getValue(value)
    }

    operator fun get(coordinate: IntCoordinate): T {
        return this[coordinate.row, coordinate.col]
    }

    operator fun get(row: Int, col: Int): T {
        return this[row][col]
    }

    operator fun contains(coordinate: IntCoordinate): Boolean {
        return coordinate.row in indices && coordinate.col in first().indices
    }

    inline fun forEachCoordinate(block: (IntCoordinate) -> Unit) {
        for (row in indices) {
            for (col in this[row].indices) {
                block(Coordinate(row, col))
            }
        }
    }

    inline fun forEachValue(block: (T) -> Unit) = forEachCoordinate {
        block(this[it])
    }

    inline fun forEachIndexed(block: (IntCoordinate, T) -> Unit) = forEachCoordinate {
        block(it, this[it])
    }

    inline fun <R> mapCoordinates(block: (IntCoordinate) -> R): List<R> {
        val grid = this
        return buildList {
            for (row in grid.indices) {
                for (col in grid[row].indices) {
                    add(block(Coordinate(row, col)))
                }
            }
        }
    }

    inline fun <R> mapValues(block: (T) -> R) = mapCoordinates {
        block(this[it])
    }

    inline fun <R> mapValuesIndexed(block: (IntCoordinate, T) -> R) = mapCoordinates {
        block(it, this[it])
    }
}

fun <T, R> Grid(data: List<List<T>>, transform: (IntCoordinate, T) -> R): Grid<R> = Grid(
    data = buildList {
        for (row in data.indices) {
            add(
                buildList {
                    for (col in data[row].indices) {
                        add(transform(Coordinate(row, col), data[row][col]))
                    }
                }
            )
        }
    },
)

fun Grid(strings: List<String>): Grid<Char> = Grid(
    data = strings.map { it.toList() },
)
