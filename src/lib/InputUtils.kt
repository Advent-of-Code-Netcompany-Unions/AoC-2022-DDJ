package lib

import java.io.File

val newline: String = System.lineSeparator()

fun File.getStringsGroupedByEmptyLine(): List<List<String>> {
    return this.readText()
        .trimEnd()
        .split("$newline$newline")
        .map { it.split(newline) }
}

fun List<String>.get2DArray(): Array<Array<Char>> {
    val height = this.size
    val width = this.first().length

    return Array(width) { i -> Array(height) { j -> this[j][i] } }
}

fun List<String>.readStringsAsDigits(): List<List<Int>> {
    return this.map {
        it.map {
            "$it".toInt()
        }
    }
}