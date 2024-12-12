package model

enum class Direction {
    North,
    East,
    South,
    West,
    ;

    fun rotateClockwise(): Direction = when (this) {
        North -> East
        East -> South
        South -> West
        West -> North
    }
}
