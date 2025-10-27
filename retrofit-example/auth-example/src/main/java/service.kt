import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

data class SuccessResponse (
    val message: String,
)

data class TokenResponse (
    val accessToken: String,
    val refreshToken: String
)

data class RegisterRequest (
    val username: String
)

data class LoginRequest (
    val username: String,
)

interface NoAuthService {

    @POST("/register")
    suspend fun register(@Body body: RegisterRequest): Response<SuccessResponse>

    @POST("/login")
    suspend fun login(@Body body: LoginRequest): Response<TokenResponse>
}

interface AuthService {

    @GET("/profile")
    suspend fun getProfile(): Response<SuccessResponse>
}