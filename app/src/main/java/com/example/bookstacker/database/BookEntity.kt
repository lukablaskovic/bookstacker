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
    val thumbnail: String
)
