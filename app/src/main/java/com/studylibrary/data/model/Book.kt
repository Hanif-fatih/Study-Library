package com.studylibrary

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Book model class representing a book in the library
 * Used for both local Room database and Firebase Firestore
 */
@Entity(tableName = "books")
data class Book(
    @PrimaryKey
    val id: String,
    val title: String,
    val author: String,
    val description: String,
    val category: String,
    val coverImageUrl: String,
    val bookUrl: String,
    val fileType: String, // PDF or EPUB
    val fileSize: Long = 0,
    val downloadedFilePath: String? = null,
    val isDownloaded: Boolean = false,
    val isFavorite: Boolean = false,
    val currentPage: Int = 0,
    val totalPages: Int = 0,
    val readingProgress: Float = 0f,
    val lastReadTime: Long = 0,
    val dateAdded: Long = System.currentTimeMillis(),
    val rating: Float = 0f,
    val reviewCount: Int = 0
) : java.io.Serializable {
    companion object {
        const val PDF = "PDF"
        const val EPUB = "EPUB"
    }
}
