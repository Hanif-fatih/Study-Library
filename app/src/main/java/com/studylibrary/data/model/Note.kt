package com.studylibrary.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

/**
 * Note model representing a note attached to text in a book
 */
@Entity(
    tableName = "notes",
    foreignKeys = [
        ForeignKey(
            entity = Book::class,
            parentColumns = ["id"],
            childColumns = ["bookId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Note(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val bookId: String,
    val selectedText: String,
    val noteContent: String,
    val pageNumber: Int,
    val timestamp: Long = System.currentTimeMillis(),
    val isSynced: Boolean = false
)
