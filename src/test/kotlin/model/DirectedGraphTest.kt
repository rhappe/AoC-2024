package model

import kotlin.test.Test
import kotlin.test.assertEquals

class DirectedGraphTest {
    @Test
    fun foo() {
        val graph = DirectedGraph(
            values = setOf(2, 1, 3, 4, 5, 6),
            neighborsBlock = {
                when (it) {
                    1 -> setOf(2, 5)
                    2 -> setOf(1, 3, 5)
                    3 -> setOf(2, 4)
                    4 -> setOf(3, 5, 6)
                    5 -> setOf(1, 2, 4)
                    6 -> setOf(4)
                    else -> error("Invalid node: $it")
                }
            },
        )

        assertEquals(
            expected = listOf(
                setOf(2, 1, 5),
                setOf(2, 3),
                setOf(3, 4),
                setOf(4, 5),
                setOf(4, 6),
            ),
            actual = graph.findCliques().map { clique ->
                clique.map { it.value }.toSet()
            },
        )
    }
}