package puzzles

import kotlin.test.Test
import puzzles.Day22.Secret
import kotlin.test.assertEquals


class Day22Test {
    @Test
    fun `mix number into secret`() {
        val secret = Secret(42)
        val newSecret = secret.mix(15)
        assertEquals(
            expected = Secret(37),
            actual = newSecret,
        )
    }

    @Test
    fun `prune a secret number`() {
        val secret = Secret(value = 100000000)
        val newSecret = secret.prune()
        assertEquals(
            expected = Secret(16113920),
            actual = newSecret,
        )
    }

    @Test
    fun `Next secret number`() {
        val initialSecret = Secret(value = 123)
        val secrets = buildList<Secret> {
            repeat(10) {
                if (isEmpty()) {
                    add(initialSecret.next())
                } else {
                    add(last().next())
                }
            }
        }

        assertEquals(
            expected = listOf(
                15887950L,
                16495136L,
                527345L,
                704524L,
                1553684L,
                12683156L,
                11100544L,
                12249484L,
                7753432L,
                5908254L,
            ),
            actual = secrets.map { it.value },
        )
    }

    @Test
    fun `200th secret with start value of 1`() {
        val secret = Secret(1)
        assertEquals(
            expected = Secret(8685429),
            actual = secret.next(2000),
        )
    }

    @Test
    fun `200th secret with start value of 10`() {
        val secret = Secret(10)
        assertEquals(
            expected = Secret(4700978),
            actual = secret.next(2000),
        )
    }

    @Test
    fun `200th secret with start value of 100`() {
        val secret = Secret(100)
        assertEquals(
            expected = Secret(15273692),
            actual = secret.next(2000),
        )
    }

    @Test
    fun `200th secret with start value of 2024`() {
        val secret = Secret(2024)
        assertEquals(
            expected = Secret(8667524),
            actual = secret.next(2000),
        )
    }

    @Test
    fun `Test part 1 with fake data`() {
        val day22 = Day22(
            input = listOf(
                "1",
                "10",
                "100",
                "2024",
            ),
        )

        assertEquals(
            expected = 37327623,
            actual = day22.part1.solve(),
        )
    }

    @Test
    fun `Secret value real price`() {
        val initialSecret = Secret(value = 123)
        val secrets = buildList<Secret> {
            add(initialSecret)
            repeat(9) {
                if (isEmpty()) {
                    add(initialSecret.next())
                } else {
                    add(last().next())
                }
            }
        }

        assertEquals(
            expected = listOf(3, 0, 6, 5, 4, 4, 6, 4, 4, 2),
            actual = secrets.map { it.realPrice }
        )
    }

    @Test
    fun `Test part 2 with fake data`() {
        val day22 = Day22(
            input = listOf(
                "1",
                "2",
                "3",
                "2024",
            ),
        )

        assertEquals(
            expected = 23,
            actual = day22.part2.solve(),
        )
    }
}
