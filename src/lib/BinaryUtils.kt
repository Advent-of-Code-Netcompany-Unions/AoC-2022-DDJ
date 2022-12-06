package lib

fun List<Int>.toBinaryInt(): Int {
    return this.map(Int::toString).fold("") { acc, ele -> acc + ele }.toInt(2)
}