package com.example.bookstacker

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import coil.load

import com.example.bookstacker.databinding.GoogleBookFragmentBinding
import com.example.bookstacker.model.Book

class MyItemGoogleBookRecyclerViewAdapter(
    private val values: MutableList<Book>
) : RecyclerView.Adapter<MyItemGoogleBookRecyclerViewAdapter.ViewHolder>() {
    interface OnAddButtonClickListener {
        fun onAddButtonClicked(item: Book)
    }
    var onAddButtonClickListener: OnAddButtonClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            GoogleBookFragmentBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position]
        if (item.volumeInfo.title != null) {
            holder.titleView.text = item.volumeInfo.title
        }
        if (item.volumeInfo.authors != null) {
            holder.authorView.text = item.volumeInfo.authors.joinToString(", ")
        }
        if (item.volumeInfo.publishedDate != null) {
            holder.yearView.text = item.volumeInfo.publishedDate
        }
        if (item.volumeInfo.imageLinks != null) {
            val imageUrl = item.volumeInfo.imageLinks.thumbnail
            holder.bookCoverView.load(imageUrl)
        }
        holder.addButtonView.setOnClickListener {
            onAddButtonClickListener?.onAddButtonClicked(item)
        }
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(binding: GoogleBookFragmentBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val titleView: TextView = binding.title
        val authorView: TextView = binding.author
        val yearView: TextView = binding.year
        val bookCoverView: ImageView = binding.bookCover
        val addButtonView: ImageButton = binding.addNewGoogleBook
        override fun toString(): String {
            return super.toString() + " '" + titleView.text + "'"
        }
    }
}