package lib

import java.awt.Toolkit
import java.awt.datatransfer.StringSelection
import java.io.File
import kotlin.system.measureTimeMillis

@DslMarker
annotation class AocDsl

class Challenge<T> {

    private var year = 2022
    private var day = 1

    private val inputs = mutableListOf<String>()

    private lateinit var parser: suspend (File) -> T
    private var solvers = mutableListOf<suspend (T) -> String?>()

    suspend fun solveChallenge() {
        if (inputs.size == 0) {
            inputs.add("input.txt")
        }

        val clipboard = Toolkit.getDefaultToolkit().systemClipboard

        for (inp in inputs) {
            for (solver in solvers) {
                println("Solving $inp...")
                var res: String?
                val elapsed = measureTimeMillis {
                    res = solver(parser(openInput(inp)))
                }
                if(res != null) {
                    clipboard.setContents(StringSelection(res), null)
                }
                println("Time spent: $elapsed ms")
                println("Result: $res\n")
            }
        }
    }

    private suspend fun openInput(name: String): File {
        val f = File("src/aoc$year/day$day/$name")
        if (!f.isFile) {
            InputDownloader.instance.createInputFile(day, year)
        }
        return f
    }

    @AocDsl
    fun day(d: Int) {
        this.day = d
    }

    @AocDsl
    fun year(y: Int) {
        this.year = y
    }

    @AocDsl
    fun input(inp: String) {
        inputs += inp
    }

    @AocDsl
    fun parser(block: suspend (File) -> T) {
        parser = block
    }

    @AocDsl
    fun solver(block: suspend (T) -> String?) {
        solvers += block
    }
}

@AocDsl
suspend fun <T> challenge(block: Challenge<T>.() -> Unit) {
    val model = Challenge<T>()
    model.apply(block).solveChallenge()
}