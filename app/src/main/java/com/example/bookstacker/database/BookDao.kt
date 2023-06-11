package com.example.bookstacker.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface BookDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(books: List<BookEntity>)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(book: BookEntity )

    @Query("SELECT * FROM bookentity")
    suspend fun getAllBooks(): List<BookEntity>

    @Query("UPDATE bookentity SET title = :title, authors = :authors, publisher = :publisher, publishedDate = :publishedDate, description = :description, pageRead = :pageRead, status = :status, pageCount = :pageCount, thumbnail = :thumbnail, ISBN = :isbn, mainCategory = :mainCategory, categories = :categories, infoLink = :infoLink WHERE id = :bookId")
    suspend fun updateBook(
        bookId: Long,
        title: String,
        authors: String,
        publisher: String,
        publishedDate: String,
        description: String,
        pageRead: Int,
        status: String,
        pageCount: Int,
        thumbnail: String,
        isbn: String,
        mainCategory: String,
        categories: String,
        infoLink: String
    )

    @Query("DELETE FROM bookentity WHERE id = :bookId")
    suspend fun deleteById(bookId: Long)
}
