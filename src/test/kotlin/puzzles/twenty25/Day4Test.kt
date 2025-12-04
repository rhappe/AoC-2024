package puzzles.twenty25

import kotlin.test.Test

class Day4Test {
    @Test
    fun `test part 1`() {
        kotlin.test.assertEquals(
            expected = 13,
            actual = Day4(testData).part1.solve(),
        )
    }

    @Test
    fun `test part 2`() {
        kotlin.test.assertEquals(
            expected = 43,
            actual = Day4(testData).part2.solve(),
        )
    }

    private val testData = """
        ..@@.@@@@.
        @@@.@.@.@@
        @@@@@.@.@@
        @.@@@@..@.
        @@.@@@@.@@
        .@@@@@@@.@
        .@.@.@.@@@
        @.@@@.@@@@
        .@@@@@@@@.
        @.@.@@@.@.
    """.trimIndent().split("\n")
}