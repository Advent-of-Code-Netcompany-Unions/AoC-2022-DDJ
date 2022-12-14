package lib

fun getNeighbourCoords(i: Int, j: Int, diagonals: Boolean = false): List<Pair<Int, Int>> {
    var neighbours = listOf(i to j + 1, i to j - 1, i + 1 to j, i - 1 to j)
    if (diagonals) {
        neighbours = neighbours.plus(listOf(i + 1 to j + 1, i + 1 to j - 1, i - 1 to j + 1, i - 1 to j - 1))
    }
    return neighbours
}

fun Pair<Int, Int>.getNeighbours(): List<Pair<Int, Int>> {
    return getNeighbourCoords(this.first, this.second)
}

fun <T> Array<Array<T>>.getNeighbours(i: Int, j: Int, diagonals: Boolean = false): List<Pair<Pair<Int, Int>, T>> {
    val neighbours = getNeighbourCoords(i, j, diagonals)
    return neighbours.filter { this.isValidCoord(it.first, it.second) }.map { it to this[it.first][it.second] }
}

fun <T> Array<Array<T>>.isValidCoord(i: Int, j: Int): Boolean {
    return i > -1 && j > -1 && i < this.size && j < this.first().size
}

fun <T> Array<Array<T>>.tryMove(from: Pair<Int, Int>, direction: Pair<Int, Int>): Pair<T?, Pair<Int, Int>> {
    val (x, y) = from.first + direction.first to from.second + direction.second
    return if(this.isValidCoord(x, y)) {
        this[x][y] to (x to y)
    } else {
        null to (x to y)
    }
}

fun <T> Array<Array<T>>.draw(from: Pair<Int, Int>, to: Pair<Int, Int>, value: T) {
    if (from.first == to.first) {
        val coords = listOf(from.second, to.second).sorted()
        (coords[0]..coords[1]).forEach {
            this[from.first][it] = value
        }
    } else if (from.second == to.second) {
        val coords = listOf(from.first, to.first).sorted()
        (coords[0]..coords[1]).forEach {
            this[it][from.second] = value
        }
    } else {
        throw Exception("Coordinates do not match: $from, $to")
    }
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

fun Pair<Int, Int>.adjacent(direction: CardinalDirection): Pair<Int, Int> {
    return when (direction) {
        CardinalDirection.East -> x() + 1 to y()
        CardinalDirection.South -> x() to y() + 1
        CardinalDirection.West -> x() - 1 to y()
        CardinalDirection.North -> x() to y() - 1
    }
}

enum class CardinalDirection(val value: Int) {
    East(0),
    South(1),
    West(2),
    North(3);

    fun turn(change: RelativeDirection): CardinalDirection {
        return when (change) {
            RelativeDirection.Left -> CardinalDirection.values().first { it.value == (this.value - 1).wrapAt(4) }
            RelativeDirection.Right -> CardinalDirection.values().first { it.value == (this.value + 1) % 4 }
        }
    }
}

enum class RelativeDirection(val char: Char) {
    Left('L'),
    Right('R')
}

fun <T> Pair<T, T>.x() = first
fun <T> Pair<T, T>.y() = second

fun Pair<Int, Int>.wrapAt(limits: Pair<Int, Int>): Pair<Int, Int> {
    val x = this.x().wrapAt(limits.x())
    val y = this.y().wrapAt(limits.y())
    return x to y
}