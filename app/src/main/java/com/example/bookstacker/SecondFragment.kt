package com.example.bookstacker

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.room.Room
import com.example.bookstacker.database.BookDatabase
import com.example.bookstacker.database.BookEntity
import com.example.bookstacker.databinding.FragmentSecondBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class SecondFragment : Fragment() {

    private lateinit var db: BookDatabase
    private var _binding: FragmentSecondBinding? = null

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
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}