package aoc.twenty24

import aoc.api.readInput

fun main() {
    val input = readInput(day = 5)
    val rulesInput = input.subList(0, input.indexOf(""))
    val updatesInput = input.subList(input.indexOf("") + 1, input.size)

    val partOneAnswer = Day05.Part01.getCorrectPageUpdatesMiddleSum(rulesInput, updatesInput)
    println("Part 1: $partOneAnswer")

    val partTwoAnswer = Day05.Part02.getCorrectedIncorrectPageUpdatesMiddleSum(rulesInput, updatesInput)
    println("Part 2: $partTwoAnswer")
}

private object Day05 {
    object Part01 {
        fun getCorrectPageUpdatesMiddleSum(rulesInput: List<String>, updatesInput: List<String>): Int {
            val rules = parseRulesInput(rulesInput)
            val updates = parseUpdates(updatesInput)
            val correctUpdates = updates.filter { update -> isUpdateCorrect(rules, update) }
            return correctUpdates.sumOf { it.orderedPages[it.orderedPages.size / 2] }
        }
    }

    object Part02 {
        fun getCorrectedIncorrectPageUpdatesMiddleSum(rulesInput: List<String>, updatesInput: List<String>): Int {
            val rules = parseRulesInput(rulesInput)
            val updates = parseUpdates(updatesInput)
            val incorrectUpdates = updates.filter { update -> !isUpdateCorrect(rules, update) }
            val corrected = incorrectUpdates.map { update -> getCorrectedPages(rules, update.orderedPages) }
            return corrected.sumOf { it[it.size / 2] }
        }
    }

    private fun getCorrectedPages(rules: List<PageUpdateRule>, pages: List<Int>): List<Int> {
        if (pages.isEmpty()) return emptyList()
        val currentPage = pages.first()
        val remainingPages = pages.subList(1, pages.size)

        return if (isPageCorrect(rules, currentPage, remainingPages)) {
            // if the current page is in the correct position, add it to the front of the list
            // and then continue correcting the rest of the pages.
            listOf(currentPage) + getCorrectedPages(rules, remainingPages)
        } else {
            // if the current page is in the incorrect position, just push it to the end of the list.
            getCorrectedPages(rules, remainingPages + listOf(currentPage))
        }
    }

    private fun isUpdateCorrect(rules: List<PageUpdateRule>, update: Update): Boolean {
        update.orderedPages.forEachIndexed { index, currentPage ->
            val remainingPages = update.orderedPages.subList(index + 1, update.orderedPages.size)
            if (!isPageCorrect(rules, currentPage, remainingPages)) {
                return false
            }
        }
        return true
    }

    private fun isPageCorrect(rules: List<PageUpdateRule>, currentPage: Int, remainingPages: List<Int>): Boolean {
        val matchingRules = rules.filter { it.first == currentPage && it.second in remainingPages }
        return matchingRules.size == remainingPages.size
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
