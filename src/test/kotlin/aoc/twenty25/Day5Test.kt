package aoc.twenty25

import kotlin.test.Test

class Day5Test {
    @Test
    fun `test part 1`() {
        kotlin.test.assertEquals(
            expected = 3,
            actual = Day5(testData).part1.solve(),
        )
    }

    @Test
    fun `test part 2`() {
        kotlin.test.assertEquals(
            expected = 14,
            actual = Day5(testData).part2.solve(),
        )
    }

    private val testData = """
        3-5
        10-14
        16-20
        12-18

        1
        5
        8
        11
        17
        32
    """.trimIndent().split("\n")
}