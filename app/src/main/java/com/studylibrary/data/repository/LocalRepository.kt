package com.studylibrary.data.repository

import com.studylibrary.data.db.*
import com.studylibrary.data.model.*
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Local Repository for Room Database operations
 */
@Singleton
class LocalRepository @Inject constructor(
    private val bookDao: BookDao,
    private val bookmarkDao: BookmarkDao,
    private val highlightDao: HighlightDao,
    private val noteDao: NoteDao,
    private val readingProgressDao: ReadingProgressDao
) {

    // ===== BOOK OPERATIONS =====
    suspend fun insertBook(book: Book) = bookDao.insertBook(book)
    suspend fun insertBooks(books: List<Book>) = bookDao.insertBooks(books)
    suspend fun updateBook(book: Book) = bookDao.updateBook(book)
    suspend fun deleteBook(book: Book) = bookDao.deleteBook(book)

    fun getBookById(bookId: String): Flow<Book?> = bookDao.getBookById(bookId)
    fun getFavoriteBooks(): Flow<List<Book>> = bookDao.getFavoriteBooks()
    fun getDownloadedBooks(): Flow<List<Book>> = bookDao.getDownloadedBooks()
    fun getRecentlyRead(limit: Int = 10): Flow<List<Book>> = bookDao.getRecentlyRead(limit)
    fun getCategories(): Flow<List<String>> = bookDao.getCategories()
    fun searchBooks(query: String): Flow<List<Book>> = bookDao.searchBooks("%$query%")

    suspend fun updateReadingProgress(bookId: String, currentPage: Int, progress: Float) =
        bookDao.updateReadingProgress(bookId, currentPage, progress)

    suspend fun updateFavoriteStatus(bookId: String, isFavorite: Boolean) =
        bookDao.updateFavoriteStatus(bookId, isFavorite)

    suspend fun updateDownloadStatus(bookId: String, isDownloaded: Boolean, filePath: String?) =
        bookDao.updateDownloadStatus(bookId, isDownloaded, filePath)

    suspend fun deleteNonDownloadedBooks() = bookDao.deleteNonDownloadedBooks()

    // ===== BOOKMARK OPERATIONS =====
    suspend fun insertBookmark(bookmark: Bookmark): Long = bookmarkDao.insertBookmark(bookmark)
    suspend fun deleteBookmark(bookmark: Bookmark) = bookmarkDao.deleteBookmark(bookmark)
    fun getBookmarks(bookId: String): Flow<List<Bookmark>> = bookmarkDao.getBookmarks(bookId)
    suspend fun getBookmark(bookId: String, pageNumber: Int): Bookmark? =
        bookmarkDao.getBookmark(bookId, pageNumber)

    // ===== HIGHLIGHT OPERATIONS =====
    suspend fun insertHighlight(highlight: Highlight): Long = highlightDao.insertHighlight(highlight)
    suspend fun updateHighlight(highlight: Highlight) = highlightDao.updateHighlight(highlight)
    suspend fun deleteHighlight(highlight: Highlight) = highlightDao.deleteHighlight(highlight)
    fun getHighlights(bookId: String): Flow<List<Highlight>> = highlightDao.getHighlights(bookId)
    suspend fun getUnsyncedHighlights(bookId: String): List<Highlight> =
        highlightDao.getUnsyncedHighlights(bookId)
    suspend fun markHighlightsAsSynced(ids: List<Int>) = highlightDao.markAsSynced(ids)

    // ===== NOTE OPERATIONS =====
    suspend fun insertNote(note: Note): Long = noteDao.insertNote(note)
    suspend fun updateNote(note: Note) = noteDao.updateNote(note)
    suspend fun deleteNote(note: Note) = noteDao.deleteNote(note)
    fun getNotes(bookId: String): Flow<List<Note>> = noteDao.getNotes(bookId)
    suspend fun getUnsyncedNotes(bookId: String): List<Note> = noteDao.getUnsyncedNotes(bookId)
    suspend fun markNotesAsSynced(ids: List<Int>) = noteDao.markAsSynced(ids)

    // ===== READING PROGRESS OPERATIONS =====
    suspend fun insertProgress(progress: ReadingProgress) = readingProgressDao.insertProgress(progress)
    suspend fun updateProgress(progress: ReadingProgress) = readingProgressDao.updateProgress(progress)
    fun getProgress(bookId: String): Flow<ReadingProgress?> = readingProgressDao.getProgress(bookId)
}
