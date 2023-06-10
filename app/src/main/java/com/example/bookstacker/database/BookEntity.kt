package com.example.bookstacker.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class BookEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val authors: String,
    val publisher: String,
    val publishedDate: String,
    val description: String,
    val pageRead: Int = 0,
    val status: String = "UNREAD",
    val pageCount: Int,
    val thumbnail: String,
    val ISBN: String,
    val mainCategory: String,
    val categories: String,
    val infoLink: String,
) {
    companion object {
        // Sort by title
        fun List<BookEntity>.sortByTitleAZ(): List<BookEntity> =
            sortedBy { it.title }
        fun List<BookEntity>.sortByTitleZA(): List<BookEntity> =
            sortedByDescending  { it.title }

    }
}
