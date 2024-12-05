package puzzles

import api.readInput

fun main() {
    val input = readInput(day = 5)
    val rulesInput = input.subList(0, input.indexOf(""))
    val updatesInput = input.subList(input.indexOf("") + 1, input.size)

    val partOneAnswer = Day05.Part01.getCorrectPageUpdatesMiddleSum(
        rulesInput = rulesInput,
        updatesInput = updatesInput,
    )
    println("Part 1: $partOneAnswer")
}

private object Day05 {
    object Part01 {
        fun getCorrectPageUpdatesMiddleSum(
            rulesInput: List<String>,
            updatesInput: List<String>,
        ): Int {
            val rules = parseRulesInput(rulesInput)
            val updates = parseUpdates(updatesInput)

            val correctUpdates = updates.filter { update ->
                isUpdateCorrect(
                    rules = rules,
                    update = update,
                )
            }
            return correctUpdates.sumOf { it.orderedPages[it.orderedPages.size / 2] }
        }

        private fun isUpdateCorrect(rules: List<PageUpdateRule>, update: Update): Boolean {
            update.orderedPages.forEachIndexed { index, currentPage ->
                val remainingPages = update.orderedPages.subList(index + 1, update.orderedPages.size)
                val matchingRules = rules.filter { it.first == currentPage && it.second in remainingPages }
                if (matchingRules.size != remainingPages.size) {
                    return false
                }
            }
            return true
        }


    }

    private fun parseRulesInput(rulesInput: List<String>): List<PageUpdateRule> = rulesInput.map { line ->
        val parts = line.split("|")
        PageUpdateRule(
            first = parts[0].toInt(),
            second = parts[1].toInt(),
        )
    }

    private data class PageUpdateRule(
        val first: Int,
        val second: Int,
    )

    private fun parseUpdates(updates: List<String>): List<Update> = updates.map { line ->
        Update(orderedPages = line.split(",").map { it.toInt() })
    }

    private data class Update(
        val orderedPages: List<Int>,
    )
}