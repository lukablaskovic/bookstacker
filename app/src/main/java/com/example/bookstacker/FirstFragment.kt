package com.example.bookstacker

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.example.bookstacker.database.BookDatabase
import com.example.bookstacker.database.BookEntity
import com.example.bookstacker.databinding.FragmentFirstBinding
import com.example.bookstacker.model.Book
import com.example.bookstacker.model.ImageLinks
import com.example.bookstacker.model.VolumeInfo
import com.example.bookstacker.placeholder.PlaceholderContent
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment() {

    private var _binding: FragmentFirstBinding? = null
    private lateinit var db: BookDatabase
    val books: MutableList<Book> = mutableListOf()

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var adapter: MyListOfAddedBooksRecyclerViewAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentFirstBinding.inflate(inflater, container, false)
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
        books.clear();
        CoroutineScope(Dispatchers.IO).launch {
            // Now fetch all books from the database
            val storedBooks = db.bookDao().getAllBooks()
            books.addAll(convertBookEntityListToBookCollection(storedBooks));
            val totalBooksTextView: TextView = view.findViewById(R.id.totalBooks)
            val totalBooks = books.size
            totalBooksTextView.text = "$totalBooks"

            // Print them out (on the main thread to avoid NetworkOnMainThreadException)
            withContext(Dispatchers.Main) {
                storedBooks.forEach { book ->
                    Log.d("FirstFragment", book.toString())
                }
            }
        }

        // Create your data list here or retrieve it from a source

        // Initialize the adapter with the data list
        adapter = MyListOfAddedBooksRecyclerViewAdapter(books)

        // Get a reference to the RecyclerView
        val recyclerView: RecyclerView = view.findViewById(R.id.list)

        // Set the adapter on the RecyclerView
        recyclerView.adapter = adapter

        // Get a reference to the "Add Book" button
        val addBookButton: FloatingActionButton = view.findViewById(R.id.addBook)

        binding.addBook.setOnClickListener {
            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
        }
    }

    fun convertBookEntityListToBookCollection(bookEntityList: List<BookEntity>): Collection<Book> {
        return bookEntityList.map { bookEntity ->
            Book(
                id = bookEntity.id.toString(),
                volumeInfo = VolumeInfo( bookEntity.title,  listOf(bookEntity.authors),  bookEntity.publisher,  bookEntity.publishedDate,  bookEntity.description, ImageLinks("",bookEntity.thumbnail), bookEntity.pageCount)
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}