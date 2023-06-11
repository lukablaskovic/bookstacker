package com.example.bookstacker.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [BookEntity::class], version = 7)
abstract class BookDatabase : RoomDatabase() {
    abstract fun bookDao(): BookDao
}