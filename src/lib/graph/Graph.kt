package lib.graph

class Graph<T>(val connections: Map<T, List<Pair<T, Long>>>) {
    val nodes = connections.keys

    val distances = mutableListOf<Pair<T, List<Pair<T, Long?>>>>()

    fun shortestDistanceFromToLookup(node: T, dest: T): Long? {
        if(distances.isEmpty())
            distances.addAll(nodes.map { n -> n to nodes.map { it to shortestDistanceFromTo(n, it) } })
        return distances.first { it.first == node }.second.first { it.first == dest }.second
    }

    fun shortestDistanceFromTo(node: T, dest: T): Long? {
        return shortestDistanceFromTo(node) { it == dest }
    }

    fun shortestDistanceFromTo(
        node: T,
        predicate: (T) -> Boolean,
    ): Long? {

        val visitableNodes = mutableMapOf<T, Long>()
        val unvisitedNodes = this.nodes.associateWith<T, Long?> { null }.toMutableMap()
        unvisitedNodes[node] = 0
        visitableNodes[node] = 0
        //val paths = this.nodes.associateWith { "" }.toMutableMap()

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
                        visitableNodes[it.first] = newDist
                        //paths[it.first] = paths.getOrDefault(currentNode, "") + "- ($currentNode)"
                    }
                }

            unvisitedNodes.remove(currentNode)
            visitableNodes.remove(currentNode)
            currentNode = visitableNodes.minByOrNull { it.value }?.key
        }

        return null
    }

    companion object {
        fun <T> ofEdgeList(edges: List<Edge<T>>): Graph<T> {
            val nodeSet = (edges.map { it.start } union edges.map { it.end }).toSet()
            val connectionMap = nodeSet.associateWith { node ->
                edges
                    .filter { it.start == node }
                    .map { it.end to it.cost }
            }
            return Graph(connectionMap)
        }
    }
}

class Edge<T>(val start: T, val end: T, val cost: Long = 1)

fun <T> List<Edge<T>>.buildGraph(): Graph<T> {
    return Graph.ofEdgeList(this)
}

