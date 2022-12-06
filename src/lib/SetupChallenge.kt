package lib

import java.io.File
import java.util.*


var day: Int? = null
var year: Int? = 2022

suspend fun main() {

    day = day ?: Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
    year = year ?: 2022

    val path = "src/aoc$year/day$day/"
    File(path).mkdirs()

    InputDownloader.instance.createInputFile(day!!, year!!)
    //InputDownloader.instance.createDescriptionFile(day!!, year!!)

    val template = File("src/main/resources/template").readText()
    val solver = template
        .replace("<day>", "$day")
        .replace("<year>", "$year")
    val output = File(path + "day${day}Solver.kt")
    if (!output.exists()) {
        output.writeText(solver)
    }
}