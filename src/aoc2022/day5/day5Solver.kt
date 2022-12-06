package aoc2022.day5

import lib.*

suspend fun main() {
    challenge<Pair<List<String>, List<List<Int>>>> {

        day(5)
        year(2022)

        //input("example.txt")

        parser {
            val parts = it.getStringsGroupedByEmptyLine()
            val stacks = listOf("") + parseCrates(parts[0]) //Dummy string at index 0 to account for 1-indexed instructions
            val instructions = parseInstructions(parts[1])

            stacks to instructions
        }

        solver {
            println("Solving challenge 1...")
            var order = it.first
            it.second.forEach { order = move9000(order, it[0], it[1], it[2]) }
            order.drop(1).map { it.last() }
                .joinToString("")
        }

        solver {
            println("Solving challenge 2...")
            var order = it.first
            it.second.forEach { order = move9001(order, it[0], it[1], it[2]) }
            order.drop(1).map { it.last() }
                .joinToString("")
        }
    }
}

fun parseCrates(inp: List<String>): List<String> {
    return inp.get2DArray()
        .filter { it.last().isDigit() }
        .map { it.joinToString("").trim().reversed().drop(1) }
}

fun parseInstructions(inp: List<String>): List<List<Int>> {
    return inp.map { it.replace("move ", "").replace("from ", "").replace("to ", "") }
        .map { it.split(' ') }
        .map { it.map { it.toInt() } }
}

fun move9000(stacks: List<String>, amount: Int, src: Int, dest: Int): List<String> {
    val res = stacks.toMutableList()
    res[dest] = stacks[dest] + stacks[src].substring(stacks[src].length - amount).reversed()
    res[src] = stacks[src].substring(0, stacks[src].length - amount)
    return res
}

fun move9001(stacks: List<String>, amount: Int, src: Int, dest: Int): List<String> {
    val res = stacks.toMutableList()
    res[dest] = stacks[dest] + stacks[src].substring(stacks[src].length - amount)
    res[src] = stacks[src].substring(0, stacks[src].length - amount)
    return res
}
