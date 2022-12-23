package aoc2022.day20

import lib.*
import kotlin.math.absoluteValue

suspend fun main() {
    challenge<List<Int>> {

        day(20)
        year(2022)

        input("example.txt")
        input("input.txt")

        parser {
            it.readLines()
                .map { it.toInt() }
        }

        solver {
            println("Solving challenge 1...")
            val n = it.size
            val res = it.mix(1, 1)
            val base = res.indexOf(0)
            listOf(1000, 2000, 3000).sumOf { res[(base + it).wrapAt(n)] }.toString()
        }

        solver {
            println("Solving challenge 2...")
            val key = 811589153L
            val n = it.size
            val res = it.mix(10, key)
            val base = res.indexOf(0L)
            listOf(1000, 2000, 3000).sumOf { res[(base + it) % n] }.toString()
        }
    }
}

fun List<Int>.mix(n: Int, key: Long): List<Long> {
    val linked = LinkedList(this.map { it * key })
    val order = mutableListOf<LinkedNode<Long>>()
    var current = linked.head
    repeat(this.size) {
        order.add(current)
        current = current.next
    }
    repeat(n) {
        order.forEach {
            it.move((it.value % (this.size-1)).toInt())
            //println("${linked.toList(this.size)}")
        }
    }
    return linked.toList(this.size)
}

class LinkedList<T>(eles: List<T>) {

    val head: LinkedNode<T>

    init {
        val initList = eles.map { LinkedNode(it) }
        val size = initList.size
        initList.indices.forEach {
            initList[it].next = initList[(it + 1) % size]
            initList[it].prev = if (it > 0) initList[it - 1] else initList.last()
        }
        head = initList.first()
    }

    fun toList(n: Int): List<T> {
        val res = mutableListOf<T>()
        var current = head
        repeat(n) {
            res.add(current.value)
            current = current.next
        }
        return res
    }
}

class LinkedNode<T>(val value: T) {
    var next = this
    var prev = this

    fun addBefore(newNode: LinkedNode<T>) {
        val prev = this.prev

        newNode.prev = prev
        prev.next = newNode

        newNode.next = this
        this.prev = newNode
    }

    fun addAfter(newNode: LinkedNode<T>) {
        val next = this.next

        newNode.next = next
        next.prev = newNode

        newNode.prev = this
        this.next = newNode
    }

    fun advance() {
        val temp = this.next
        this.remove()
        temp.addAfter(this)

    }

    fun descend() {
        val temp = this.prev
        this.remove()
        temp.addBefore(this)
    }

    fun move(n: Int) {
        if (n < 0) {
            repeat(n.absoluteValue) { this.descend() }
        } else {
            repeat(n) { this.advance() }
        }
    }

    fun remove() {
        prev.next = this.next
        next.prev = this.prev
    }
}