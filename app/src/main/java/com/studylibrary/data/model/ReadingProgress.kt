package com.studylibrary.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

/**
 * ReadingProgress model tracking user's reading progress
 */
@Entity(
    tableName = "reading_progress",
    foreignKeys = [
        ForeignKey(
            entity = Book::class,
            parentColumns = ["id"],
            childColumns = ["bookId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class ReadingProgress(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val bookId: String,
    val currentPage: Int,
    val totalPages: Int,
    val readingPercentage: Float,
    val lastUpdated: Long = System.currentTimeMillis()
)
