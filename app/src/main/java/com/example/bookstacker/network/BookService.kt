import com.example.bookstacker.model.BookResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface BookService {
    @GET("books/v1/volumes")
    fun getBooks(@Query("q") query: String): Call<BookResponse>
}
