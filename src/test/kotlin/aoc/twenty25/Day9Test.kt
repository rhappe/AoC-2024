package aoc.twenty25

import kotlin.test.Test

class Day9Test {
    @Test
    fun `test part 1`() {
        kotlin.test.assertEquals(
            expected = 50,
            actual = Day9(testData).part1.solve(),
        )
    }

    @Test
    fun `test part 2`() {
        kotlin.test.assertEquals(
            expected = 24,
            actual = Day9(testData).part2.solve(),
        )
    }

    private val testData = """
        7,1
        11,1
        11,7
        9,7
        9,5
        2,5
        2,3
        7,3
    """.trimIndent().split("\n")
}