package aoc2022.day16

import lib.*
import lib.graph.*

suspend fun main() {
    challenge<List<Pair<Pair<String, Int>, List<String>>>> {

        day(16)
        year(2022)

        //input("example.txt")

        parser {
            it.readLines()
                .map {
                    it.replace("Valve ", "")
                        .replace(" has flow rate=", ",")
                        .replace(" tunnels lead to valves ", "")
                        .replace(" tunnel leads to valve ", "")
                }
                .map { it.split(";") }
                .map { it[0].split(",") to it[1].split(", ") }
                .map { (it.first[0] to it.first[1].toInt()) to it.second }
        }

        solver {
            println("Solving challenge 1...")
            val connections = it.flatMap { p -> p.second.map { Edge(p.first.first, it) } }
            val graph = connections.buildGraph()
            val closedValves = it.map { it.first }.filter { it.second > 0 }.toMutableList()

            findOptimalPath(graph, closedValves, 0, 30, listOf("AA")).toString()
        }

        solver {
            println("Solving challenge 2...")
            val connections = it.flatMap { p -> p.second.map { Edge(p.first.first, it) } }
            val graph = connections.buildGraph()
            val closedValves = it.map { it.first }.filter { it.second > 0 }.toMutableList()

            findOptimalPaths(graph, closedValves, 0, 26L to 26L, listOf("AA") to listOf("AA")).toString()
        }
    }
}

fun findOptimalPath(
    g: Graph<String>,
    closedValves: List<Pair<String, Int>>,
    score: Long,
    time: Long,
    path: List<String>
): Long {
    if (time < 1 || closedValves.isEmpty()) {
        //println("Found path $path with score $score")
        return score
    }

    val paths = (closedValves.indices).map {
        val options = closedValves.toMutableList()
        val choice = options.removeAt(it)
        val cost = g.shortestDistanceFromToLookup(path.last(), choice.first)
        var res = score
        if (cost != null && cost <= time - 1) {
            val timeRemaining = time - (cost + 1)
            res = findOptimalPath(
                g,
                options,
                score + (timeRemaining * choice.second),
                timeRemaining,
                path.plus(choice.first)
            )
        }
        res
    }

    return paths.max()
}

fun findOptimalPaths(
    g: Graph<String>,
    closedValves: List<Pair<String, Int>>,
    score: Long,
    time: Pair<Long, Long>,
    paths: Pair<List<String>, List<String>>
): Long {
    if ((time.first < 1 && time.second < 1) || closedValves.isEmpty()) {
        //println("Found path $path with score $score")
        return score
    }

    val vals = mutableListOf(score)

    (closedValves.indices).forEach {
        if (time.first > 1) {
            val options = closedValves.toMutableList()
            val distanceBound = options.minBy { g.shortestDistanceFromToLookup(paths.first.last(), it.first)!! }
            val choice1 = options.removeAt(it)
            if(choice1.second >= distanceBound.second) {
                val cost1 = g.shortestDistanceFromToLookup(paths.first.last(), choice1.first)
                if (cost1 != null && cost1 <= time.first - 1) {
                    val timeRemaining = time.first - (cost1 + 1)
                    vals.add(
                        findOptimalPath(
                            g,
                            options,
                            score + (timeRemaining * choice1.second),
                            timeRemaining,
                            paths.first.plus(choice1.first)
                        )
                    )
                }
            }
        }
    }

    (closedValves.indices).forEach {
        if (time.second > 1) {
            val options = closedValves.toMutableList()
            val distanceBound = options.minBy { g.shortestDistanceFromToLookup(paths.first.last(), it.first)!! }
            val choice2 = options.removeAt(it)
            if(choice2.second >= distanceBound.second) {
                val cost2 = g.shortestDistanceFromToLookup(paths.second.last(), choice2.first)
                if (cost2 != null && cost2 <= time.second - 1) {
                    val timeRemaining = time.second - (cost2 + 1)
                    vals.add(
                        findOptimalPath(
                            g,
                            options,
                            score + (timeRemaining * choice2.second),
                            timeRemaining,
                            paths.second.plus(choice2.first)
                        )
                    )
                }
            }
        }
    }

    (closedValves.indices).forEach {
        if (time.first > 1 && time.second > 1 && closedValves.size > 1) {
            val options = closedValves.toMutableList()
            val distanceBound1 = options.minBy { g.shortestDistanceFromToLookup(paths.first.last(), it.first)!! }
            val choice1 = options.removeAt(it)

            if(choice1.second >= distanceBound1.second) {
                val cost1 = g.shortestDistanceFromToLookup(paths.first.last(), choice1.first)
                if (cost1 != null && cost1 <= time.first - 1) {
                    val time1Remaining = time.first - (cost1 + 1)

                    options.indices.forEach {
                        val options2 = options.toMutableList()
                        val distanceBound2 =
                            options.minBy { g.shortestDistanceFromToLookup(paths.first.last(), it.first)!! }
                        val choice2 = options2.removeAt(it)
                        if(choice2.second >= distanceBound2.second) {
                            val cost2 = g.shortestDistanceFromToLookup(paths.second.last(), choice2.first)
                            if (cost2 != null && cost2 <= time.second - 1) {
                                val time2Remaining = time.second - (cost2 + 1)
                                vals.add(
                                    findOptimalPaths(
                                        g,
                                        options2,
                                        score + (time1Remaining * choice1.second) + (time2Remaining * choice2.second),
                                        time1Remaining to time2Remaining,
                                        paths.first.plus(choice1.first) to paths.second.plus(choice2.first)
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    return vals.max()
}