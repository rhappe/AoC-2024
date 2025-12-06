package puzzles.twenty25

import kotlin.test.Test

class Day6Test {
    @Test
    fun `test part 1`() {
        kotlin.test.assertEquals(
            expected = 4277556,
            actual = Day6(testData).part1.solve(),
        )
    }

    @Test
    fun `test part 2`() {
        kotlin.test.assertEquals(
            expected = 3263827,
            actual = Day6(testData).part2.solve(),
        )
    }

    private val testData = """
        123 328  51 64 
         45 64  387 23 
          6 98  215 314
        *   +   *   +  
    """.trimIndent().split("\n")
}