package aoc2022.day15

import lib.*
import kotlin.math.absoluteValue

suspend fun main() {
    challenge<List<Pair<Pair<Int, Int>, Pair<Int, Int>>>> {

        day(15)
        year(2022)

        //input("example.txt")

        parser {
            it.readLines()
                .map {
                    it.replace("Sensor at ", "")
                        .replace(" closest beacon is at ", "")
                        .split(":")
                }
                .map { it.map { it.split(", ") } }
                .map {
                    (it[0][0].replace("x=", "").toInt() to it[0][1].replace("y=", "")
                        .toInt()) to (it[1][0].replace("x=", "").toInt() to it[1][1].replace("y=", "").toInt())
                }
        }

        solver {
            println("Solving challenge 1...")
            val row = 2000000
            val candidates = it.map {
                it.first.first to ((it.first.second - row).absoluteValue to manhattanDistance(
                    it.first,
                    it.second
                ))
            }
                .filter { it.second.second - it.second.first > 0 }
                .map { it.first - (it.second.second - it.second.first)..it.first + (it.second.second - it.second.first) }
                .foldRight(setOf<Int>()) { a, r -> a union r }
            val beacons = it.map { it.second }.filter { it.second == row }.distinct()
            (candidates.size - beacons.size).toString()
        }

        solver {
            println("Solving challenge 2...")
            val coverage = it.map { it.first to manhattanDistance(it.first, it.second) }
            val limit = 4000000L
            val (x, y) = findUncoveredCoordinate(coverage, limit)!!

            (x * limit + y).toString()
        }
    }
}

fun manhattanDistance(from: Pair<Int, Int>, to: Pair<Int, Int>): Int {
    return (from.first - to.first).absoluteValue + (from.second - to.second).absoluteValue
}

fun findUncoveredCoordinate(coverage: List<Pair<Pair<Int, Int>, Int>>, limit: Long): Pair<Int, Int>? {
    var (x, y) = 0 to 0
    while (x < limit) {
        while (y < limit) {
            val blocker = coverage.firstOrNull { it.second >= manhattanDistance(x to y, it.first) }
            if (blocker == null) {
                return x to y
            } else {
                y = (blocker.first.second + (blocker.second - (blocker.first.first - x).absoluteValue)) + 1
            }
        }
        y = 0
        x++
    }
    return null
}