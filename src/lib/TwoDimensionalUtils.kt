package lib

fun getNeighbourCoords(i: Int, j: Int, diagonals: Boolean = false): List<Pair<Int, Int>> {
    var neighbours = listOf(i to j + 1, i to j - 1, i + 1 to j, i - 1 to j)
    if (diagonals) {
        neighbours = neighbours.plus(listOf(i + 1 to j + 1, i + 1 to j - 1, i - 1 to j + 1, i - 1 to j - 1))
    }
    return neighbours
}

fun <T> Array<Array<T>>.getNeighbours(i: Int, j: Int, diagonals: Boolean = false): List<Pair<Pair<Int, Int>, T>> {
    val neighbours = getNeighbourCoords(i, j, diagonals)
    return neighbours.filter { this.isValidCoord(it.first, it.second) }.map { it to this[it.first][it.second] }
}

fun <T> Array<Array<T>>.isValidCoord(i: Int, j: Int): Boolean {
    return i > -1 && j > -1 && i < this.size && j < this.first().size
}

fun <T> Array<Array<T>>.print() {
    for (i in this.first().indices) {
        for (j in this.indices) {
            print(this[j][i])
        }
        println()
    }
}

fun <T> List<List<T>>.getNeighbours(i: Int, j: Int): List<Pair<Pair<Int, Int>, T>> {
    val neighbours =
        listOf(i to j + 1, i to j - 1, i + 1 to j, i - 1 to j).filter { this.isValidCoord(it.first, it.second) }
    return neighbours.map { it to this[it.first][it.second] }
}

fun <T> Collection<Collection<T>>.isValidCoord(i: Int, j: Int): Boolean {
    return i > -1 && j > -1 && i < this.size && j < this.first().size
}

infix fun Pair<IntProgression, IntProgression>.intersect(other: Pair<IntProgression, IntProgression>): Pair<IntProgression, IntProgression> {
    return (this.first intersect other.first) to (this.second intersect other.second)
}

operator fun Pair<IntProgression, IntProgression>.minus(other: Pair<IntProgression, IntProgression>): List<Pair<IntProgression, IntProgression>> {
    val xs = this.first - other.first
    val ys = this.second - other.second
    val safeAreas = xs.flatMap { x -> ys.map { x to it } }
    //val emptyArea = 0..-1 to 0..-1

    val mappedYs = ys.map { this.first to it }//.map { safeAreas.fold(it) { acc, a -> (acc - a).firstOrNull() ?: emptyArea } }
    val mappedXs = xs.map { it to this.second }//.map { safeAreas.fold(it) { acc, a -> (acc - a).firstOrNull() ?: emptyArea } }
        return safeAreas
            .plus(mappedXs)
            .plus(mappedYs)
            .filter { !it.first.isEmpty() && !it.second.isEmpty() }
}