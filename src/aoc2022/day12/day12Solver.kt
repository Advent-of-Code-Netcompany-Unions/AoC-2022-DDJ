package aoc2022.day12

import lib.*
import lib.graph.Edge
import lib.graph.Graph

suspend fun main() {
    challenge<Array<Array<Char>>> {

        day(12)
        year(2022)

        parser {
            it.readLines()
                .get2DArray()
        }

        solver {
            println("Solving challenge 1...")

            val (graph, start, _) = buildGraph(it) {it < 2}
            graph.shortestDistanceFromTo(start) { it.third == 'E' }?.toString()
        }

        solver {
            println("Solving challenge 2...")
            val (graph, _, dest) = buildGraph(it) {it > -2}
            graph.shortestDistanceFromTo(dest) { it.third == 'a' }?.toString()
        }
    }
}

fun buildGraph(input: Array<Array<Char>>, edgeQualifier: (Int) -> Boolean)
: Triple<Graph<Triple<Int, Int, Char>>, Triple<Int, Int, Char>, Triple<Int, Int, Char>> {

    val edges = mutableListOf<Edge<Triple<Int, Int, Char>>>()
    var start: Triple<Int, Int, Char>? = null
    var dest: Triple<Int, Int, Char>? = null

    var i = 0
    var j = 0
    while (i < input.size) {
        while (j < input.first().size) {

            val char = input[i][j]
            var height = char

            if (height == 'S') {
                height = 'a'
                start = Triple(i, j, char)
            }

            if (height == 'E') {
                height = 'z'
                dest = Triple(i, j, char)
            }

            val adj = input.getNeighbours(i, j)
            adj.filter { edgeQualifier(it.second.code - height.code) }
                .forEach { edges.add(Edge(Triple(i, j, char), Triple(it.first.first, it.first.second, it.second))) }
            j++
        }
        j = 0
        i++
    }

    return Triple(Graph(edges), start!!, dest!!)
}