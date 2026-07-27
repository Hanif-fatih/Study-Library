package com.studylibrary.data.db

import androidx.paging.PagingSource
import androidx.room.*
import com.studylibrary.data.model.Book
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for Book entity
 */
@Dao
interface BookDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBook(book: Book)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBooks(books: List<Book>)

    @Update
    suspend fun updateBook(book: Book)

    @Delete
    suspend fun deleteBook(book: Book)

    @Query("SELECT * FROM books WHERE id = :bookId")
    fun getBookById(bookId: String): Flow<Book?>

    @Query("SELECT * FROM books ORDER BY dateAdded DESC")
    fun getAllBooks(): PagingSource<Int, Book>

    @Query("SELECT * FROM books WHERE category = :category ORDER BY dateAdded DESC")
    fun getBooksByCategory(category: String): PagingSource<Int, Book>

    @Query("SELECT * FROM books WHERE isFavorite = 1 ORDER BY dateAdded DESC")
    fun getFavoriteBooks(): Flow<List<Book>>

    @Query("SELECT * FROM books WHERE isDownloaded = 1 ORDER BY dateAdded DESC")
    fun getDownloadedBooks(): Flow<List<Book>>

    @Query("SELECT * FROM books WHERE lastReadTime > 0 ORDER BY lastReadTime DESC LIMIT :limit")
    fun getRecentlyRead(limit: Int): Flow<List<Book>>

    @Query("SELECT DISTINCT category FROM books ORDER BY category ASC")
    fun getCategories(): Flow<List<String>>

    @Query("SELECT * FROM books WHERE title LIKE :query OR author LIKE :query OR description LIKE :query")
    fun searchBooks(query: String): Flow<List<Book>>

    @Query("UPDATE books SET currentPage = :currentPage, readingProgress = :progress WHERE id = :bookId")
    suspend fun updateReadingProgress(bookId: String, currentPage: Int, progress: Float)

    @Query("UPDATE books SET isFavorite = :isFavorite WHERE id = :bookId")
    suspend fun updateFavoriteStatus(bookId: String, isFavorite: Boolean)

    @Query("UPDATE books SET isDownloaded = :isDownloaded, downloadedFilePath = :filePath WHERE id = :bookId")
    suspend fun updateDownloadStatus(bookId: String, isDownloaded: Boolean, filePath: String?)

    @Query("DELETE FROM books WHERE isDownloaded = 0")
    suspend fun deleteNonDownloadedBooks()
}
