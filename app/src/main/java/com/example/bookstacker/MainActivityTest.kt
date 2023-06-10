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
            BookDatabase::class.java, "fetched_books"
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
                        books.addAll(bookResponse.items) // Add the fetched books to your list

                        // Print each book's title, description and image links
                        for (book in books) {
                            println("Title: ${book.volumeInfo.title}")
                            println("Description: ${book.volumeInfo.description}")
                            println("Thumbnail: ${book.volumeInfo.imageLinks?.thumbnail ?: "No thumbnail available"}")
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