package lib

fun isWithinSquare(point: Pair<Int, Int>, square: Pair<Pair<Int, Int>, Pair<Int, Int>>): Boolean {
    val (xMin, xMax) = square.first
    val (yMin, yMax) = square.second
    return point.first in xMin..xMax && point.second in yMin..yMax
}