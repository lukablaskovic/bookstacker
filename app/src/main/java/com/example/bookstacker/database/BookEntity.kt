package com.example.bookstacker.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class BookEntity(
    @PrimaryKey val id: String,
    val title: String,
    val authors: String,  // convert the list to string before storing
    val publisher: String,
    val publishedDate: String,
    val description: String,
    val thumbnail: String  // just storing the thumbnail url
)