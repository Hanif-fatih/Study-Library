package com.studylibrary.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

/**
 * Highlight model representing highlighted text in a book
 */
@Entity(
    tableName = "highlights",
    foreignKeys = [
        ForeignKey(
            entity = Book::class,
            parentColumns = ["id"],
            childColumns = ["bookId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Highlight(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val bookId: String,
    val text: String,
    val pageNumber: Int,
    val color: String = "#FFFF00", // Yellow by default
    val timestamp: Long = System.currentTimeMillis(),
    val isSynced: Boolean = false
) {
    companion object {
        const val COLOR_YELLOW = "#FFFF00"
        const val COLOR_GREEN = "#00FF00"
        const val COLOR_BLUE = "#0000FF"
        const val COLOR_RED = "#FF0000"
    }
}
