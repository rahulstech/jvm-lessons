import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Query
import retrofit2.http.Url

data class UploadResponse(
    val filename: String
)

data class S3UrlResponse(
    val url: String
)

interface Service {

    @Multipart // use this annotation for multipart form-data request
    @POST("/single")
    fun uploadFile(
        // in case of file part I must not set the filed name as @Part value
        // it will throw error; but any other type of RequestBody I have to mention
        // the field name as @Part value
        @Part file: MultipartBody.Part
    ): Call<UploadResponse>

    // getS3PutObjectUrl and s3PutObject together required for uploading a file (object) to S3 bucket
    // getS3PutObjectUrl is sent by node-js app server. pre-signed url are valid for certain time and dedicated to a particular task
    // in this case only put an object into predefined s3 bucket.
    // s3PutObject send a put request to the pre-signed url with the file content. on successful it responses with 200 and no response body

    @GET("/s3-put-object-url")
    fun getS3PutObjectUrl(
        @Query("filename") filename: String,
        @Query("contentType") contentType: String
    ): Call<S3UrlResponse>

    @PUT
    fun s3PutObject(
        @Url url: String,
        @Body file: RequestBody // Note: it is not a multipart form-data request, therefore I am using RequestBody instead of MultipartBody Part
    ): Call<Void>
}

class Client {

    val service = Retrofit.Builder()
        .baseUrl("http://localhost:5000")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(Service::class.java)
}