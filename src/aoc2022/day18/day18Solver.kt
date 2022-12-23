package aoc2022.day18

import lib.*

suspend fun main() {
    challenge<List<Triple<Int, Int, Int>>> {

        day(18)
        year(2022)

        //input("example.txt")

        parser {
            it.readLines()
                .map { it.split(",") }
                .map { it.map { it.toInt() } }
                .map { Triple(it[0], it[1], it[2]) }
        }

        solver {
            println("Solving challenge 1...")
            it.sumOf { coord -> 6 - it.intersect(coord.getNeighbours()).size }
                .toString()
        }

        solver {
            println("Solving challenge 2...")
            it.sumOf { coord -> coord.getExteriorSides(it.toSet()) }
                .toString()
        }
    }
}

fun Triple<Int, Int, Int>.getExteriorSides(points: Set<Triple<Int, Int, Int>>): Int {
    val xMin = points.minOf { it.first }
    val xMax = points.maxOf { it.first }
    val yMin = points.minOf { it.second }
    val yMax = points.maxOf { it.second }
    val zMin = points.minOf { it.third }
    val zMax = points.maxOf { it.third }

    val neighbours = this.getNeighbours()
    val coveredSides = neighbours.intersect(points)
    val openSides = neighbours.minus(points)

    var interiorSides = 0
    openSides.forEach {
        val visited = mutableSetOf(it)
        val candidates = it.getNeighbours().minus(points).toMutableList()

        while (candidates.isNotEmpty()) {
            val current = candidates.removeLast()
            if(current.first < xMin || current.first > xMax || current.second < yMin || current.second > yMax || current.third < zMin || current.third > zMax) {
                break
            }
            visited.add(current)
            candidates.addAll(current.getNeighbours().minus(points).minus(visited))
        }

        if(candidates.isEmpty()) {
            interiorSides++
        }
    }

    return 6 - coveredSides.size - interiorSides
}