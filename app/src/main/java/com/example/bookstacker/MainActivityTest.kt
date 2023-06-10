package com.example.bookstacker

import BookService
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.bookstacker.database.BookDatabase
import com.example.bookstacker.model.Book
import com.example.bookstacker.model.BookResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivityTest : AppCompatActivity() {

    val books: MutableList<Book> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

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
                            println(book)
                            //println("Title: ${book.volumeInfo.title}")
                            //println("Description: ${book.volumeInfo.description}")
                            //println("Thumbnail: ${book.volumeInfo.imageLinks?.thumbnail ?: "No thumbnail available"}")
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