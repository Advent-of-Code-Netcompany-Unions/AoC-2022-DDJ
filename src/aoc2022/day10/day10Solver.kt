package aoc2022.day10

import lib.*
import kotlin.math.absoluteValue

suspend fun main() {
    challenge<List<String>> {

        day(10)
        year(2022)

        //input("example.txt")

        parser {
            it.readLines()
        }

        solver {
            println("Solving challenge 1...")

            val measureCycles = listOf(20, 60, 100, 140, 180, 220)

            val cpu = cpu(it)
            var sum = 0

            (1..220).forEach {
                if(it in measureCycles) {
                    sum += cpu.x * it
                }
                cpu.runCycle()
            }
            sum.toString()
        }

        solver {
            println("Solving challenge 2...")
            val cpu = cpu(it)
            val screen = crt(cpu)
            "\n" + screen.run()
                .joinToString("") { it.joinToString("") + "\n" }
        }
    }
}

class cpu(val program: List<String>) {

    private var currentInst = ""
    private var ip = 0
    var x = 1

    fun runCycle() {

        if (currentInst.startsWith("addx")) {
            val addend = currentInst.substring(5).toInt()
            x += addend
            ip++
            currentInst = ""
            return
        }

        val inst = program[ip]
        if (inst == "noop") {
            ip++
            return
        } else {
            currentInst = inst
        }
    }
}

class crt(val cpu: cpu) {

    val screen = Array(6) { Array(40) {'.'} }

    fun run(): Array<Array<Char>> {
        (0..239).forEach{
            val toDraw = it / 40 to it % 40

            if((cpu.x - toDraw.second).absoluteValue < 2)
                screen[toDraw.first][toDraw.second] = '#'

            cpu.runCycle()
        }
        return screen
    }
}