package com.example.bookstacker.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class BookEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    var title: String,
    var authors: String,
    var publisher: String,
    var publishedDate: String,
    var description: String,
    var pageRead: Int = 0,
    var status: String = "UNREAD",
    var pageCount: Int,
    var thumbnail: String,
    var ISBN: String,
    var mainCategory: String,
    var categories: String,
    var infoLink: String,
) {
    companion object {
        // Sort by title
        fun List<BookEntity>.sortByTitleAZ(): List<BookEntity> =
            sortedBy { it.title }
        fun List<BookEntity>.sortByTitleZA(): List<BookEntity> =
            sortedByDescending  { it.title }

    }
}
