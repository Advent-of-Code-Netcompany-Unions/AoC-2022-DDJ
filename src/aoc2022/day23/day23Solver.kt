package aoc2022.day23

import aoc2022.day15.manhattanDistance
import lib.*

suspend fun main() {
    challenge<Pair<List<Elf>, Array<Array<Char>>>> {

        day(23)
        year(2022)

        input("example.txt")
        input("input.txt")

        parser {
            val map = it.readLines()
                .get2DArray()
            val elves = mutableListOf<Elf>()
            map.forEachIndexed { i, chars ->
                chars.forEachIndexed { j, c ->
                    if (c == '#') {
                        elves.add(Elf(i to j, null))
                    }
                }
            }
            elves to map
        }

        solver {
            println("Solving challenge 1...")
            var (elves, map) = it

            (0..9).forEach {
                elves.forEach { it.makePlan(map) }

                elves.filter { it.plan != null }
                    .forEach { if (elves.count { other -> other.plan == it.plan } < 2) it.pos = it.plan!! }

                //Normalize positions
                val xOffset = elves.minOf { it.pos.x() } * -1
                val yOffset = elves.minOf { it.pos.y() } * -1

                elves.forEach { it.pos = it.pos.x() + xOffset to it.pos.y() + yOffset }

                map = Array(elves.maxOf { it.pos.x() } + 1) { Array(elves.maxOf { it.pos.y() } + 1) { '.' } }
                elves.forEach { map[it.pos.x()][it.pos.y()] = '#' }
                elves.forEach { it.plan = null }
            }
            map.sumOf { it.count { it == '.' } }.toString()
        }

        solver {
            println("Solving challenge 2...")
            var (elves, map) = it

            var i = 0L
            while(true) {
                i++
                elves.forEach { it.makePlan(map) }
                if (elves.none { it.plan != null })
                    break

                elves.filter { it.plan != null }
                    .forEach { if (elves.count { other -> other.plan == it.plan } < 2) it.pos = it.plan!! }

                //Normalize positions
                val xOffset = elves.minOf { it.pos.x() } * -1
                val yOffset = elves.minOf { it.pos.y() } * -1

                elves.forEach { it.pos = it.pos.x() + xOffset to it.pos.y() + yOffset }

                map = Array(elves.maxOf { it.pos.x() } + 1) { Array(elves.maxOf { it.pos.y() } + 1) { '.' } }
                elves.forEach { map[it.pos.x()][it.pos.y()] = '#' }
                elves.forEach { it.plan = null }
            }
            i.toString()
        }
    }
}

class Elf(var pos: Pair<Int, Int>, var plan: Pair<Int, Int>?) {

    val directions =
        listOf(CardinalDirection.North, CardinalDirection.South, CardinalDirection.West, CardinalDirection.East)
    var preferredDirection = 0

    fun makePlan(map: Array<Array<Char>>) {
        val neighbours = getNeighbourCoords(pos.x(), pos.y(), true)
        val neighbouringElves = neighbours.filter { map.isValidCoord(it.x(), it.y()) && map[it.x()][it.y()] == '#' }
        if (neighbouringElves.any()) {
            val priorities = directions.subList(preferredDirection, 4) + directions.subList(0, preferredDirection)
            val options = priorities.map { pos.adjacent(it) }
            plan = options.firstOrNull { neighbouringElves.none { elf -> manhattanDistance(elf, it) < 2 } }
        }
        preferredDirection = (preferredDirection + 1).wrapAt(4)
    }
}