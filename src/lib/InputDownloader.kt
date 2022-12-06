package lib

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import java.io.File

class InputDownloader {

    private val session = File("src/main/resources/session").readText()

    suspend fun downloadInput(day: Int, year: Int): String = download(day, year, "/input")
    suspend fun downloadDescription(day: Int, year: Int) : String = download(day, year, "")

    suspend fun download(day: Int, year: Int, suffix: String): String {
        val client = HttpClient(CIO)
        val resp: HttpResponse = client.get("https://adventofcode.com/$year/day/$day$suffix") {cookie("session", session)}
        client.close()
        return resp.bodyAsText()
    }

    suspend fun createInputFile(day: Int, year: Int) = createFile(day, year, "input.txt", downloadInput(day, year))
    suspend fun createDescriptionFile(day: Int, year: Int) = createFile(day, year, "description.html", downloadDescription(day, year))

    fun createFile(day: Int, year: Int, fileName: String, content: String) {
        val f = File("src/aoc$year/day$day/$fileName")
        if(!f.isFile)
        {
            f.parentFile.mkdirs()
            f.writeText(content.replace("\n", System.lineSeparator()))
        }
    }

    companion object {
        val instance: InputDownloader = InputDownloader()
    }
}