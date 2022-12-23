package aoc2022.day21

import lib.*

suspend fun main() {
    challenge<MutableMap<String, Operation>> {

        day(21)
        year(2022)

        parser {
            it.readLines()
                .map { it.split(": ") }
                .map { it[0] to operationOf(it[1], it[0]) }
                .toMap()
                .toMutableMap()
        }

        solver {
            println("Solving challenge 1...")
            doMath(it["root"]!!, it).toString()
        }

        solver {
            println("Solving challenge 2...")
            val root = it["root"]!! as MonkeyMath

            val (human, nonHuman, _) = findHuman(it[root.monkey1]!!, it[root.monkey2]!!, it)
            val matchRes = doMath(nonHuman, it)
            val res = findHumanValue(human, matchRes, it)

            res.toString()
        }
    }
}

fun doMath(operation: Operation, monkeys: MutableMap<String, Operation>): Long {
    if (operation is ValueOperation)
        return operation.value
    else if (operation is MonkeyMath) {
        val arg1 = doMath(monkeys[operation.monkey1]!!, monkeys)
        val arg2 = doMath(monkeys[operation.monkey2]!!, monkeys)
        monkeys[operation.monkey1] = ValueOperation(arg1, operation.monkey1)
        monkeys[operation.monkey2] = ValueOperation(arg2, operation.monkey2)
        return operation.ope.getLambda().invoke(arg1, arg2)
    }
    throw Exception("Unknown math: $operation")
}

fun findHumanValue(monkey: Operation, res: Long, monkeys: MutableMap<String, Operation>): Long {
    if (monkey is ValueOperation)
        return res

    monkey as MonkeyMath
    val (human, nonHuman, missingFirst) = findHuman(monkeys[monkey.monkey1]!!, monkeys[monkey.monkey2]!!, monkeys)
    val monkeyRes = doMath(nonHuman, monkeys)
    val matchRes = monkey.ope.getInverse(missingFirst).invoke(res, monkeyRes)
    return findHumanValue(human, matchRes, monkeys)
}

fun findHuman(a: Operation, b: Operation, monkeys: Map<String, Operation>): Triple<Operation, Operation, Boolean> {
    if (a.hasHuman(monkeys))
        return Triple(a, b, true)
    return Triple(b, a, false)
}

fun operationOf(rep: String, name: String): Operation {
    val parts = rep.split(" ")
    if (parts.size > 1) {
        val ope = BinaryOperation(parts[1])
        return MonkeyMath(ope, parts[0], parts[2])
    }
    return ValueOperation(rep.toLong(), name)
}

interface Operation {
    fun hasHuman(monkeys: Map<String, Operation>): Boolean
}

class ValueOperation(val value: Long, val name: String) : Operation {
    override fun hasHuman(monkeys: Map<String, Operation>): Boolean {
        return name == "humn"
    }
}

class BinaryOperation(val operator: String) {

    fun getLambda(): (Long, Long) -> Long {
        return when (operator) {
            "+" -> { a, b -> a + b }
            "-" -> { a, b -> a - b }
            "*" -> { a, b -> a * b }
            "/" -> { a, b -> a / b }
            "%" -> { a, b -> a % b }
            else -> throw Exception("Unknown operator: $operator")
        }
    }

    fun getInverse(missingFirstOperand: Boolean): (Long, Long) -> Long {

        return when (operator) {
            "+" -> { a, b -> a - b}
            "-" -> if (missingFirstOperand) { a, c -> a + c } else { a, b -> b - a }
            "*" -> { a, b -> a / b }
            "/" -> if (missingFirstOperand) { a, c -> a * c } else { a, b -> b / a }
            else -> throw Exception("Unknown operator: $operator")
        }
    }
}

class MonkeyMath(val ope: BinaryOperation, val monkey1: String, val monkey2: String) : Operation {
    override fun hasHuman(monkeys: Map<String, Operation>): Boolean {
        if (monkey1 == "humn" || monkey2 == "humn") {
            return true
        }
        return monkeys[monkey1]!!.hasHuman(monkeys) || monkeys[monkey2]!!.hasHuman(monkeys)
    }
}
