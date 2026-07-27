package com.studylibrary.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.studylibrary.data.model.Book
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Combined Book Repository - merges Firestore and Local data
 */
@Singleton
class BookRepository @Inject constructor(
    private val firestoreRepository: FirestoreRepository,
    private val localRepository: LocalRepository
) {

    /**
     * Get books from Firestore and sync with local database
     */
    fun getBooksWithSync(): Flow<Result<List<Book>>> {
        return firestoreRepository.getBooksRealtime()
    }

    /**
     * Get favorite books from local database
     */
    fun getFavoriteBooks(): Flow<List<Book>> = localRepository.getFavoriteBooks()

    /**
     * Get downloaded books from local database
     */
    fun getDownloadedBooks(): Flow<List<Book>> = localRepository.getDownloadedBooks()

    /**
     * Get recently read books
     */
    fun getRecentlyRead(limit: Int = 10): Flow<List<Book>> = localRepository.getRecentlyRead(limit)

    /**
     * Get all categories
     */
    fun getCategories(): Flow<List<String>> = localRepository.getCategories()

    /**
     * Get books by category
     */
    fun getBooksByCategory(category: String): Flow<Result<List<Book>>> {
        return firestoreRepository.getBooksByCategory(category)
    }

    /**
     * Get paginated books
     */
    fun getPaginatedBooks(): Flow<PagingData<Book>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { localRepository.bookDao.getAllBooks() }
        ).flow
    }

    /**
     * Search books
     */
    fun searchBooks(query: String): Flow<List<Book>> = localRepository.searchBooks(query)

    /**
     * Get single book
     */
    fun getBook(bookId: String): Flow<Book?> = localRepository.getBookById(bookId)

    /**
     * Update favorite status
     */
    suspend fun updateFavorite(bookId: String, isFavorite: Boolean) {
        localRepository.updateFavoriteStatus(bookId, isFavorite)
    }

    /**
     * Update reading progress
     */
    suspend fun updateReadingProgress(bookId: String, currentPage: Int, totalPages: Int) {
        val progress = if (totalPages > 0) (currentPage * 100f) / totalPages else 0f
        localRepository.updateReadingProgress(bookId, currentPage, progress)
    }

    /**
     * Save book locally after download
     */
    suspend fun saveDownloadedBook(book: Book) {
        localRepository.insertBook(book)
    }

    /**
     * Insert books from Firestore to local DB
     */
    suspend fun insertBooks(books: List<Book>) {
        localRepository.insertBooks(books)
    }
}
