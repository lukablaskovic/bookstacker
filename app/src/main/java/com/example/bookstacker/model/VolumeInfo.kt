package com.example.bookstacker.model

data class VolumeInfo(
    val title: String,
    val authors: List<String>,
    val publisher: String,
    val publishedDate: String,
    val description: String,
    val imageLinks: ImageLinks
)

// Reference https://developers.google.com/books/docs/v1/using