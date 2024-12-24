package puzzles.twenty24

import api.readInput

fun main() {
    val input = readInput(day = 4)

    val partOneResult = Day04.Part01.countWords(input, "SAXMAAA")
    println("Part 1: $partOneResult")

    val partTwoResult = Day04.Part02.countXShapes(input, "XSA")
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
            return rotate(input)
        }

        private fun getDiagonalWordsLeftToRight(input: List<String>): List<String> {
            val strings = buildList {
                for (row in 0..(input.lastIndex + input[0].lastIndex)) {
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
                        append(input[row][input[0].lastIndex - column])
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
                    // backward as in this string makes the "backward slash" shape of the X
                    val backward = buildString {
                        for (index in -limbLength until 0) {
                            append(input[row + index][col + index])
                        }
                        append(input[row][col])
                        for (index in 1..limbLength) {
                            append(input[row + limbLength][col + limbLength])
                        }
                    }
                    // forward as in this string makes the "forward slash" shape of the X
                    val forward = buildString {
                        for (index in -limbLength until 0) {
                            append(input[row - index][col + index])
                        }
                        append(input[row][col])
                        for (index in 1..limbLength) {
                            append(input[row - index][col + index])
                        }
                    }

                    if ((backward == word || backward == word.reversed()) && (forward == word || forward == word.reversed())) {
                        count++
                    }
                }
            }

            return count
        }
    }
}
