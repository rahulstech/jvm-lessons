import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

data class ServerResponse(
    val message: String
) {
    override fun toString(): String {
        return "ServerResponse(message=$message)"
    }
}

data class ChatMessage(
    val contentType: String,
    val content: String
)

interface ChatClient {

    @PATCH("/users/{userId}/pushToken")
    fun setPushToken(
        @Path("userId") userId: String,
        @Body pushToken: Map<String, String>
    ): Call<ServerResponse>

    @POST("/users/{userId}/messages")
    fun sendMessage(@Path("userId") userId: String, message: ChatMessage): Call<ServerResponse>
}