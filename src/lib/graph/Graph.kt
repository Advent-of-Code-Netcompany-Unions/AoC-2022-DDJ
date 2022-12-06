package lib.graph

class Graph<T>(val edges: List<Edge<T>>) {
    val nodes = (edges.map { it.start } union edges.map { it.end }).toSet()
    val connections = nodes.associateWith { node ->
        edges
            .filter { it.start == node }
            .map { it.end to it.cost }
    }

    fun shortestPathFromNodeTo(
        node: T,
        dest: T,
    ): Long {

        val unvisitedNodes = this.nodes.associateWith { Long.MAX_VALUE }.toMutableMap()
        unvisitedNodes[node] = 0
        val paths = this.nodes.associateWith { "" }.toMutableMap()

        var currentNode: T? = node
        while (currentNode != null) {
            if (currentNode == dest) {
                //println("Path: ${paths[currentNode]}" + "- ($currentNode)")
                return unvisitedNodes[currentNode]!!
            }

            this.connections[currentNode]!!
                .filter { unvisitedNodes.containsKey(it.first) }
                .forEach {
                    val newDist = unvisitedNodes[currentNode]!! + it.second
                    if( newDist < unvisitedNodes.getOrDefault(it.first, Long.MAX_VALUE))
                    {
                        unvisitedNodes[it.first] = newDist
                        paths[it.first] = paths.getOrDefault(currentNode, "") + "- ($currentNode)"
                    }
                }
            unvisitedNodes.remove(currentNode)
            currentNode = unvisitedNodes.minByOrNull { it.value }?.key
        }

        return Long.MAX_VALUE
    }
}

class Edge<T>(val start: T, val end: T, val cost: Int = 1)

fun <T> List<Edge<T>>.buildGraph(): Graph<T> {
    return Graph(this)
}

