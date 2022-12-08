package aoc2022.day8

import lib.*

suspend fun main() {
    challenge<List<List<Int>>> {

        day(8)
        year(2022)

        parser {
            it.readLines()
                .get2DArray()
                .map { it.map { it.digitToInt() } }
        }

        solver {
            println("Solving challenge 1...")

            val xMax = it.size - 1
            val yMax = it.first().size - 1

            var i = 1
            var j = 1
            var numVisible = xMax * 2 + yMax * 2

            while (i < xMax) {
                while (j < yMax) {

                    val size = it[i][j]
                    val left = it.take(i).map { it[j] }
                    val right = it.drop(i + 1).map { it[j] }
                    val up = it[i].take(j)
                    val down = it[i].drop(j + 1)

                    if (sequenceOf(left, right, up, down).any { it.all { it < size } })
                        numVisible++

                    j++
                }
                j = 1
                i++
            }
            numVisible.toString()
        }

        solver {
            println("Solving challenge 2...")
            val xMax = it.size - 1
            val yMax = it.first().size - 1

            var i = 1
            var j = 1
            var best = 0

            while (i < xMax) {
                while (j < yMax) {
                    val size = it[i][j]

                    val left = it.take(i).map { it[j] }
                    val right = it.drop(i + 1).map { it[j] }
                    val up = it[i].take(j)
                    val down = it[i].drop(j + 1)

                    var leftVisible = left.reversed().takeWhile { it < size }.size
                    var rightVisible = right.takeWhile { it < size }.size
                    var upVisible = up.reversed().takeWhile { it < size }.size
                    var downVisible = down.takeWhile { it < size }.size

                    if (leftVisible < left.size)
                        leftVisible++

                    if (rightVisible < right.size)
                        rightVisible++

                    if (upVisible < up.size)
                        upVisible++

                    if (downVisible < down.size)
                        downVisible++

                    val score = leftVisible * rightVisible * upVisible * downVisible

                    if (score > best)
                        best = score

                    j++
                }
                j = 1
                i++
            }
            best.toString()
        }
    }
}