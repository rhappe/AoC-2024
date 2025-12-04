package model

interface Direction {
    val horizontalDelta: Int
    val verticalDelta: Int

    fun rotate(clockwise: Boolean, count: Int = 2): Direction

    companion object {
        val North = CardinalDirection.Primary.North
        val East = CardinalDirection.Primary.East
        val South = CardinalDirection.Primary.South
        val West = CardinalDirection.Primary.West

        val cardinals = CardinalDirection.Companion
    }
}

operator fun Direction.plus(other: Direction): Direction = DirectionImpl(
    horizontalDelta = horizontalDelta + other.horizontalDelta,
    verticalDelta = verticalDelta + other.verticalDelta,
)

private data class DirectionImpl(
    override val horizontalDelta: Int = 0,
    override val verticalDelta: Int = 0,
) : Direction {
    private fun rotateOnce(clockwise: Boolean): Direction = when {
        clockwise -> DirectionImpl(
            horizontalDelta = if (verticalDelta < 0) -verticalDelta else verticalDelta,
            verticalDelta = if (horizontalDelta < 0) horizontalDelta else -horizontalDelta,
        )

        else -> DirectionImpl(
            horizontalDelta = if (verticalDelta < 0) verticalDelta else -verticalDelta,
            verticalDelta = if (horizontalDelta < 0) -horizontalDelta else horizontalDelta,
        )
    }

    override fun rotate(clockwise: Boolean, count: Int): Direction = when {
        count == 0 -> this
        count < 1 -> rotateOnce(clockwise).rotate(clockwise, count + 1)
        else -> rotateOnce(clockwise).rotate(clockwise, count - 1)
    }
}

interface Cardinals {
    val primaries: List<CardinalDirection>
    val secondaries: List<CardinalDirection>
}

sealed interface CardinalDirection : Direction {

    enum class Primary(private val direction: Direction) : CardinalDirection, Direction by direction {
        North(DirectionImpl(verticalDelta = -1)),
        East(DirectionImpl(horizontalDelta = 1)),
        South(DirectionImpl(verticalDelta = 1)),
        West(DirectionImpl(horizontalDelta = -1)),
        ;

        override fun rotate(clockwise: Boolean, count: Int): CardinalDirection {
            return super.rotate(clockwise, count)
        }
    }

    enum class Secondary(private val direction: Direction) : CardinalDirection, Direction by direction {
        NorthEast(Primary.North + Primary.East),
        SouthEast(Primary.South + Primary.East),
        NorthWest(Primary.North + Primary.West),
        SouthWest(Primary.South + Primary.West),
        ;

        override fun rotate(clockwise: Boolean, count: Int): CardinalDirection {
            return super.rotate(clockwise, count)
        }
    }

    private fun rotateOnce(clockwise: Boolean): CardinalDirection = when (this) {
        Primary.North -> if (clockwise) Secondary.NorthEast else Secondary.NorthWest
        Secondary.NorthEast -> if (clockwise) Primary.East else Primary.North
        Primary.East -> if (clockwise) Secondary.SouthEast else Secondary.NorthEast
        Secondary.SouthEast -> if (clockwise) Primary.South else Primary.East
        Primary.South -> if (clockwise) Secondary.SouthWest else Secondary.SouthEast
        Secondary.SouthWest -> if (clockwise) Primary.West else Primary.South
        Primary.West -> if (clockwise) Secondary.NorthWest else Secondary.SouthWest
        Secondary.NorthWest -> if (clockwise) Primary.North else Primary.West
    }

    override fun rotate(clockwise: Boolean, count: Int): CardinalDirection = when {
        count == 0 -> this
        count < 1 -> rotateOnce(clockwise).rotate(clockwise, count + 1)
        else -> rotateOnce(clockwise).rotate(clockwise, count - 1)
    }

    companion object : Cardinals, List<CardinalDirection> by Primary.entries + Secondary.entries {
        override val primaries: List<Primary> = Primary.entries
        override val secondaries: List<Secondary> = Secondary.entries
    }
}
