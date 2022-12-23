package lib.graph

import java.util.OptionalLong

class Graph<T>(val edges: List<Edge<T>>) {
    val nodes = (edges.map { it.start } union edges.map { it.end }).toSet()
    val connections = nodes.associateWith { node ->
        edges
            .filter { it.start == node }
            .map { it.end to it.cost }
    }

    val distances = nodes.map { n -> n to nodes.map { it to shortestDistanceFromTo(n, it) } }

    fun shortestDistanceFromToLookup(node: T, dest: T): Long? {
        return distances.first { it.first == node }.second.first { it.first == dest }.second
    }

    fun shortestDistanceFromTo(node: T, dest: T): Long? {
        return shortestDistanceFromTo(node) { it == dest }
    }

    fun shortestDistanceFromTo(
        node: T,
        predicate: (T) -> Boolean,
    ): Long? {

        val unvisitedNodes = this.nodes.associateWith<T, Long?> { null }.toMutableMap()
        unvisitedNodes[node] = 0
        val paths = this.nodes.associateWith { "" }.toMutableMap()

        var currentNode: T? = node
        while (currentNode != null) {
            if (predicate(currentNode)) {
                //println("Path: ${paths[currentNode]}" + "- ($currentNode)")
                return unvisitedNodes[currentNode]
            }

            this.connections[currentNode].orEmpty()
                .filter { unvisitedNodes.containsKey(it.first) }
                .forEach {
                    val oldDist = unvisitedNodes[it.first]
                    val newDist = unvisitedNodes[currentNode]!! + it.second

                    if (oldDist == null || newDist < oldDist) {
                        unvisitedNodes[it.first] = newDist
                        paths[it.first] = paths.getOrDefault(currentNode, "") + "- ($currentNode)"
                    }
                }

            unvisitedNodes.remove(currentNode)
            currentNode = unvisitedNodes.filter { it.value != null }.minByOrNull { it.value!! }?.key
        }

        return null
    }
}

class Edge<T>(val start: T, val end: T, val cost: Int = 1)

fun <T> List<Edge<T>>.buildGraph(): Graph<T> {
    return Graph(this)
}

