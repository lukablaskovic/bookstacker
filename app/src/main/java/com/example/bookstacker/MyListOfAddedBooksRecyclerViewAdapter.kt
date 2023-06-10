package com.example.bookstacker

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import com.example.bookstacker.placeholder.PlaceholderContent.PlaceholderItem
import com.example.bookstacker.databinding.FragmentListOfAddedBooksBinding

/**
 * [RecyclerView.Adapter] that can display a [PlaceholderItem].
 * TODO: Replace the implementation with code for your data type.
 */
class MyListOfAddedBooksRecyclerViewAdapter(
    private val values: List<PlaceholderItem>
) : RecyclerView.Adapter<MyListOfAddedBooksRecyclerViewAdapter.ViewHolder>() {

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
        holder.titleView.text = item.title
        holder.authorView.text = item.author
        holder.yearView.text = item.year
        holder.pagesView.text = item.numberOfPages.toString()
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(binding: FragmentListOfAddedBooksBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val titleView: TextView = binding.title
        val authorView: TextView = binding.author
        val yearView: TextView = binding.year
        val pagesView: TextView = binding.pages

        override fun toString(): String {
            return super.toString() + " '" + titleView.text + "'"
        }
    }

}