package puzzles.twenty25

import kotlin.test.Test
import kotlin.test.assertEquals

class Day2Test {
    @Test
    fun `test part 1`() {
        assertEquals(
            expected = 1227775554L,
            actual = Day2(testData).part1.solve(),
        )
    }
    @Test
    fun `test part 2`() {
        assertEquals(
            expected = 4174379265L,
            actual = Day2(testData).part2.solve(),
        )
    }

    private val testData = "11-22,95-115,998-1012,1188511880-1188511890,222220-222224,1698522-1698528,446443-446449,38593856-38593862,565653-565659,824824821-824824827,2121212118-2121212124"
}