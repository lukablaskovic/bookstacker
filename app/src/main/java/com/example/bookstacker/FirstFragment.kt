package com.example.bookstacker

import android.os.Bundle
import android.text.Editable
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
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.EditText
import android.widget.ImageButton
import androidx.room.Delete
import com.example.bookstacker.databinding.FragmentFirstBinding
import com.example.bookstacker.model.Book
import com.example.bookstacker.model.ImageLinks
import com.example.bookstacker.model.IndustryIdentifier
import com.example.bookstacker.model.VolumeInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FirstFragment : Fragment(), MyListOfAddedBooksRecyclerViewAdapter.OnAddButtonClickListener  {

    private var _binding: FragmentFirstBinding? = null
    private lateinit var db: BookDatabase
    val books: MutableList<Book> = mutableListOf()
    var storedBooks:  List<BookEntity> = mutableListOf()
    var sortedByTitleAZ:  List<BookEntity> = mutableListOf()
    var sortedByTitleZA:  List<BookEntity> = mutableListOf()
    var openedBook:  BookEntity? = null

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
            adapter.onAddButtonClickListener = this@FirstFragment

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
            val backButton = requireActivity().findViewById<View>(R.id.back)
            val logo = requireActivity().findViewById<View>(R.id.logo)
            backButton.setOnClickListener {
                val addBookButton = requireActivity().findViewById<View>(R.id.addBook)
                val editBookButton = requireActivity().findViewById<View>(R.id.editBook)
                addBookButton.visibility = VISIBLE
                editBookButton.visibility = GONE
                binding.thirdFragment.visibility = GONE
                logo.visibility = VISIBLE
                EditBook()
            }
        }

        binding.addBook.setOnClickListener {
            val backButton = requireActivity().findViewById<View>(R.id.back)
            val logo = requireActivity().findViewById<View>(R.id.logo)
            logo.visibility = GONE
            backButton.visibility = VISIBLE
            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
        }
        binding.editBook.setOnClickListener {
            val backButton = requireActivity().findViewById<View>(R.id.back)
            val logo = requireActivity().findViewById<View>(R.id.logo)
            binding.editBook.visibility = GONE
            binding.bookInfo.visibility = GONE
            backButton.visibility = GONE
            logo.visibility = VISIBLE
            binding.bookInputsEdit.visibility = VISIBLE
            SetBookinputInfo();
        }
        binding.saveBookButton.setOnClickListener {
            val backButton = requireActivity().findViewById<View>(R.id.back)
            val logo = requireActivity().findViewById<View>(R.id.logo)
            logo.visibility = GONE
            binding.bookInfo.visibility = VISIBLE
            binding.bookInputsEdit.visibility = GONE
            backButton.visibility = VISIBLE
            SaveBook()
            EditBook()
        }
        binding.deleteBookButton.setOnClickListener {
            val backButton = requireActivity().findViewById<View>(R.id.back)
            val logo = requireActivity().findViewById<View>(R.id.logo)
            val addBookButton = requireActivity().findViewById<View>(R.id.addBook)
            val editBookButton = requireActivity().findViewById<View>(R.id.editBook)
            addBookButton.visibility = VISIBLE
            editBookButton.visibility = GONE
            binding.thirdFragment.visibility = GONE
            logo.visibility = VISIBLE
            backButton.visibility = GONE
            binding.bookInfo.visibility = VISIBLE
            binding.bookInputsEdit.visibility = GONE
            DeleteBook()
        }
        binding.sortAZ.setOnClickListener {       // Set the adapter on the RecyclerView
            adapter.setItems(sortedByTitleAZ.toMutableList())
        }
        binding.sortZA.setOnClickListener {    // Set the adapter on the RecyclerView
            adapter.setItems(sortedByTitleZA.toMutableList())
        }

        val unread: ImageButton? = view?.findViewById(R.id.unread)
        val reading: ImageButton? = view?.findViewById(R.id.reading)
        val read: ImageButton? = view?.findViewById(R.id.read)

        unread?.setOnClickListener {
            reading?.visibility = VISIBLE
            read?.visibility = GONE
            unread?.visibility = GONE
            openedBook?.status = "READING"
        }
        reading?.setOnClickListener {
            reading?.visibility = GONE
            read?.visibility = VISIBLE
            unread?.visibility = GONE
            openedBook?.status = "READ"
        }
        read?.setOnClickListener {
            reading?.visibility = GONE
            read?.visibility = GONE
            unread?.visibility = VISIBLE
            openedBook?.status = "UNREAD"
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
    fun SetBookinputInfo() {
        val titleText: TextView? = view?.findViewById(R.id.bookTitleEnter)
        val authorsText: TextView? = view?.findViewById(R.id.bookAuthorEnter)
        val publisherText: TextView? = view?.findViewById(R.id.publisher)
        val publishedDateText: TextView? = view?.findViewById(R.id.date)
        val pagesText: TextView? = view?.findViewById(R.id.pagesCount)
        val descriptionText: TextView? = view?.findViewById(R.id.description)
        val thumbnailText: TextView? = view?.findViewById(R.id.imageURL)
        val ISBNText: TextView? = view?.findViewById(R.id.ISBN)
        val categoriesText: TextView? = view?.findViewById(R.id.categories)
        val infoURLText: TextView? = view?.findViewById(R.id.infoURL)

        val title = titleText?.text.toString()
        val authors = authorsText?.text.toString()
        val publisher = publisherText?.text.toString()
        val publishedDate = publishedDateText?.text.toString()
        var pages = pagesText?.text.toString().toIntOrNull()
        if (pages == null)
            pages = 0
        val description = descriptionText?.text.toString()
        val thumbnail = thumbnailText?.text.toString()
        val ISBN = ISBNText?.text.toString()
        val categories = categoriesText?.text.toString()
        val infoURL = infoURLText?.text.toString()

        val titleEditText: TextView? = view?.findViewById(R.id.BookTitleEdit)
        val authorsEditText: TextView? = view?.findViewById(R.id.BookAuthorEdit)
        val publisherEditText: TextView? = view?.findViewById(R.id.BookPublisherEdit)
        val publishedDateEditText: TextView? = view?.findViewById(R.id.BookDateEdit)
        val pagesEditText: TextView? = view?.findViewById(R.id.BookPagesEdit)
        val descriptionEditText: TextView? = view?.findViewById(R.id.BookDescriptionEdit)
        val thumbnailEditText: TextView? = view?.findViewById(R.id.BookImageEdit)
        val iSBNEditText: TextView? = view?.findViewById(R.id.BookISBNEdit)
        val categoriesEditText: TextView? = view?.findViewById(R.id.BookCategoriesEdit)
        val infoURLEditText: TextView? = view?.findViewById(R.id.BookInfoURLEdit)

        titleEditText?.text = title.let { Editable.Factory.getInstance().newEditable(it) }
        authorsEditText?.text = authors?.let { Editable.Factory.getInstance().newEditable(it) }
        publisherEditText?.text = publisher?.let { Editable.Factory.getInstance().newEditable(it) }
        publishedDateEditText?.text = publishedDate?.let { Editable.Factory.getInstance().newEditable(it) }
        pagesEditText?.text = pages?.let { Editable.Factory.getInstance().newEditable(it.toString()) }
        descriptionEditText?.text = description?.let { Editable.Factory.getInstance().newEditable(it) }
        thumbnailEditText?.text = thumbnail?.let { Editable.Factory.getInstance().newEditable(it) }
        categoriesEditText?.text = categories?.let { Editable.Factory.getInstance().newEditable(it) }
        infoURLEditText?.text = infoURL?.let { Editable.Factory.getInstance().newEditable(it) }
        iSBNEditText?.text = ISBN?.let { Editable.Factory.getInstance().newEditable(it) }
    }
    fun EditBook() {
        val pagesReadEditText: EditText? = view?.findViewById(R.id.pagesRead)
        val titleEditText: TextView? = view?.findViewById(R.id.bookTitleEnter)
        val authorsEditText: TextView? = view?.findViewById(R.id.bookAuthorEnter)
        val publisherEditText: TextView? = view?.findViewById(R.id.publisher)
        val publishedDateEditText: TextView? = view?.findViewById(R.id.date)
        val pagesEditText: TextView? = view?.findViewById(R.id.pagesCount)
        val descriptionEditText: TextView? = view?.findViewById(R.id.description)
        val thumbnailEditText: TextView? = view?.findViewById(R.id.imageURL)
        val iSBNEditText: TextView? = view?.findViewById(R.id.ISBN)
        val categoriesEditText: TextView? = view?.findViewById(R.id.categories)
        val infoURLEditText: TextView? = view?.findViewById(R.id.infoURL)

        val pagesRead = pagesReadEditText?.text.toString().toIntOrNull()
        if (pagesRead != null) {
            openedBook?.pageRead = pagesRead
        }

        val title = titleEditText?.text?.toString()
        if (!title.isNullOrEmpty()) {
            openedBook?.title = title
        }

        val authors = authorsEditText?.text?.toString()
        if (!authors.isNullOrEmpty()) {
            openedBook?.authors = authors
        }

        val publisher = publisherEditText?.text?.toString()
        if (!publisher.isNullOrEmpty()) {
            openedBook?.publisher = publisher
        }

        val publishedDate = publishedDateEditText?.text?.toString()
        if (!publishedDate.isNullOrEmpty()) {
            openedBook?.publishedDate = publishedDate
        }

        val pagesCount = pagesEditText?.text?.toString()?.toIntOrNull()
        if (pagesCount != null) {
            openedBook?.pageCount = pagesCount
        }

        val description = descriptionEditText?.text?.toString()
        if (!description.isNullOrEmpty()) {
            openedBook?.description = description
        }

        val thumbnail = thumbnailEditText?.text?.toString()
        if (!thumbnail.isNullOrEmpty()) {
            openedBook?.thumbnail = thumbnail
        }

        val isbn = iSBNEditText?.text?.toString()
        if (!isbn.isNullOrEmpty()) {
            openedBook?.ISBN = isbn
        }

        val categories = categoriesEditText?.text?.toString()
        if (!categories.isNullOrEmpty()) {
            openedBook?.categories = categories
        }

        val infoURL = infoURLEditText?.text?.toString()
        if (!infoURL.isNullOrEmpty()) {
            openedBook?.infoLink = infoURL
        }

        CoroutineScope(Dispatchers.IO).launch {
            openedBook?.let {
                db.bookDao().updateBook(
                    it.id,
                    it.title,
                    it.authors,
                    it.publisher,
                    it.publishedDate,
                    it.description,
                    it.pageRead,
                    it.status,
                    it.pageCount,
                    it.thumbnail,
                    it.ISBN,
                    it.mainCategory,
                    it.categories,
                    it.infoLink
                )
            }
            storedBooks = db.bookDao().getAllBooks()
            sortedByTitleAZ = storedBooks.sortByTitleAZ()
            sortedByTitleZA = storedBooks.sortByTitleZA()

            withContext(Dispatchers.Main) {
                adapter.setItems(storedBooks.toMutableList())
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onAddButtonClicked(item: BookEntity) {
        binding.thirdFragment.visibility = VISIBLE
        val backButton = requireActivity().findViewById<View>(R.id.back)
        val logo = requireActivity().findViewById<View>(R.id.logo)
        logo.visibility = GONE
        backButton.visibility = VISIBLE

        val addBookButton = requireActivity().findViewById<View>(R.id.addBook)
        val editBookButton = requireActivity().findViewById<View>(R.id.editBook)
        addBookButton.visibility = GONE
        editBookButton.visibility = VISIBLE

        openedBook = item;

        val pagesReadEditText: EditText? = view?.findViewById(R.id.pagesRead)
        val titleEditText: TextView? = view?.findViewById(R.id.bookTitleEnter)
        val authorsEditText: TextView? = view?.findViewById(R.id.bookAuthorEnter)
        val publisherEditText: TextView? = view?.findViewById(R.id.publisher)
        val publishedDateEditText: TextView? = view?.findViewById(R.id.date)
        val pagesEditText: TextView? = view?.findViewById(R.id.pagesCount)
        val descriptionEditText: TextView? = view?.findViewById(R.id.description)
        val thumbnailEditText: TextView? = view?.findViewById(R.id.imageURL)
        val ISBNEditText: TextView? = view?.findViewById(R.id.ISBN)
        val categoriesEditText: TextView? = view?.findViewById(R.id.categories)
        val infoURLEditText: TextView? = view?.findViewById(R.id.infoURL)


        val unread: ImageButton? = view?.findViewById(R.id.unread)
        val reading: ImageButton? = view?.findViewById(R.id.reading)
        val read: ImageButton? = view?.findViewById(R.id.read)

        pagesReadEditText?.text = item.pageRead?.let { Editable.Factory.getInstance().newEditable(it.toString()) }
        titleEditText?.text = item.title?.let { Editable.Factory.getInstance().newEditable(it) }
        authorsEditText?.text = item.authors?.let { Editable.Factory.getInstance().newEditable(it) }
        publisherEditText?.text = item.publisher?.let { Editable.Factory.getInstance().newEditable(it) }
        publishedDateEditText?.text = item.publishedDate?.let { Editable.Factory.getInstance().newEditable(it) }
        pagesEditText?.text = item.pageCount?.let { Editable.Factory.getInstance().newEditable(it.toString()) }
        descriptionEditText?.text = item.description?.let { Editable.Factory.getInstance().newEditable(it) }
        thumbnailEditText?.text = item.thumbnail?.let { Editable.Factory.getInstance().newEditable(it) }
        categoriesEditText?.text = item.categories?.let { Editable.Factory.getInstance().newEditable(it) }
        infoURLEditText?.text = item.infoLink?.let { Editable.Factory.getInstance().newEditable(it) }
        ISBNEditText?.text = item.ISBN?.let { Editable.Factory.getInstance().newEditable(it) }

        when (item.status) {
            "READING" -> {
                reading?.visibility = VISIBLE
                read?.visibility = GONE
                unread?.visibility = GONE
            }
            "UNREAD" -> {
                reading?.visibility = GONE
                read?.visibility = GONE
                unread?.visibility = VISIBLE
            }
            "READ" -> {
                reading?.visibility = GONE
                read?.visibility = VISIBLE
                unread?.visibility = GONE
            }
            else -> {
                reading?.visibility = GONE
                read?.visibility = GONE
                unread?.visibility = GONE
            }
        }
    }

    fun SaveBook() {
        val titleText: TextView? = view?.findViewById(R.id.bookTitleEnter)
        val authorsText: TextView? = view?.findViewById(R.id.bookAuthorEnter)
        val publisherText: TextView? = view?.findViewById(R.id.publisher)
        val publishedDateText: TextView? = view?.findViewById(R.id.date)
        val pagesText: TextView? = view?.findViewById(R.id.pagesCount)
        val descriptionText: TextView? = view?.findViewById(R.id.description)
        val thumbnailText: TextView? = view?.findViewById(R.id.imageURL)
        val ISBNText: TextView? = view?.findViewById(R.id.ISBN)
        val categoriesText: TextView? = view?.findViewById(R.id.categories)
        val infoURLText: TextView? = view?.findViewById(R.id.infoURL)

        val titleEditText: TextView? = view?.findViewById(R.id.BookTitleEdit)
        val authorsEditText: TextView? = view?.findViewById(R.id.BookAuthorEdit)
        val publisherEditText: TextView? = view?.findViewById(R.id.BookPublisherEdit)
        val publishedDateEditText: TextView? = view?.findViewById(R.id.BookDateEdit)
        val pagesEditText: TextView? = view?.findViewById(R.id.BookPagesEdit)
        val descriptionEditText: TextView? = view?.findViewById(R.id.BookDescriptionEdit)
        val thumbnailEditText: TextView? = view?.findViewById(R.id.BookImageEdit)
        val iSBNEditText: TextView? = view?.findViewById(R.id.BookISBNEdit)
        val categoriesEditText: TextView? = view?.findViewById(R.id.BookCategoriesEdit)
        val infoURLEditText: TextView? = view?.findViewById(R.id.BookInfoURLEdit)

        val title = titleEditText?.text.toString()
        val authors = authorsEditText?.text.toString()
        val publisher = publisherEditText?.text.toString()
        val publishedDate = publishedDateEditText?.text.toString()
        var pages = pagesEditText?.text.toString().toIntOrNull()
        if (pages == null)
            pages = 0
        val description = descriptionEditText?.text.toString()
        val thumbnail = thumbnailEditText?.text.toString()
        val ISBN = iSBNEditText?.text.toString()
        val categories = categoriesEditText?.text.toString()
        val infoURL = infoURLEditText?.text.toString()


        titleText?.text = title.let { Editable.Factory.getInstance().newEditable(it) }
        authorsText?.text = authors?.let { Editable.Factory.getInstance().newEditable(it) }
        publisherText?.text = publisher?.let { Editable.Factory.getInstance().newEditable(it) }
        publishedDateText?.text = publishedDate?.let { Editable.Factory.getInstance().newEditable(it) }
        pagesText?.text = pages?.let { Editable.Factory.getInstance().newEditable(it.toString()) }
        descriptionText?.text = description?.let { Editable.Factory.getInstance().newEditable(it) }
        thumbnailText?.text = thumbnail?.let { Editable.Factory.getInstance().newEditable(it) }
        categoriesText?.text = categories?.let { Editable.Factory.getInstance().newEditable(it) }
        infoURLText?.text = infoURL?.let { Editable.Factory.getInstance().newEditable(it) }
        ISBNText?.text = ISBN?.let { Editable.Factory.getInstance().newEditable(it) }
    }
    fun DeleteBook() {
        CoroutineScope(Dispatchers.IO).launch {
            openedBook?.let {
                db.bookDao().deleteById(
                    it.id
                )
            }
            storedBooks = db.bookDao().getAllBooks()
            sortedByTitleAZ = storedBooks.sortByTitleAZ()
            sortedByTitleZA = storedBooks.sortByTitleZA()

            withContext(Dispatchers.Main) {
                adapter.setItems(storedBooks.toMutableList())
            }
        }
    }
}