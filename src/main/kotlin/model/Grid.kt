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
            grid.forEachCoordinate { coordinate ->
                add(block(coordinate))
            }
        }
    }

    inline fun <R> mapValues(block: (T) -> R) = mapCoordinates {
        block(this[it])
    }

    inline fun <R> mapValuesIndexed(block: (IntCoordinate, T) -> R) = mapCoordinates {
        block(it, this[it])
    }

    inline fun filterCoordinatesIndexed(predicate: (IntCoordinate, T) -> Boolean): List<IntCoordinate> {
        val grid = this
        return buildList {
            grid.forEachIndexed { coordinate, value ->
                if (predicate(coordinate, value)) {
                    add(coordinate)
                }
            }
        }
    }

    fun <R> mapGridValues(block: (IntCoordinate, T) -> R): Grid<R> {
        val values = buildList<List<R>> {
            for (row in this@Grid.indices) {
                this += buildList<R> {
                    for (col in this@Grid[row].indices) {
                        add(block(Coordinate(row, col), this@Grid[row, col]))
                    }
                }
            }
        }

        return Grid(values)
    }
}

fun <T> Grid(rows: Int, cols: Int, init: (Int, Int) -> T): Grid<T> {
    val data = List(rows) { row ->
        List(cols) { col ->
            init(row, col)
        }
    }

    return Grid(data)
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
