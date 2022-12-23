package lib

fun Int.wrapAt(limit: Int): Int {
    return if (this > -1) {
        this % limit
    } else {
        limit + (this % limit)
    }
}