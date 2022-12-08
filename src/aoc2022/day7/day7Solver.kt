package aoc2022.day7

import lib.*
import lib.graph.*
import java.lang.IllegalArgumentException
import kotlin.sequences.*

suspend fun main() {
    challenge<List<List<String>>> {

        day(7)
        year(2022)

        parser {
            it.readText()
                .split("$ ")
                .filter { it.isNotEmpty() }
                .map { it.trim().split(System.lineSeparator()) }
        }

        solver {
            println("Solving challenge 1...")
            val root: Tree<Pair<String, Long>> = Root("/" to 0L, mutableListOf())

            var current = root
            it.forEach { current = current.run(it.first(), it.drop(1)) }

            root.nodes
                .filter { !it.isLeaf() && it.getSize() <= 100000 }
                .sumOf { it.getSize() }
                .toString()

        }

        solver {
            println("Solving challenge 2...")
            val root: Tree<Pair<String, Long>> = Root("/" to 0L, mutableListOf())

            var current = root
            it.forEach { current = current.run(it.first(), it.drop(1)) }

            val totalSpace = 70000000
            val requiredSpace = 30000000
            val usedSpace = root.getSize()
            val availableSpace = totalSpace - usedSpace
            val toFree = requiredSpace - availableSpace

            root.nodes
                .filter { !it.isLeaf() && it.getSize() > toFree }
                .minOf { it.getSize() }.toString()

        }
    }
}

fun getTree(desc: String, parent: Tree<Pair<String, Long>>): Tree<Pair<String, Long>> {
    if (desc.startsWith("dir")) {
        return Node(desc.substring(4) to 0, mutableListOf(), parent)
    }
    val vals = desc.split(" ")
    return Leaf(vals[1] to vals[0].toLong(), parent)
}

fun Tree<Pair<String, Long>>.run(cmd: String, output: List<String>): Tree<Pair<String, Long>> {
    when (cmd.substring(0..1)) {
        "cd" -> return this.navigate(cmd.substring(3))
        "ls" -> output.forEach { children.add(getTree(it, this)) }
        else -> throw IllegalArgumentException(cmd)
    }

    return this
}

fun Tree<Pair<String, Long>>.navigate(dest: String): Tree<Pair<String, Long>> {
    return when (dest) {
        "/" -> this.root
        ".." -> if (this.parent != null) this.parent!! else throw Exception("Root has no parent")
        else -> children.first { it.contents.first == dest }
    }
}

fun Tree<Pair<String, Long>>.getSize(): Long {
    if (this.isLeaf())
        return this.contents.second

    return this.children.sumOf { it.getSize() }
}