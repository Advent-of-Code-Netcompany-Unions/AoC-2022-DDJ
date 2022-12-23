package aoc2022.day17

import lib.*
import java.math.BigInteger
import kotlin.math.max
import kotlin.math.min

suspend fun main() {
    challenge<Winds> {

        day(17)
        year(2022)

        input("example.txt")
        input("input.txt")

        parser {
            Winds(it.readText().trim())
        }

        solver {
            println("Solving challenge 1...")
            val (res, offset) = run(2022, it)
            (res.maxOf { it.key } + offset).toString()
        }

        solver {
            println("Solving challenge 2...")
            val (res, offset) = run(1000000000000, it)
            (res.maxOf { it.key } + offset).toString()
        }
    }
}

class Winds(data: String) {
    val winds = data.mapIndexed { i, c -> Wind(if (c == '<') -1 else 1, i) }
    var current = winds[0]

    init {
        winds.forEachIndexed { index, wind -> wind.next = winds[(index + 1) % winds.size] }
    }

    fun moveShape(shape: Shape, occupiedSpaces: Map<Int, List<Int>>) {
        shape.tryMove(current.mover, occupiedSpaces)
        current = current.next
    }
}

class Wind(val mover: Int, val index: Int) {
    var next = this
}

fun run(numIterations: Long, winds: Winds): Pair<Map<Int, List<Int>>, Long> {
    var height = 0
    var heightOffset = 0L
    var occupiedSpaces = mutableMapOf<Int, List<Int>>()
    var shape: Shape = DashBlock(2, 4)

    val fingerprints = mutableMapOf<String, Pair<Long, Long>>()
    var i = 0L
    while (i < numIterations) {

        val fingerprint =
            "${winds.current.index}-${shape.index}-${occupiedSpaces.entries.joinToString { "${it.key}:" + it.value.joinToString { "$it" } }}"
        if (fingerprints.containsKey(fingerprint)) {
            val growth = (height + heightOffset) - fingerprints[fingerprint]!!.second
            val time = i - fingerprints[fingerprint]!!.first
            var count = 0L
            while (i < (numIterations - time)) {
                i += time
                heightOffset += growth
                count++
            }
            if (count > 0L)
                println("Fast-forwarded $count repetitions with growth-factor $growth and duration $time for a total increase of ${growth * count}. Iteration count is now $i")
        } else {
            fingerprints[fingerprint] = i to (height + heightOffset)
        }

        var stopped = false
        while (!stopped) {
            winds.moveShape(shape, occupiedSpaces)
            stopped = shape.tryMoveDown(occupiedSpaces)
        }

        shape.getPoints()
            .forEach { occupiedSpaces[it.second] = occupiedSpaces.getOrDefault(it.second, listOf()) + it.first }

        val removed = occupiedSpaces.prune(shape)
        heightOffset += removed
        occupiedSpaces = occupiedSpaces.filterKeys { it > removed }.mapKeys { it.key - removed }.toMutableMap()
        height = occupiedSpaces.keys.maxOrNull() ?: 0
        shape = shape.getNext(height)

        i++
    }
    return occupiedSpaces to heightOffset
}

fun printState(occupiedSpaces: Map<Int, List<Int>>, shape: Shape) {
    val canvas = Array(7) { Array(occupiedSpaces.keys.max() + 3) { "." } }
            shape.getPoints().forEach { canvas[it.first][it.second] = "#" }
            occupiedSpaces.forEach { (k, v) -> v.forEach { canvas[it][k] = "@" } }
            canvas.print()
            println()
}

fun MutableMap<Int, List<Int>>.prune(shape: Shape): Int {
    var nextRow = shape.top()
    while (nextRow > 0) {
        if(this.getOrDefault(nextRow, listOf()).contains(0)) {
            var col = 0
            val candidates = mutableListOf(nextRow to col)
            var lowpoint = nextRow
            while (candidates.any()) {

                val cand = candidates.maxBy { it.second }
                candidates.remove(cand)
                if (cand.first < lowpoint)
                    lowpoint = cand.first
                col = cand.second

                if (col == 6) {
                   // printState(this, shape)
                    return lowpoint - 1
                }

                if (this.getOrDefault(cand.first + 1, listOf()).contains(col + 1))
                    candidates.add(cand.first + 1 to col + 1)

                if (this.getOrDefault(cand.first, listOf()).contains(col + 1))
                    candidates.add(cand.first to col + 1)

                if (this.getOrDefault(cand.first - 1, listOf()).contains(col + 1))
                    candidates.add(cand.first - 1 to col + 1)
            }
        }
        nextRow--
    }
    return 0
}