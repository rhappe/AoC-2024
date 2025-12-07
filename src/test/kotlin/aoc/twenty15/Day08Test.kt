package aoc.twenty15

import org.junit.jupiter.api.Test

class Day08Test {
    @Test
    fun `test part 1`() {
        kotlin.test.assertEquals(
            expected = 12,
            actual = Day08(testData).part1.solve(),
        )
    }

    @Test
    fun `test part 2`() {
        kotlin.test.assertEquals(
            expected = 19,
            actual = Day08(testData).part2.solve(),
        )
    }

    private val testData = """
        ""
        "abc"
        "aaa\"aaa"
        "\x27"
    """.trimIndent().split("\n")
}