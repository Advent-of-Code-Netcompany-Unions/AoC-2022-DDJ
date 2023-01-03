package aoc2022.day24

import aoc2021.day21.upsert
import lib.*
import lib.graph.Graph

suspend fun main() {
    challenge<Array<Array<String>>> {

        day(24)
        year(2022)

        input("example.txt")
        input("input.txt")

        parser {
            it.readLines()

                //Prune borders
                .drop(1)
                .dropLast(1)
                .map { it.substring(1, it.length - 1) }

                .get2DArray()
                .map { it.map { it.toString() }.map { if (it == ".") "" else it }.toTypedArray() }.toTypedArray()
        }

        solver {
            println("Solving challenge 1...")

            val mapsByTime = getMapsByTime(it)
            val graph = buildGraph(mapsByTime)
            val dest = it.size - 1 to it.first().size

            val res =
                graph.shortestDistanceFromTo(Triple(0, -1, 0)) { it.first == dest.x() && it.second == dest.y() }
            res.toString()
        }

        solver {
            println("Solving challenge 2...")
            val mapsByTime = getMapsByTime(it)
            val graph = buildGraph(mapsByTime)
            val dest = it.size - 1 to it.first().size
            val iterations = mapsByTime.size

            val res1 = graph.shortestDistanceFromTo(
                Triple(
                    0,
                    -1,
                    0
                )
            ) { it.first == dest.x() && it.second == dest.y() }!!
            println("Part 1: $res1")

            val res2 = graph.shortestDistanceFromTo(
                Triple(
                    dest.x(),
                    dest.y(),
                    res1.toInt() % iterations
                )
            ) { it.first == 0 && it.second == -1 }
            println("Part 2: $res2")

            val res3 = graph.shortestDistanceFromTo(
                Triple(
                    0,
                    -1,
                    (res1 + res2!!).toInt() % iterations
                )
            ) { it.first == dest.x() && it.second == dest.y() }!!
            println("Part 3: $res3")

            (res1 + res2 + res3).toString()
        }
    }
}

fun Array<Array<String>>.moveBlizzards(): Array<Array<String>> {
    val xMax = this.size
    val yMax = this.first().size
    val next = Array(xMax) { Array(yMax) { "" } }
    this.forEachIndexed { x, cols ->
        cols.forEachIndexed { y, cell ->
            cell.forEach {
                val dest = when (it) {
                    '^' -> x to (y - 1).wrapAt(yMax)
                    'v' -> x to (y + 1).wrapAt(yMax)
                    '<' -> (x - 1).wrapAt(xMax) to y
                    '>' -> (x + 1).wrapAt(xMax) to y
                    else -> throw Exception("$it is not a blizzard")
                }
                next[dest.x()][dest.y()] = next[dest.x()][dest.y()] + it
            }
        }
    }
    return next
}

fun getMapsByTime(startingMap: Array<Array<String>>): Map<Int, Array<Array<String>>> {
    val mapsByFingerprint = mutableMapOf(startingMap.fingerprint() to (startingMap to 0))
    var done = false
    var map = startingMap
    var i = 0
    while (!done) {
        map = map.moveBlizzards()
        i++
        val fingerprint = map.fingerprint()
        if (mapsByFingerprint.containsKey(fingerprint)) {
            done = true
        } else {
            mapsByFingerprint[fingerprint] = map to i
        }
    }
    println("Computed all maps with i = $i")

    return mapsByFingerprint.map { it.value.second to it.value.first }.toMap()
}

fun buildGraph(mapsByTime: Map<Int, Array<Array<String>>>): Graph<Triple<Int, Int, Int>> {
    val iterations = mapsByTime.size
    val edges = mutableMapOf<Triple<Int, Int, Int>, List<Pair<Triple<Int, Int, Int>, Long>>>()
    (1 .. iterations).forEach { time ->
        val currentMap = mapsByTime[time % iterations]!!

        //Add all neighbours without blizzards next turn
        currentMap.forEachIndexed { x, col ->
            col.forEachIndexed { y, _ ->
                val thisNode = Triple(x, y, (time - 1))
                currentMap.getNeighbours(x, y)
                    .filter { it.second.isEmpty() }
                    .map { it.first }
                    .forEach {
                        edges.upsert(
                            thisNode,
                            listOf(Triple(it.first, it.second, time % iterations) to 1L)
                        ) { p, n -> p + n }
                    }

                //Add self (waiting) if no blizzard is arriving next turn
                if (currentMap[x][y].isEmpty())
                    edges.upsert(thisNode, listOf(Triple(x, y, time % iterations) to 1L)) { p, n -> p + n }

            }
        }

        //Add waiting at start node
        val start = Triple(0, -1, time - 1)
        edges.upsert(start, listOf(Triple(0, -1, time % iterations) to 1L)) { p, n -> p + n }

        //Add waiting at destination node
        val end = Triple(currentMap.size - 1, currentMap.first().size, time - 1)
        edges.upsert(
            end,
            listOf(Triple(currentMap.size - 1, currentMap.first().size, time % iterations) to 1L)
        ) { p, n -> p + n }

        //Add possibilities to go from start and dest into the valley
        if (currentMap[0][0].isEmpty())
            edges.upsert(start, listOf(Triple(0, 0, time % iterations) to 1L)) { p, n -> p + n }
        if (currentMap[currentMap.size - 1][currentMap.first().size - 1].isEmpty())
            edges.upsert(
                end,
                listOf(Triple(currentMap.size - 1, currentMap.first().size - 1, time % iterations) to 1L)
            ) { p, n -> p + n }

        //Add connections to start and dest nodes
        edges.upsert(
            Triple(currentMap.size - 1, currentMap.first().size - 1, time - 1),
            listOf(Triple(currentMap.size - 1, currentMap.first().size, time % iterations) to 1L)
        ) { p, n -> p + n }
        edges.upsert(Triple(0, 0, time - 1), listOf(Triple(0, -1, time % iterations) to 1L)) { p, n -> p + n }
    }

    println("Computed ${edges.values.sumOf { it.count() }} edges")

    val g = Graph(edges)

    println("Built graph with ${g.nodes.size} nodes")

    return g
}

fun Array<Array<String>>.fingerprint(): String {
    return this.joinToString { it.joinToString { it } }
}