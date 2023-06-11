package com.example.bookstacker

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import coil.load
import com.example.bookstacker.database.BookEntity
import com.example.bookstacker.databinding.FragmentListOfAddedBooksBinding

class MyListOfAddedBooksRecyclerViewAdapter(
    private val values: MutableList<BookEntity>,
) : RecyclerView.Adapter<MyListOfAddedBooksRecyclerViewAdapter.ViewHolder>() {

    interface OnAddButtonClickListener {
        fun onAddButtonClicked(item: BookEntity)
    }
    var onAddButtonClickListener: OnAddButtonClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return ViewHolder(
            FragmentListOfAddedBooksBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position]

        if (item.title != null) {
            holder.titleView.text = item.title
        }
        if (item.authors != null) {
            holder.authorView.text = item.authors
        }
        if (item.pageCount != null) {
            holder.pagesView.text = " Pages: " + item.pageCount.toString()
        }
        if (item.publishedDate != null) {
            if (item.publishedDate.length >= 4) {
                val year = item.publishedDate.substring(0, 4)
                holder.yearView.text = year
            } else {
                holder.yearView.text = ""
            }
        }
        if (item.thumbnail != null) {
            if (item.thumbnail != "") {
                val imageUrl = item.thumbnail
                holder.bookCoverView.load(imageUrl)
            }
            else {
                holder.bookCoverView.load("https://www.forewordreviews.com/books/covers/not-for-profit.jpg")
            }
        } else {
            holder.bookCoverView.load("https://www.forewordreviews.com/books/covers/not-for-profit.jpg")
        }
        when (item.status) {
            "READING" -> {
                holder.yellow.visibility = VISIBLE
                holder.red.visibility = INVISIBLE
                holder.green.visibility = INVISIBLE
            }
            "UNREAD" -> {
                holder.yellow.visibility = INVISIBLE
                holder.red.visibility = VISIBLE
                holder.green.visibility = INVISIBLE
            }
            "READ" -> {
                holder.yellow.visibility = INVISIBLE
                holder.red.visibility = INVISIBLE
                holder.green.visibility = VISIBLE
            }
            else -> {
                holder.yellow.visibility = INVISIBLE
                holder.red.visibility = INVISIBLE
                holder.green.visibility = INVISIBLE
            }
        }

        holder.itemView.setOnClickListener {
            onAddButtonClickListener?.onAddButtonClicked(item)
        }
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(binding: FragmentListOfAddedBooksBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val titleView: TextView = binding.title
        val authorView: TextView = binding.author
        val yearView: TextView = binding.year
        val pagesView: TextView = binding.pages
        val bookCoverView: ImageView = binding.bookCover
        val yellow: ImageView = binding.bookmarkYellow
        val red: ImageView = binding.bookmarkRed
        val green: ImageView = binding.bookmarkGreen

        override fun toString(): String {
            return super.toString() + " '" + titleView.text + "'"
        }
    }

    fun setItems(items: List<BookEntity>) {
        values.clear()
        values.addAll(items)
        notifyDataSetChanged()
    }
}