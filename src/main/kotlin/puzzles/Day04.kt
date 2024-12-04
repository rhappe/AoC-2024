package puzzles

import api.readInput

fun main() {
    val input = readInput(day = 4)

    val partOneResult = Day04.Part01.countWords(input, "XMAS")
    println("Part 1: $partOneResult")

    val partTwoResult = Day04.Part02.countXShapes(input, "MAS")
    println("Part 2: $partTwoResult")
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

            val count = allWords.sumOf { word.toRegex().findAll(it).count() }
            val reversedCount = allWords.sumOf { word.reversed().toRegex().findAll(it).count() }
            return count + reversedCount
        }

        private fun getHorizontalWords(input: List<String>): List<String> {
            return input
        }

        private fun getVerticalWords(input: List<String>): List<String> {
            val rotated = rotate(input)
            return rotated
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

            return strings
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

    object Part02 {
        fun countXShapes(input: List<String>, word: String): Int {
            check(word.length % 2 == 1) { "Word must have an odd number of characters" }
            if (word.length > input.size || word.length > input[0].length) {
                return 0
            }

            var count = 0
            val limbLength = word.length / 2
            for (row in limbLength until input[0].length - limbLength) {
                for (col in limbLength until input.size - limbLength) {
                    val left = buildString {
                        for (index in -limbLength until 0) {
                            append(input[row + index][col + index])
                        }
                        append(input[row][col])
                        for (index in 1..limbLength) {
                            append(input[row + limbLength][col + limbLength])
                        }
                    }
                    val right = buildString {
                        for (index in -limbLength until 0) {
                            append(input[row - index][col + index])
                        }
                        append(input[row][col])
                        for (index in 1..limbLength) {
                            append(input[row - index][col + index])
                        }
                    }

                    if ((left == word || left == word.reversed()) && (right == word || right == word.reversed())) {
                        count++
                    }
                }
            }

            return count
        }
    }
}
