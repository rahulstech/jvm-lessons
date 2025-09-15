import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.File

interface Storage {

    fun readAll(): List<Any>?

    fun writeAll(entries: List<Any>?)
}

abstract class FileStorage(private val filename: String) : Storage {

    private val file: File = File(filename)

    override fun readAll(): List<Any>? {
        val entries = mutableListOf<Any>()
        try {
            file.bufferedReader().use {
                var data: Any?
                while (true) {
                    data = readSingle(it)
                    if (null == data) break
                    entries.add(data)
                }
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        return entries
    }

    override fun writeAll(entries: List<Any>?) {
        try {
            file.bufferedWriter().use { writer ->
                entries?.forEach { data ->
                    writeSingle(writer, data)
                }
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    abstract fun readSingle(reader: BufferedReader): Any?

    abstract fun writeSingle(writer: BufferedWriter, data: Any)
}

class CsvFileStorage(filename: String, val callbackToString: (Any) -> String, val callBackFromString: (String) -> Any) :
    FileStorage(filename) {
    override fun readSingle(reader: BufferedReader): Any? {
        val line = reader.readLine() ?: return null
        return callBackFromString(line)
    }

    override fun writeSingle(writer: BufferedWriter, data: Any) {
        val line = callbackToString(data)
        writer.write(line)
        writer.newLine()
    }
}

object StorageFactory {

    fun inMemoryStorage(): Storage {
        return object : Storage {

            override fun readAll(): List<Any> = mutableListOf()

            override fun writeAll(entries: List<Any>?) {}
        }
    }

    fun csvFileStorage(
        filename: String,
        callbackToString: (Any) -> String,
        callBackFromString: (String) -> Any
    ): Storage =
        CsvFileStorage(filename, callbackToString, callBackFromString)
}