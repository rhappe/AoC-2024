package puzzles

import kotlin.test.Test
import kotlin.test.assertEquals

class Day21Test {
    @Test
    fun `numeric keyboard outputs correct directional sequence`() {
        val numericKeypad = Day21.Keypad.Numeric(sequence = "029A")

        assertEquals(
            expected = "<A^A^^>AvvvA",
            actual = numericKeypad.getDirectionalSequence(),
        )
    }

    @Test
    fun `single keypad controller outputs correct directional sequence`() {
        val numericKeypad = Day21.Keypad.Numeric(sequence = "029A")
        val directionalKeypad = Day21.Keypad.Directional(replica = numericKeypad)

        assertEquals(
            expected = "v<<A>>^A<A>A<AAv>A^Av<AAA^>A",
            actual = directionalKeypad.getDirectionalSequence(),
        )
    }

    @Test
    fun `quadruple nested keypad controller outputs correct directional sequence`() {
        val sequences = listOf("029A", "980A", "179A", "456A", "379A")
        val totalComplexity = sequences.map {
            val robot1 = Day21.Keypad.Numeric(sequence = it)
            val robot2 = Day21.Keypad.Directional(replica = robot1)
            Day21.Keypad.Directional(replica = robot2)
        }

        assertEquals(
            expected = 126384,
            actual = totalComplexity.sumOf { it.complexityScore },
        )
    }
}