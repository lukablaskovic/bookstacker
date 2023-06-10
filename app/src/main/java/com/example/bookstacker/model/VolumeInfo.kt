package com.example.bookstacker.model

data class VolumeInfo(
    val title: String,
    val authors: List<String>,
    val publisher: String,
    val publishedDate: String,
    val description: String,
    val imageLinks: ImageLinks,
    val industryIdentifiers: List<IndustryIdentifier>,
    val pageCount: Int,
    val mainCategory: String,
    val categories: List<String>,
    val infoLink: String
)

// Reference https://developers.google.com/books/docs/v1/using