package com.example.bookstacker

import BookService
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import com.example.bookstacker.database.BookDatabase
import com.example.bookstacker.database.BookEntity
import com.example.bookstacker.model.Book
import com.example.bookstacker.model.BookResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivityTest : AppCompatActivity() {

    private lateinit var db: BookDatabase

    val books: MutableList<Book> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        db = Room.databaseBuilder(
            applicationContext,
            BookDatabase::class.java, "book-database"
        ).build()

        val service = ServiceBuilder.buildService(BookService::class.java)
        val call = service.getBooks("Lord of the rings")

        call.enqueue(object : Callback<BookResponse> {
            override fun onResponse(
                call: Call<BookResponse>,
                response: Response<BookResponse>
            ) {
                if (response.isSuccessful) {
                    response.body()?.let { bookResponse ->
                        // Map the response items to BookEntity and save them to the database
                        val bookEntities = bookResponse.items.map { book ->
                            BookEntity(
                                id = book.id,
                                title = book.volumeInfo.title ?: "No title",
                                authors = book.volumeInfo.authors.joinToString(),
                                publisher = book.volumeInfo.publisher ?: "No publisher",
                                publishedDate = book.volumeInfo.publishedDate ?: "No published date",
                                description = book.volumeInfo.description ?: "No description",
                                thumbnail = book.volumeInfo.imageLinks?.thumbnail ?: ""
                            )
                        }
                        //This actually saves books to database
                        CoroutineScope(Dispatchers.IO).launch {
                            db.bookDao().insertAll(bookEntities)

                            // Now fetch all books from the database
                            val storedBooks = db.bookDao().getAllBooks()

                            // Print them out (on the main thread to avoid NetworkOnMainThreadException)
                            withContext(Dispatchers.Main) {
                                storedBooks.forEach { book ->
                                    Log.d("MainActivity", book.toString())
                                }
                            }
                        }
                    }
                }
            }

            override fun onFailure(call: Call<BookResponse>, t: Throwable) {
                println("Error happening!")
            }
        })
    }
}