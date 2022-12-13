package aoc2022.day13

import lib.*

suspend fun main() {
    challenge<List<List<String>>> {

        day(13)
        year(2022)

        //input("example.txt")

        parser {
            it.getStringsGroupedByEmptyLine()
        }

        solver {
            println("Solving challenge 1...")
            val packets = it.map { NestedList(it[0]) to NestedList(it[1]) }
            packets.mapIndexed { i, packet -> i + 1 to (packet.first.compareWith(packet.second) < 0) }
                .sumOf { if (it.second) it.first else 0 }
                .toString()
        }

        solver {
            println("Solving challenge 2...")
            val packets = it.asSequence()
                .flatten()
                .map { NestedList(it) }
                .plus(listOf(NestedList("[2]"), NestedList("[6]")))
                .sortedWith { a, b -> a.compareWith(b) }
                .mapIndexed { i, p -> p.data to i + 1 }
                .filter { it.first == "[2]" || it.first == "[6]" }.toList()
            (packets[0].second * packets[1].second).toString()
        }
    }
}

interface PacketData {
    fun compareWith(other: PacketData): Int
}

class NestedList(val data: String) : PacketData {
    val packetData = mutableListOf<PacketData>()

    init {
        val stack = ArrayDeque<Int>()
        if (data != "[]") {
            val contents = data.substring(1, data.length - 1)
            var i = 0
            while (i < contents.length) {
                when (contents[i]) {
                    '[' -> stack.addLast(i)
                    ']' -> {
                        val start = stack.removeLast()
                        if (stack.isEmpty()) {
                            packetData.add(NestedList(contents.substring(start, i + 1)))
                        }
                    }
                    ',' -> { }
                    else ->
                        if (stack.isEmpty()) {
                            val num = contents.substring(i).takeWhile { it.isDigit() }
                            i += num.length
                            packetData.add(NestedValue(num.toInt()))
                            continue
                        }
                }
                i++
            }
        }
    }

    override fun compareWith(other: PacketData): Int {
        return when (other) {
            is NestedValue -> this.compareWith(other.asNestedList())
            is NestedList -> packetData.toMutableList().compareWith(other.packetData.toMutableList())
            else -> throw Exception("Invalid type of other: $other")
        }
    }

    private fun MutableList<PacketData>.compareWith(other: MutableList<PacketData>): Int {
        var res = 0
        while (res == 0) {
            if (this.size == 0 || other.size == 0) {
                return this.size.compareTo(other.size)
            } else {
                res = this.removeFirst().compareWith(other.removeFirst())
            }
        }
        return res
    }
}

class NestedValue(val n: Int) : PacketData {

    override fun compareWith(other: PacketData): Int {
        return if (other is NestedValue) {
            this.n.compareTo(other.n)
        } else {
            this.asNestedList().compareWith(other)
        }
    }

    fun asNestedList(): NestedList {
        return NestedList("[$n]")
    }
}