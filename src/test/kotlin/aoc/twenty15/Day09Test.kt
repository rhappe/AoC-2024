package aoc.twenty15

import org.junit.jupiter.api.Test

class Day09Test {
    @Test
    fun `test part 1`() {
        kotlin.test.assertEquals(
            expected = 605,
            actual = Day09(testData).part1.solve(),
        )
    }

    @Test
    fun `test part 2`() {
        kotlin.test.assertEquals(
            expected = 982,
            actual = Day09(testData).part2.solve(),
        )
    }

    private val testData = """
        London to Dublin = 464
        London to Belfast = 518
        Dublin to Belfast = 141
    """.trimIndent().split("\n")
}