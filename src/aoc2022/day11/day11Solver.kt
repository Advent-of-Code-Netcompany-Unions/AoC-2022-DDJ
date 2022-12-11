package aoc2022.day11

import lib.*
import java.math.BigInteger

suspend fun main() {
    challenge<List<List<String>>> {

        day(11)
        year(2022)

        //input("example.txt")

        parser {
            it.getStringsGroupedByEmptyLine()
        }

        solver {
            println("Solving challenge 1...")
            val monkeys = it.map { Monkey(it) }
            val modVal = monkeys.foldRight(BigInteger.valueOf(3L)) { monkey, acc -> monkey.testVal.multiply(acc) }
            monkeys.forEach { it.modVal = modVal }
            (1..20).forEach {
                monkeys.forEach {
                    it.takeTurn(monkeys)
                }
            }
            val monkeyBusiness = monkeys.map { it.activity }.sortedDescending().take(2)
            (monkeyBusiness[0] * monkeyBusiness[1]).toString()
        }

        solver {
            println("Solving challenge 2...")
            val monkeys = it.map { ConcerningMonkey(it) }
            val modVal = monkeys.foldRight(BigInteger.ONE) { monkey, acc -> monkey.testVal.multiply(acc) }
            monkeys.forEach { it.modVal = modVal }
            (1..10000).forEach {
                monkeys.forEach {
                    it.takeTurn(monkeys)
                }
            }
            val monkeyBusiness = monkeys.map { it.activity }.sortedDescending().take(2)
            (monkeyBusiness[0] * monkeyBusiness[1]).toString()
        }
    }
}

open class Monkey(initializers: List<String>) {

    var modVal: BigInteger = BigInteger.ZERO

    var activity: BigInteger = BigInteger.ZERO
    var testVal: BigInteger = BigInteger.ZERO
    var inventory = createInventory(initializers[1])
    val operation = createOperation(initializers[2])
    private val test = createTest(initializers[3])
    private val recipients = createRecipients(initializers[4], initializers[5])


    private fun createInventory(initializer: String): MutableList<BigInteger> {
        return initializer.trim().replace("Starting items: ", "")
            .split(", ")
            .map { it.toBigInteger() }
            .toMutableList()
    }

    private fun createOperation(initializer: String): (BigInteger) -> BigInteger {
        val ope = initializer[23]
        val value = initializer.substring(25)
        if (value == "old") {
            return when (ope) {
                '*' -> { x -> x * x }
                '+' -> { x -> x + x }
                else -> throw Exception("Unknown operator: $initializer")
            }
        }
        val bigValue = value.toBigInteger()
        return when (ope) {
            '*' -> { x -> x * bigValue }
            '+' -> { x -> x + bigValue }
            else -> throw Exception("Unknown operator: $initializer")
        }
    }

    private fun createTest(initializer: String): (BigInteger) -> Boolean {
        val value = initializer.trim().replace("Test: divisible by ", "").toBigInteger()
        testVal = value
        return { x -> x % testVal == BigInteger.ZERO }
    }

    private fun createRecipients(trueString: String, falseString: String): Pair<Int, Int> {
        val first = trueString.trim().replace("If true: throw to monkey ", "").toInt()
        val second = falseString.trim().replace("If false: throw to monkey ", "").toInt()
        return first to second
    }

    fun takeTurn(monkeys: List<Monkey>) {
        while (inventory.any()) {
            val item = inspect()
            activity++
            deliverCarefully(item, monkeys)
        }
    }

    open fun inspect(): BigInteger {
        val item = inventory.removeAt(0)
        return operation(item).divide(BigInteger.valueOf(3)) % modVal
    }

    private fun deliverCarefully(item: BigInteger, monkeys: List<Monkey>) {
        if (test(item)) {
            monkeys[recipients.first].catch(item)
        } else {
            monkeys[recipients.second].catch(item)
        }
    }

    private fun catch(item: BigInteger) {
        inventory += item
    }
}

class ConcerningMonkey(initializers: List<String>) : Monkey(initializers) {
    override fun inspect() : BigInteger {
        val item = inventory.removeAt(0)
        return operation(item) % modVal
    }
}