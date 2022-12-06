package aoc2022.day4

import lib.*

suspend fun main() {
    challenge<List<Pair<IntProgression, IntProgression>>> {

        day(4)
        year(2022)

        parser {
            it.readLines()
                .map { it.toPair(",") }
                .map { it.first.toIntProgression("-") to it.second.toIntProgression("-") }
        }

        solver {
            println("Solving challenge 1...")
            it.count { it.first.minus(it.second).isEmpty() || it.second.minus(it.first).isEmpty() }
                .toString()
        }

        solver {
            println("Solving challenge 2...")
            it.count { it.first intersects it.second }
                .toString()
        }
    }
}