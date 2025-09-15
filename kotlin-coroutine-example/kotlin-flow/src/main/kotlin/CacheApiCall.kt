import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking

fun main() {

    val api = mapOf(
        "rahul" to 2,
        "ratul" to 3,
        "rahim" to 6,
        "aushka" to 8,
        "rupali" to 9,
        "diya" to 12,
        "divya" to 13,
        "mannu" to 14,
        "anjali" to 15
    )

    val cache = mutableMapOf<String,Int>()

    while (true) {
        print("key: ")
        val key = readln()

        // step 1: check cache for key
        val flowObj1: Flow<Int?> = flow {
            val value = cache[key]
            println("getting value from cache: $key => $value")
            emit(value)
        }

        // step 2: if found return the value,
        //          if not found then get from api store it in cache then return the value from cache

        val apiFlowObj: Flow<Int?> = flow {
            val value = api[key] ?: throw Exception("no value found in api for key = $key")
            println("getting value from api: $key => $value")
            emit(value)
        }.transform {
            if (null != it) {
                println("storing value in cache: $key => $it")
                cache[key] = it
                println("updated cache $cache")
                emit(it)
            }
            else {
                println("no value found in api for key=$key")
                emit(null)
            }
        }
            .catch { println("error occurred in api flow ${it.message}") }

        val flowObj: Flow<Int?> = flowObj1.transform {
            if (null == it) {
                println("no value found in cache, let's check in api")
                emitAll(apiFlowObj)
            }
            else {
                println("cache contains the value for key=$key value=$it")
                emit(it)
            }
        }

        // step 3: show the data

        runBlocking {
            flowObj
                .catch { println("error occurred while search value for $key: ${it.message}") }
                .collect {
                    println("----------- showing result ------------")
                    println("cache = $cache")
                    println("key=$key -> $it")
            }
        }
    }
}