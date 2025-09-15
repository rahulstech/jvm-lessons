import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

data class Location(val id: String, val name: String, val region: String, val country: String, val lat: Double, val lon: Double, val tz_id: String) {

    override fun toString(): String {
        return "Location(id: $id, name: $name, region: $region, country: $country, lat: $lat, lon: $lon, tz_id: $tz_id)"
    }
}

data class Timezone(val location: Location)

interface WeatherApi {

    @GET("search.json")
    suspend fun search(
        @Query("q") q: String
    ): Response<List<Location>>

    @GET("timezone.json")
    suspend fun getTimezone(
        @Query("q") q: String
    ): Response<Timezone>
}

class WeatherClient private constructor(private val key: String) {

    companion object {

        private lateinit var key: String

        private val instance: WeatherClient by lazy { WeatherClient(key) }

        fun getInstance(key: String): WeatherClient {
            this.key = key
            return instance
        }
    }

    private val retrofit: Retrofit by lazy {

        val client = getOkkHttpClient()

        val builder = Retrofit.Builder()
            .baseUrl("https://api.weatherapi.com/v1/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)

        builder.build()
    }

    private fun getOkkHttpClient(): OkHttpClient {
        val apikeyInterceptor = Interceptor {
            val ogReq = it.request()
            val ogUrl = ogReq.url()
            val url = ogUrl.newBuilder().addQueryParameter("key",key).build()
            val req = ogReq.newBuilder().url(url).build()
            it.proceed(req)
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(apikeyInterceptor)
            .build()

        return client
    }

    val api: WeatherApi by lazy {
        retrofit.create(WeatherApi::class.java)
    }
}