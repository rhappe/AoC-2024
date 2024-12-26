package puzzles.twenty24

import kotlin.math.exp
import kotlin.test.Test
import kotlin.test.assertEquals

class Day25Test {
    @Test
    fun `Schematics are read properly`() {
        val day25 = Day25(testInput)

        assertEquals(
            expected = listOf(
                Day25.Schematic.Lock(0, 5, 3, 4, 3),
                Day25.Schematic.Lock(1, 2, 0, 5, 3),
                Day25.Schematic.Key(5, 0, 2, 1, 3),
                Day25.Schematic.Key(4, 3, 4, 0, 2),
                Day25.Schematic.Key(3, 0, 2, 0, 1),
            ),
            actual = day25.schematics,
        )
    }

    @Test
    fun `Test part 1 with test input`() {
        val day25 = Day25(testInput)
        assertEquals(
            expected = 3,
            actual = day25.part1.solve(),
        )
    }

    private val testInput = """
        #####
        .####
        .####
        .####
        .#.#.
        .#...
        .....

        #####
        ##.##
        .#.##
        ...##
        ...#.
        ...#.
        .....

        .....
        #....
        #....
        #...#
        #.#.#
        #.###
        #####

        .....
        .....
        #.#..
        ###..
        ###.#
        ###.#
        #####

        .....
        .....
        .....
        #....
        #.#..
        #.#.#
        #####
    """.trimIndent().split("\n")
}