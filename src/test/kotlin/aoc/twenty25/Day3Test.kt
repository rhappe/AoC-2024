package aoc.twenty25

import kotlin.test.Test

class Day3Test {
    @Test
    fun `test part 1`() {
        kotlin.test.assertEquals(
            expected = 357,
            actual = Day3(testData).part1.solve(),
        )
    }

    @Test
    fun `test part 2`() {
        kotlin.test.assertEquals(
            expected = 3121910778619,
            actual = Day3(testData).part2.solve(),
        )
    }

    private val testData = """
      987654321111111
      811111111111119
      234234234234278
      818181911112111
  """.trimIndent().split("\n")
}