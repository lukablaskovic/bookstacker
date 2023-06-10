package com.example.bookstacker

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.bookstacker.databinding.FragmentFirstBinding
import com.example.bookstacker.placeholder.PlaceholderContent
import com.google.android.material.floatingactionbutton.FloatingActionButton

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment() {

    private var _binding: FragmentFirstBinding? = null

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

        // Create your data list here or retrieve it from a source
        val myDataList = PlaceholderContent.ITEMS

        // Initialize the adapter with the data list
        adapter = MyListOfAddedBooksRecyclerViewAdapter(myDataList)

        // Get a reference to the RecyclerView
        val recyclerView: RecyclerView = view.findViewById(R.id.list)

        // Set the adapter on the RecyclerView
        recyclerView.adapter = adapter

        // Update the total books count TextView
        val totalBooksTextView: TextView = view.findViewById(R.id.totalBooks)
        val totalBooks = adapter.itemCount
        totalBooksTextView.text = "$totalBooks"

        // Get a reference to the "Add Book" button
        val addBookButton: FloatingActionButton = view.findViewById(R.id.addBook)

        binding.addBook.setOnClickListener {
            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}