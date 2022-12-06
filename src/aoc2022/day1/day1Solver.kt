package aoc2022.day1

import lib.*

suspend fun main() {
    challenge<List<Int>> {

        day(1)
        year(2022)

        parser {
            it.getStringsGroupedByEmptyLine()
                .map { it.sumOf { it.toInt() } }
        }

        solver {
            println("Solving challenge 1...")
            it.max()
                .toString()
        }

        solver {
            println("Solving challenge 2...")
            it.sortedDescending()
                .take(3)
                .sum()
                .toString()
        }
    }
}