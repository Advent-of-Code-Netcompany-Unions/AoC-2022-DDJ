package aoc2022.day9

import lib.*
import kotlin.math.absoluteValue

suspend fun main() {
    challenge<List<Pair<String, Int>>> {

        day(9)
        year(2022)

        //input("example.txt")

        parser {
            it.readLines()
                .map { it.toPair(" ") }
                .map { it.first to it.second.toInt() }
        }

        solver {
            println("Solving challenge 1...")
            var headPos = 0 to 0
            var tailPos = 0 to 0
            val visited = mutableListOf(tailPos)
            it.forEach {
                val direction = it.first
                (1..it.second).forEach {
                    headPos = moveHead(headPos, direction)
                    tailPos = moveTail(tailPos, headPos)
                    visited.add(tailPos)
                }
            }
            val res =visited.toSet()
            res.count().toString()
        }

        solver {
            val knots = (0..9).map { 0 to 0 }.toMutableList()
            val visited = mutableListOf(0 to 0)
            it.forEach {
                val direction = it.first
                (1..it.second).forEach {
                    knots[0] = moveHead(knots[0], direction)
                    (1..9).forEach {
                        knots[it] = moveTail(knots[it], knots[it-1])
                    }
                    visited.add(knots.last())
                }
            }
            val res =visited.toSet()
            res.count().toString()
        }
    }
}

fun moveHead(pos: Pair<Int, Int>, direction: String): Pair<Int, Int> {
    return when (direction) {
        "U" -> pos.first to pos.second + 1
        "D" -> pos.first to pos.second - 1
        "L" -> pos.first - 1 to pos.second
        "R" -> pos.first + 1 to pos.second
        else -> throw Exception("Unknown instruction: $direction")
    }
}

fun moveTail(tailPos: Pair<Int, Int>, headPos: Pair<Int, Int>): Pair<Int, Int> {
    val dif = headPos.first - tailPos.first to headPos.second - tailPos.second

    val shouldMove = dif.first.absoluteValue > 1 || dif.second.absoluteValue > 1
    var newPos = tailPos
    if (shouldMove) {
        if (dif.first.absoluteValue > 0)
            newPos = newPos.first + (dif.first / dif.first.absoluteValue) to newPos.second
        if (dif.second.absoluteValue > 0)
            newPos = newPos.first to newPos.second + (dif.second / dif.second.absoluteValue)
    }
    return newPos
}