import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// Main Connector
object ServiceBuilder {
    private const val BASE_URL = "https://www.googleapis.com/"

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    fun <T> buildService(service: Class<T>): T {
        return retrofit.create(service)
    }
}
