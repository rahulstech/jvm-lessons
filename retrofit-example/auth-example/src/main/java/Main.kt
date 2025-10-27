import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking

val tokenProvider = AuthTokenProvider()

val client = Client.getClient(tokenProvider)

fun main() {

    print("Enter username: ")
    val username = readln()

    runBlocking {
        register(username)

        login(username)

        println("get profile info before access token expires")
        getProfile()

        // access token expires in 30 seconds, wait for 31 seconds to expire accessToken
        println("waiting for 31 seconds to let access token expire")
        delay(31000)

        getProfile() // this will successful as refresh token not expired and new access token is requested

        // refresh token expires in 60 seconds, wait more 21 seconds to expire refreshToken
        println("waiting for 31 seconds to let refresh token expire")
        delay(31000)

        // this will fail now as refresh token now expired and can not request new access token
        // user need to log in again
        getProfile()
    }
}


suspend fun register(username: String) {
    val response = client.noAuthService.register(RegisterRequest(username))
    if (response.isSuccessful) {
        val body = response.body()!!
        println(body.message)
    }
    else {
        println("register failed: ${response.errorBody()?.string()}")
    }
}

suspend fun login(username: String) {
    val response = client.noAuthService.login(LoginRequest(username))
    if (response.isSuccessful) {
        val body = response.body()!!
        println("token obtained successfully")
        tokenProvider.updateToken(body.accessToken,body.refreshToken)
    }
    else {
        println("login failed: ${response.errorBody()?.string()}")
    }
}

suspend fun getProfile() {
    val response = client.authService.getProfile()
    if (response.isSuccessful) {
        val body = response.body()!!
        println(body.message)
    }
    else {
        println("get-profile failed: ${response.errorBody()?.string()}")
    }
}




