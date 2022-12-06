package aoc2022.day6

import lib.*

suspend fun main() {
    challenge<Sequence<Char>> {

        day(6)
        year(2022)

        parser {
            it.readText().asSequence()
        }

        solver {
            println("Solving challenge 1...")
            it.findSubstringWithoutRepeatingChars(4).toString()
        }

        solver {
            println("Solving challenge 2...")
            it.findSubstringWithoutRepeatingChars(14).toString()
        }
    }
}

fun Sequence<Char>.findSubstringWithoutRepeatingChars(n: Int): Int {
    return this
        .windowed(n)
        .mapIndexed { i, window -> window.all { c -> window.count { it == c } == 1 } to i + n }
        .first { it.first }
        .second
}