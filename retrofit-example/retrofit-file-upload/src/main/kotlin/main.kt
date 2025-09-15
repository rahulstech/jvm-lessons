import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

// create File object of the upload file
val file = File("/home/acer/Desktop/svg icons/sleet.png")

// upload single file to nodejs server
fun uploadSingleFile(service: Service) {

    // create a RequestBody object with file content-type and file object. to create RequestBody object from input stream,
    // I can extend RequestBody and implement writeTo(BufferSink) method to write the input stream content to BufferSink.
    val requestFile = RequestBody.create(MediaType.parse("image/png"), File("/home/acer/Desktop/svg icons/body.png"))

    // create MultipartBody Part from RequestBody. MultipartBody is a subclass of RequestBody.
    // MultipartBody Part is responsible for setting proper Content-Disposition header
    // with filename which is not handled by RequestBody itself. therefore in multipart request
    // to handle file upload I need to wrap RequestBody into a MultipartBody Part.
    val filePart = MultipartBody.Part.createFormData("file", file.name, requestFile)

    service.uploadFile(filePart).enqueue(object : Callback<UploadResponse>{
        override fun onResponse(c: Call<UploadResponse>, res: Response<UploadResponse>) {
            if (res.isSuccessful) {
                println("upload successful with response ${res.body()?.filename}")
            }
            else {
                println("upload fail with error ${res.errorBody()}")
            }
        }

        override fun onFailure(c: Call<UploadResponse>, t: Throwable) {
            t.printStackTrace()
        }
    })
}

fun putObjectInS3(service: Service, url: String, file: File, contentType: String) {
    // upload to s3
    val body = RequestBody.create(MediaType.parse(contentType),file);
    service.s3PutObject(url, body)
        .enqueue(object : Callback<Void>{
            override fun onResponse(
                call: Call<Void>,
                response: Response<Void>
            ) {
                if (response.isSuccessful) {
                    println("file uploaded to s3")
                }
                else {
                    println("file not uploaded to s3")
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                t.printStackTrace()
            }
        })
}

// upload single file to S3
fun uploadSingleFileToS3(service: Service) {
    val contentType = "image/png"

    // get the s3 pre-signed url
    service.getS3PutObjectUrl(file.name, contentType)
        .enqueue(object : Callback<S3UrlResponse>{
            override fun onResponse(call: Call<S3UrlResponse>, res: Response<S3UrlResponse>) {
                if (res.isSuccessful) {
                    // get the pre-signed url
                    val url = (res.body() as S3UrlResponse).url
                    println("get s3 pre-signed url $url")

                    // init upload
                    putObjectInS3(service,url,file,contentType)
                }else {
                    println("get s3 put object url failed ${res.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<S3UrlResponse>, t: Throwable) {
                t.printStackTrace()
            }
        })
}

fun main(args: Array<String>) {

    val client = Client()
    val service = client.service

//    uploadSingleFile(service)

    uploadSingleFileToS3(service)
}