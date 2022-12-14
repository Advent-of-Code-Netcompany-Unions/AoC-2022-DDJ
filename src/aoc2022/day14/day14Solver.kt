package aoc2022.day14

import lib.*

suspend fun main() {
    challenge<List<List<Pair<Int, Int>>>> {

        day(14)
        year(2022)

        //input("example.txt")

        parser {
            it.readLines()
                .map { it.split(" -> ") }
                .map { it.map { it.split(",") } }
                .map { it.map { it[0].toInt() to it[1].toInt() } }
        }

        solver {
            println("Solving challenge 1...")
            val (xMax, yMax) = getMaxVals(it)
            val cave = buildCave(xMax to yMax, it)

            var n = -1 //Exclude the final drop of sand which disappears into the void
            var done = false
            while (!done) {
                val pos = cave.dropSand(500 to 0)
                cave[pos.first][pos.second] = "o"
                n++
                done = pos.second == yMax
            }
            n.toString()
        }

        solver {
            println("Solving challenge 2...")
            val (x, y) = getMaxVals(it)
            val xMax = x + 500 //Assume the "sand pyramid" extends equallish in each direction from (500, 0)
            val yMax = y + 2
            val cave = buildCave(xMax to yMax, it)
            cave.draw(0 to yMax, xMax to yMax, "#")

            var n = 0
            var done = false
            while (!done) {
                val pos = cave.dropSand(500 to 0)
                cave[pos.first][pos.second] = "o"
                done = pos.first == 500 && pos.second == 0
                n++
            }
            n.toString()
        }
    }
}

fun getMaxVals(vals: List<List<Pair<Int, Int>>>): Pair<Int, Int> {
    val xMax = vals.maxOf { it.maxOf { it.first } }
    val yMax = vals.maxOf { it.maxOf { it.second } }

    return xMax to yMax
}

fun buildCave(maxVals: Pair<Int, Int>, segments: List<List<Pair<Int, Int>>>): Array<Array<String>> {
    val cave = Array(maxVals.first + 1) { Array(maxVals.second + 1) { "." } }
    segments.forEach { it.windowed(2).forEach { cave.draw(it[0], it[1], "#") } }
    return cave
}

fun Array<Array<String>>.dropSand(from: Pair<Int, Int>): Pair<Int, Int> {

    var next = tryMove(from, 0 to 1)
    if (next.first == null)
        return from

    if (next.first == ".") {
        val path = this[from.first].drop(from.second).takeWhile { it == "." }
        val (x, y) = from.first to from.second + path.size - 1
        return this.dropSand(x to y)
    }

    next = tryMove(from, -1 to 1)
    if (next.first == ".")
        return this.dropSand(next.second)

    next = tryMove(from, 1 to 1)
    if (next.first == ".")
        return this.dropSand(next.second)

    return from
}