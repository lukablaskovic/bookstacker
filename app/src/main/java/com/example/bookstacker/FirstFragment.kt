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
import com.example.bookstacker.database.BookEntity.Companion.sortByTitleAZ
import com.example.bookstacker.database.BookEntity.Companion.sortByTitleZA
import com.example.bookstacker.databinding.FragmentFirstBinding
import com.example.bookstacker.model.Book
import com.example.bookstacker.model.ImageLinks
import com.example.bookstacker.model.IndustryIdentifier
import com.example.bookstacker.model.VolumeInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FirstFragment : Fragment() {

    private var _binding: FragmentFirstBinding? = null
    private lateinit var db: BookDatabase
    val books: MutableList<Book> = mutableListOf()
    var storedBooks:  List<BookEntity> = mutableListOf()
    var sortedByTitleAZ:  List<BookEntity> = mutableListOf()
    var sortedByTitleZA:  List<BookEntity> = mutableListOf()

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
            storedBooks = db.bookDao().getAllBooks()

            sortedByTitleAZ = storedBooks.sortByTitleAZ()
            sortedByTitleZA = storedBooks.sortByTitleZA()

            // Get a reference to the RecyclerView
            val recyclerView: RecyclerView = view.findViewById(R.id.list)
            adapter = MyListOfAddedBooksRecyclerViewAdapter(storedBooks.toMutableList())        // Set the adapter on the RecyclerView
            recyclerView.adapter = adapter

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

        binding.addBook.setOnClickListener {
            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
        }
        binding.sortAZ.setOnClickListener {
            val recyclerView: RecyclerView = view.findViewById(R.id.list)
            adapter = MyListOfAddedBooksRecyclerViewAdapter(sortedByTitleAZ.toMutableList())        // Set the adapter on the RecyclerView
            recyclerView.adapter = adapter
        }
        binding.sortZA.setOnClickListener {
            val recyclerView: RecyclerView = view.findViewById(R.id.list)
            adapter = MyListOfAddedBooksRecyclerViewAdapter(sortedByTitleZA.toMutableList())        // Set the adapter on the RecyclerView
            recyclerView.adapter = adapter
        }
    }

    fun convertBookEntityListToBookCollection(bookEntityList: List<BookEntity>): Collection<Book> {
        return bookEntityList.map { bookEntity ->
            Book(
                id = bookEntity.id.toString(),
                volumeInfo = VolumeInfo( bookEntity.title,  listOf(bookEntity.authors),
                    bookEntity.publisher,  bookEntity.publishedDate,  bookEntity.description,
                    ImageLinks("",bookEntity.thumbnail), listOf(IndustryIdentifier("ISBN_13", bookEntity.ISBN)),
                    bookEntity.pageCount, bookEntity.mainCategory, listOf(bookEntity.categories), bookEntity.infoLink)
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}