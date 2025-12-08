package aoc.twenty15

import org.junit.jupiter.api.Test

class Day14Test {
    @Test
    fun `test part 1`() {
        kotlin.test.assertEquals(
            expected = 1120,
            actual = Day14(testData, 1000).part1.solve(),
        )
    }

    @Test
    fun `test part 2`() {
        kotlin.test.assertEquals(
            expected = 689,
            actual = Day14(testData, 1000).part2.solve(),
        )
    }

    private val testData = """
        Comet can fly 14 km/s for 10 seconds, but then must rest for 127 seconds.
        Dancer can fly 16 km/s for 11 seconds, but then must rest for 162 seconds.
    """.trimIndent().split("\n")
}