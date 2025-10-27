import com.google.gson.GsonBuilder
import okhttp3.Authenticator
import okhttp3.Interceptor
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import okhttp3.Route
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

const val BASE_URL = "http://localhost:3000"

class Client private constructor(
    private val tokenProvider: AuthTokenProvider
) {

    companion object {
        private var client: Client? = null

        fun getClient(tokenProvider: AuthTokenProvider): Client {
            if (client == null) {
                client = Client(tokenProvider)
            }
            return client!!
        }
    }

    val noAuthService: NoAuthService by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val service = retrofit.create(NoAuthService::class.java)
        service
    }

    val authService: AuthService by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(createAuthHttpClient())
            .build()
        val service = retrofit.create(AuthService::class.java)
        service
    }

    private fun createAuthHttpClient(): OkHttpClient {
        val interceptor = Interceptor {
            val accessToken = tokenProvider.getAccessToken()
            val request = if (null != accessToken) {
                println("found accessToken in provider")
                it.request().newBuilder()
                    .header("Authorization", "Bearer $accessToken")
                    .build()
            }
            else {
                println("didn't find accessToken, need to refresh token")
                it.request()
            }
            it.proceed(request)
        }

        return OkHttpClient.Builder()
            .addInterceptor(interceptor)
            // authorizer is automatically called when last unsuccessful request's response code 401 Unauthorized
            .authenticator(TokenAuthenticator(tokenProvider))
            .build()
    }
}

class TokenAuthenticator(
    private val tokenProvider: AuthTokenProvider
): Authenticator {

    // implement this method and add token refresh
    // the response argument is the response of the last unauthorized request
    override fun authenticate(router: Route?, response: Response): Request? {
        val refreshToken = tokenProvider.getRefreshToken()
        if (null == refreshToken) {
            println("did not find refresh token")
            tokenProvider.removeToken()
            return null
        }

        // NOTE: requesting new token should be done synchronously,
        // otherwise this method will return immediately and the actual request will be failed
        val tokenResponse = requestNewToken(refreshToken) ?: return null

        println("obtained new accessToken")

        tokenProvider.updateToken(tokenResponse.accessToken,tokenResponse.refreshToken)
        val accessToken = tokenResponse.accessToken

        return response.request().newBuilder()
            // NOTE: using header() instead of addHeader(). header() replaces existing one, addHeader() appends a new one
            // since the existing request already has Authorization header, adding one extra will make server confuse
            // therefore replace existing all Authorization header and keep the new only
            .header("Authorization", "Bearer $accessToken")
            .build()

    }

    private fun requestNewToken(refreshToken: String): TokenResponse? {

        val gson = GsonBuilder()
            .setLenient()
            .create()

        val content = gson.toJson(mapOf("refreshToken" to refreshToken)).toString()
        val body = RequestBody.create(MediaType.parse("application/json"), content)

        val httpClient = OkHttpClient()
        val request = Request.Builder()
            .url("$BASE_URL/refresh")
            .post(body)
            .build()

        val response = httpClient.newCall(request).execute()
        if (response.isSuccessful) {
            return response.body().let { body ->
                gson.fromJson(body!!.string(), TokenResponse::class.java)
            }
        }

        return null
    }
}