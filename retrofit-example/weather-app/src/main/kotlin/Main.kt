import kotlinx.coroutines.runBlocking

fun main(): Unit = runBlocking {

    val apikey = System.getProperty("api.key")
    val client = WeatherClient.getInstance(apikey)
    val api = client.api

    val keyword = "Kandi"
    val id = "1118644"

    getTimezone(api, id)
}

suspend fun searchCities(api: WeatherApi, keyword: String) {
    println("search result for keyword: $keyword")
    val response = api.search(keyword)
    if (!response.isSuccessful) {
        println("no result found for $keyword")
    }
    else {
        val result: List<Location>? = response.body()
        result?.forEach {
            println(it)
        }
    }
}

suspend fun getTimezone(api: WeatherApi, id: String) {
    println("timezone for city with id: $id")
    val response = api.getTimezone("id:$id")
    if (!response.isSuccessful) {
        println("no city found for id $id")
    }
    else {
        val result: Timezone? = response.body()
        println(result?.location)
    }
}