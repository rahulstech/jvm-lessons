import kotlinx.coroutines.*
import java.io.File

fun countWordAppearanceInString(line: String, wordToFind: String): Long {
    return line.split(wordToFind).size - 1L
}

fun createWordCountTask(linesToProcess: List<String>, wordToFind: String): Deferred<Long> {
    return GlobalScope.async {
        var count = 0L
        linesToProcess.forEach { processLine ->
            count += countWordAppearanceInString(processLine, wordToFind)
        }
        count
    }
}

fun main() {
    runBlocking {

        val LINES_TO_PROCESS = 50
        val wordToFind = "AI"

        runCatching {
            val tasks = mutableListOf<Deferred<Long>>()
            File("./content.txt").bufferedReader().use { reader ->
                var line: String?
                var remaining_lines = LINES_TO_PROCESS
                val lines = mutableListOf<String>()

                while (true) {
                    line = reader.readLine()
                    if (null == line) {
                        val result = createWordCountTask(lines.toList(), wordToFind)
                        tasks.add(result)
                        break
                    }
                    if (remaining_lines == 0) {
                        val result = createWordCountTask(lines.toList(), wordToFind)
                        tasks.add(result)
                        lines.clear()
                        remaining_lines = LINES_TO_PROCESS
                    }
                    lines.add(line)
                    remaining_lines--
                }
            }

            val wordCount = tasks.awaitAll().sum()
            println("the word '$wordToFind' found $wordCount times")
        }
    }
}