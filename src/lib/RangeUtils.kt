package lib

fun IntProgression.size(): Int {
    return if (this.isEmpty()) 0 else (this.last - this.first + 1) / this.step
}

infix fun IntProgression.intersects(other: IntProgression): Boolean {
    return maxOf(this.first, other.first) <= minOf(this.last, other.last)
}

infix fun IntProgression.intersect(other: IntProgression): IntProgression {
    val opt1 = other.first..this.last
    val opt2 = this.first..other.last
    return listOf(this, other, opt1, opt2).minByOrNull { it.size() }!!
}

operator fun IntProgression.minus(other: IntProgression): List<IntProgression> {
    return listOf(this.first until other.first, other.last+1 ..this.last).filter { !it.isEmpty() }
}

fun String.toIntProgression(sep: String): IntProgression {
    val parts = this.split(sep)
    return parts[0].toInt()..parts[1].toInt()
}

fun IntProgression.startAt(n: Int): IntProgression {
    return maxOf(this.first, n)..this.last
}

fun IntProgression.stopAt(n: Int): IntProgression {
    return this.first..minOf(this.last, n)
}

fun LongProgression.size(): Long {
    return if (this.isEmpty()) 0 else (this.last - this.first + 1) / this.step
}

infix fun LongProgression.intersects(other: LongProgression): Boolean {
    return maxOf(this.first, other.first) <= minOf(this.last, other.last)
}

infix fun LongProgression.intersect(other: LongProgression): LongProgression {
    val opt1 = other.first..this.last
    val opt2 = this.first..other.last
    return listOf(this, other, opt1, opt2).minByOrNull { it.size() }!!
}

operator fun LongProgression.minus(other: LongProgression): List<LongProgression> {
    return listOf(this.first..other.first, other.last..this.last).filter { !it.isEmpty() }
}