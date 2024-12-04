package puzzles

import api.readInput

fun main() {
    val input = readInput(day = 4)

    val partOneResult = Day04.Part01.countWords(input, "XMAS")
    println("Part 1: $partOneResult")
}

private object Day04 {
    object Part01 {
        fun countWords(input: List<String>, word: String): Int {
            val allWords = buildList {
                addAll(getHorizontalWords(input))
                addAll(getVerticalWords(input))
                addAll(getDiagonalWordsLeftToRight(input))
                addAll(getDiagonalWordsRightToLeft(input))
            }
            return allWords.sumOf { word.toRegex().findAll(it).count() }
        }
    }

    private fun getHorizontalWords(input: List<String>): List<String> {
        return input + input.map { it.reversed() }
    }

    private fun getVerticalWords(input: List<String>): List<String> {
        val rotated = rotate(input)
        return rotated + rotated.map { it.reversed() }
    }

    private fun getDiagonalWordsLeftToRight(input: List<String>): List<String> {
        val strings = buildList {
            for (row in 0..(input.size + input[0].length - 2)) {
                val word = buildString {
                    for (column in 0..row) {
                        if (row - column in input.indices && column in input[row - column].indices) {
                            append(input[row - column][column])
                        }
                    }
                }
                add(word)
            }
        }

        return strings + strings.map { it.reversed() }
    }

    private fun getDiagonalWordsRightToLeft(input: List<String>): List<String> {
        val rotated = rotate(input)
        return getDiagonalWordsLeftToRight(rotated)
    }

    private fun rotate(input: List<String>) = buildList {
        for (column in input[0].indices) {
            val string = buildString {
                for (row in input.indices) {
                    append(input[row][input[0].length - column - 1])
                }
            }
            add(string)
        }
    }
}
