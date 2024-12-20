package model

class Grid<T>(data: List<List<T>>) : List<List<T>> by data {
    operator fun get(coordinate: Coordinate<Int>): T {
        return this[coordinate.row, coordinate.col]
    }

    operator fun get(row: Int, col: Int): T {
        return this[row][col]
    }

    operator fun contains(coordinate: Coordinate<Int>): Boolean {
        return coordinate.row in indices && coordinate.col in first().indices
    }

    inline fun forEachCoordinate(block: (Coordinate<Int>) -> Unit) {
        for (row in indices) {
            for (col in this[row].indices) {
                block(Coordinate(row, col))
            }
        }
    }

    inline fun forEachValue(block: (T) -> Unit) = forEachCoordinate {
        block(this[it])
    }

    inline fun forEachIndexed(block: (Coordinate<Int>, T) -> Unit) = forEachCoordinate {
        block(it, this[it])
    }

    inline fun <T> mapCoordinates(block: (Coordinate<Int>) -> T): List<T> {
        val grid = this
        return buildList {
            for (row in grid.indices) {
                for (col in grid[row].indices) {
                    add(block(Coordinate(row, col)))
                }
            }
        }
    }

    inline fun mapValues(block: (T) -> Unit) = mapCoordinates {
        block(this[it])
    }

    inline fun mapValuesIndexed(block: (Coordinate<Int>, T) -> Unit) = mapCoordinates {
        block(it, this[it])
    }
}

fun <T, R> Grid(data: List<List<T>>, transform: (Coordinate<Int>, T) -> R): Grid<R> = Grid(
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
