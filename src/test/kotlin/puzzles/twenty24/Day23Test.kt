package puzzles.twenty24

import kotlin.test.Test
import kotlin.test.assertEquals

class Day23Test {
    private val networkMapInput = listOf(
        "kh-tc",
        "qp-kh",
        "de-cg",
        "ka-co",
        "yn-aq",
        "qp-ub",
        "cg-tb",
        "vc-aq",
        "tb-ka",
        "wh-tc",
        "yn-cg",
        "kh-ub",
        "ta-co",
        "de-co",
        "tc-td",
        "tb-wq",
        "wh-td",
        "ta-ka",
        "td-qp",
        "aq-cg",
        "wq-ub",
        "ub-vc",
        "de-ta",
        "wq-aq",
        "wq-vc",
        "wh-yn",
        "ka-de",
        "kh-ta",
        "co-tc",
        "wh-qp",
        "tb-vc",
        "td-yn",
    )

    @Test
    fun `Solve part 1 with fake input`() {
        val day23 = Day23(networkMapInput)

        assertEquals(
            expected = 7,
            actual = day23.part1.solve(),
        )
    }

    @Test
    fun `Solve part 2 with fake input`() {
        val day23 = Day23(networkMapInput)

        assertEquals(
            expected = "co,de,ka,ta",
            actual = day23.part2.solve(),
        )
    }
}