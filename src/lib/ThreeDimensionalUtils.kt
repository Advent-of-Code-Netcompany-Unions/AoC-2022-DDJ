package lib

import kotlin.math.pow
import kotlin.math.sqrt

class Cube(val first: IntProgression, val second: IntProgression, val third: IntProgression) {

    infix fun intersects(other: Cube): Boolean {
        return this.first intersects other.first
                && this.second intersects other.second
                && this.third intersects other.third
    }

    infix fun intersect(other: Cube): Cube {
        return Cube(
            this.first intersect other.first,
            this.second intersect other.second,
            this.third intersect other.third
        )
    }

    fun sliceX(slice: IntProgression): List<Cube> {
        if (!(slice intersects this.first)) {
            return listOf(this)
        }
        if (slice.first <= this.first.first && slice.last >= this.first.last) {
            return listOf()
        }
        return listOf(
            Cube(this.first.first until slice.first, this.second, this.third),
            Cube(slice.last+1 .. this.first.last, this.second, this.third)
        )
            .filter { it.isNotEmpty() }
    }

    fun sliceX(slices: List<IntProgression>): List<Cube> {
        var res = listOf(this)
        slices.forEach { slice -> res = res.flatMap { it.sliceX(slice) } }
        return res
    }

    fun sliceY(slice: IntProgression): List<Cube> {
        if (!(slice intersects this.second)) {
            return listOf(this)
        }
        if (slice.first <= this.second.first && slice.last >= this.second.last) {
            return listOf()
        }
        return listOf(
            Cube(this.first, this.second.first until slice.first, this.third),
            Cube(this.first, slice.last+1 .. this.second.last, this.third)
        )
            .filter { it.isNotEmpty() }
    }

    fun sliceY(slices: List<IntProgression>): List<Cube> {
        var res = listOf(this)
        slices.forEach { slice -> res = res.flatMap { it.sliceY(slice) } }
        return res
    }

    fun getPoints(): Set<Triple<Int, Int, Int>> {
        return this.first.flatMap { x ->
            this.second.flatMap { y ->
                this.third.map { z ->
                    Triple(x, y, z)
                }
            }
        }.toSet()
    }

    operator fun minus(other: Cube): List<Cube> {

        if (!(this intersects other)) {
            return listOf(this)
        }

        val xs = this.first - other.first
        val ys = this.second - other.second
        val zs = this.third - other.third

        val res =
            xs.map { Cube(it, this.second, this.third) }
                .plus(ys.flatMap { Cube(this.first, it, this.third).sliceX(xs) })
                .plus(zs.flatMap { Cube(this.first, this.second, it).sliceX(xs).flatMap { it.sliceY(ys) } })
                .filter { it.isNotEmpty() }

        return res
    }

    fun size(): Long {
        return this.first.size().toLong() * this.second.size() * this.third.size()
    }

    fun isEmpty(): Boolean {
        return this.first.size() == 0 || this.second.size() == 0 || this.third.size() == 0
    }

    fun isNotEmpty(): Boolean {
        return this.first.size() > 0 && this.second.size() > 0 && this.third.size() > 0
    }

    override fun equals(other: Any?): Boolean {
        if (other is Cube) {
            return this.first == other.first && this.second == other.second && this.third == other.third
        }
        return false
    }

    override fun hashCode(): Int {
        var result = first.hashCode()
        result = 31 * result + second.hashCode()
        result = 31 * result + third.hashCode()
        return result
    }
}

fun Triple<Int, Int, Int>.distanceTo(other: Triple<Int, Int, Int>): Double {

    return sqrt((this.first.toDouble() - other.first).pow(2) + (this.second.toDouble() - other.second).pow(2) + (this.third.toDouble() - other.third).pow(2))
}

fun Triple<Int, Int, Int>.getNeighbours(): Set<Triple<Int, Int, Int>> {
    return setOf(
        Triple(this.first - 1, this.second, this.third),
        Triple(this.first + 1, this.second, this.third),
        Triple(this.first, this.second - 1, this.third),
        Triple(this.first, this.second + 1, this.third),
        Triple(this.first, this.second, this.third - 1),
        Triple(this.first, this.second, this.third + 1))
}