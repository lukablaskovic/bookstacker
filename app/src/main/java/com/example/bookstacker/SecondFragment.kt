package com.example.bookstacker

import BookService
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.example.bookstacker.database.BookDatabase
import com.example.bookstacker.database.BookEntity
import com.example.bookstacker.databinding.FragmentSecondBinding
import com.example.bookstacker.model.Book
import com.example.bookstacker.model.BookResponse
import com.example.bookstacker.model.ImageLinks
import com.example.bookstacker.model.VolumeInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class SecondFragment : Fragment() {

    private lateinit var db: BookDatabase
    private var _binding: FragmentSecondBinding? = null
    val books: MutableList<Book> = mutableListOf()
    private lateinit var adapter: MyItemGoogleBookRecyclerViewAdapter

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(

        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentSecondBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)

        db = Room.databaseBuilder(
            requireContext(),
            BookDatabase::class.java, "book-database"
        )
            .fallbackToDestructiveMigration()
            .build()

        val addNewBookButton: Button = view.findViewById(R.id.addNewBookButton)

        addNewBookButton.setOnClickListener {
            val titleEditText: EditText = view.findViewById(R.id.BookTitleEdit)
            val authorsEditText: EditText = view.findViewById(R.id.BookAuthorEdit)
            val publisherEditText: EditText = view.findViewById(R.id.BookPublisherEdit)
            val publishedDateEditText: EditText = view.findViewById(R.id.BookDateEdit)
            val pagesEditText: EditText = view.findViewById(R.id.BookPagesEdit)
            val descriptionEditText: EditText = view.findViewById(R.id.BookDescriptionEdit)
            val thumbnailEditText: EditText = view.findViewById(R.id.BookImageEdit)

            val title = titleEditText.text.toString()
            val authors = authorsEditText.text.toString()
            val publisher = publisherEditText.text.toString()
            val publishedDate = publishedDateEditText.text.toString()
            var pages = pagesEditText.text.toString().toIntOrNull()
            if (pages == null)
                pages = 0
            val description = descriptionEditText.text.toString()
            val thumbnail = thumbnailEditText.text.toString()

            val bookEntity = BookEntity(
                title = title,
                authors = authors,
                publisher = publisher,
                publishedDate = publishedDate,
                description = description,
                thumbnail = thumbnail,
                status = "UNREAD",
                pageCount = pages,
                pageRead = 0
            )
            //This actually saves books to database
            CoroutineScope(Dispatchers.IO).launch {
                db.bookDao().insert(bookEntity)

                // Now fetch all books from the database
                val storedBooks = db.bookDao().getAllBooks()

                // Print them out (on the main thread to avoid NetworkOnMainThreadException)
                withContext(Dispatchers.Main) {
                    storedBooks.forEach { book ->
                        Log.d("SecondFragment", book.toString())
                    }
                }
            }
        }
        val searchView: SearchView = view.findViewById(R.id.searchView)

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                // This method is called when the user submits the search query
                performSearch(query)
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                // This method is called when the search query text changes
                // You can perform incremental search or filtering here
                return true
            }
        })

        // Set an OnCloseListener to handle the search view close event
        searchView.setOnCloseListener {
            // Clear the search results or perform any necessary actions
            clearSearchResults()
            false
        }

        // Set an OnFocusChangeListener to handle the search view focus change event
        searchView.setOnQueryTextFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                // Clear the search results or perform any necessary actions
                clearSearchResults()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    private fun clearSearchResults() {
        val recyclerView: RecyclerView? = view?.findViewById(R.id.list)
        recyclerView?.visibility = View.GONE

        val inputsLayout: LinearLayout? = view?.findViewById(R.id.inputs)
        inputsLayout?.visibility = View.VISIBLE
    }

    private fun performSearch(query: String) {

        val recyclerView: RecyclerView? = view?.findViewById(R.id.list)
        recyclerView?.visibility = View.VISIBLE
        val inputsLayout: LinearLayout? = view?.findViewById(R.id.inputs)
        inputsLayout?.visibility = View.GONE

        val service = ServiceBuilder.buildService(BookService::class.java)
        val call = service.getBooks(query)

        call.enqueue(object : Callback<BookResponse> {
            override fun onResponse(
                call: Call<BookResponse>,
                response: Response<BookResponse>
            ) {
                if (response.isSuccessful) {
                    books.clear()
                    response.body()?.let { bookResponse ->
                        books.addAll(bookResponse.items) // Add the fetched books to your list

                        // Print each book's title, description and image links
                        for (book in books) {
                            println(book)
                        }
                    }
                    // Initialize the adapter with the data list
                    adapter = MyItemGoogleBookRecyclerViewAdapter(books)
                    // Set the adapter on the RecyclerView
                    recyclerView?.adapter = adapter
                }
            }

            override fun onFailure(call: Call<BookResponse>, t: Throwable) {
                println("Error happening!")
            }
        })
    }
}