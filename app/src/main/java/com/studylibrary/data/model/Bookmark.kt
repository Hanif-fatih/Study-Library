package com.studylibrary.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

/**
 * Bookmark model representing a bookmark in a book
 */
@Entity(
    tableName = "bookmarks",
    foreignKeys = [
        ForeignKey(
            entity = Book::class,
            parentColumns = ["id"],
            childColumns = ["bookId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Bookmark(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val bookId: String,
    val pageNumber: Int,
    val chapterTitle: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
