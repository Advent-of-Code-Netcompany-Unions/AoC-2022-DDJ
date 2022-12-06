package aoc2022.day2

import lib.*
import kotlin.math.abs

suspend fun main() {
    challenge<List<Pair<String, String>>> {

        day(2)
        year(2022)

        parser {
            it.readLines()
                .map { it.toPair(" ") }
        }

        val moves = mapOf("A" to 1, "B" to 2, "C" to 3, "X" to 1, "Y" to 2, "Z" to 3)

        solver {
            println("Solving challenge 1...")
            it.sumOf { play(moves[it.first]!!, moves[it.second]!!) }
                .toString()
        }

        solver {
            println("Solving challenge 2...")
            it.sumOf { getMove(moves[it.first]!!, it.second) }
                .toString()
        }
    }
}

fun play(m1: Int, m2: Int): Int {
    if(abs(m1 - m2) < 2) {
        return (m2 - m1) * 3 + m2 + 3
    }
    return (m2 % 3) * 6 + m2
}

fun getMove(opp: Int, res: String): Int {
    return when(res) {
        "X" -> if (opp == 1) 3 else opp - 1
        "Y" -> opp + 3
        "Z" -> (opp % 3) + 1 + 6
        else -> throw IllegalArgumentException(res)
    }
}