package lib

infix fun String.intersect(other: String): Set<Char> {
    return this.toSet().intersect(other.toSet())
}

infix fun String.intersect(other: Set<Char>): Set<Char> {
    return this.toSet().intersect(other)
}

infix fun Set<Char>.intersect(other: String): Set<Char> {
    return this.intersect(other.toSet())
}

infix fun String.intersects(other: String): Boolean {
    return this.toSet().intersect(other.toSet()).isNotEmpty()
}

fun String.toPair(separator: String): Pair<String, String> {
    val parts = this.split(separator)
    return parts[0] to parts[1]
}