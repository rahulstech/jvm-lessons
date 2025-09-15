import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

fun  main(args: Array<String>) {
    val client = Retrofit.Builder()
        .baseUrl("http://localhost:3000")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(ChatClient::class.java)

    client.setPushToken("1", mapOf( "pushToken" to "my-push-token"))
        .enqueue(object: Callback<ServerResponse> {
            override fun onResponse(p0: Call<ServerResponse>, res: Response<ServerResponse>) {
                if (res.isSuccessful) {
                    println("setPushToken successful: message = ${res.body()?.message}")
                }
                else {
                    println("setPushToken fail: error = ${res.errorBody()?.string()}")
                }
            }

            override fun onFailure(p0: Call<ServerResponse>, t: Throwable) {
                t.printStackTrace()
            }
        })
}