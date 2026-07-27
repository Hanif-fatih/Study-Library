package com.studylibrary.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.studylibrary.data.model.Book
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Firebase Firestore Repository for remote data operations
 */
@Singleton
class FirestoreRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) {

    /**
     * Fetch all books from Firestore with real-time updates
     */
    fun getBooksRealtime(): Flow<Result<List<Book>>> = callbackFlow {
        val listener = firestore.collection("books")
            .orderBy("dateAdded", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                when {
                    error != null -> {
                        Timber.e(error, "Error fetching books")
                        trySend(Result.failure(error))
                    }
                    snapshot != null -> {
                        try {
                            val books = snapshot.toObjects(Book::class.java)
                            trySend(Result.success(books))
                        } catch (e: Exception) {
                            Timber.e(e, "Error parsing books")
                            trySend(Result.failure(e))
                        }
                    }
                }
            }

        awaitClose {
            listener.remove()
        }
    }

    /**
     * Fetch books by category
     */
    fun getBooksByCategory(category: String): Flow<Result<List<Book>>> = callbackFlow {
        val listener = firestore.collection("books")
            .whereEqualTo("category", category)
            .orderBy("dateAdded", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                when {
                    error != null -> {
                        Timber.e(error, "Error fetching books by category")
                        trySend(Result.failure(error))
                    }
                    snapshot != null -> {
                        try {
                            val books = snapshot.toObjects(Book::class.java)
                            trySend(Result.success(books))
                        } catch (e: Exception) {
                            Timber.e(e, "Error parsing books")
                            trySend(Result.failure(e))
                        }
                    }
                }
            }

        awaitClose {
            listener.remove()
        }
    }

    /**
     * Get single book details
     */
    suspend fun getBook(bookId: String): Result<Book> = try {
        val document = firestore.collection("books").document(bookId).await()
        val book = document.toObject(Book::class.java)
        if (book != null) {
            Result.success(book)
        } else {
            Result.failure(Exception("Book not found"))
        }
    } catch (e: Exception) {
        Timber.e(e, "Error fetching book: $bookId")
        Result.failure(e)
    }

    /**
     * Search books by title, author, or description
     */
    fun searchBooks(query: String): Flow<Result<List<Book>>> = callbackFlow {
        if (query.isEmpty()) {
            trySend(Result.success(emptyList()))
            close()
            return@callbackFlow
        }

        val lowerQuery = query.lowercase()
        val listener = firestore.collection("books")
            .addSnapshotListener { snapshot, error ->
                when {
                    error != null -> {
                        Timber.e(error, "Error searching books")
                        trySend(Result.failure(error))
                    }
                    snapshot != null -> {
                        try {
                            val books = snapshot.toObjects(Book::class.java)
                                .filter {
                                    it.title.lowercase().contains(lowerQuery) ||
                                    it.author.lowercase().contains(lowerQuery) ||
                                    it.description.lowercase().contains(lowerQuery)
                                }
                            trySend(Result.success(books))
                        } catch (e: Exception) {
                            Timber.e(e, "Error parsing books")
                            trySend(Result.failure(e))
                        }
                    }
                }
            }

        awaitClose {
            listener.remove()
        }
    }

    /**
     * Save highlights to Firestore
     */
    suspend fun saveHighlight(
        bookId: String,
        userId: String,
        highlightData: Map<String, Any>
    ): Result<String> = try {
        val docRef = firestore.collection("books").document(bookId)
            .collection("highlights").document()
        docRef.set(highlightData).await()
        Result.success(docRef.id)
    } catch (e: Exception) {
        Timber.e(e, "Error saving highlight")
        Result.failure(e)
    }

    /**
     * Get highlights from Firestore
     */
    fun getHighlights(bookId: String): Flow<Result<List<Map<String, Any>>>> = callbackFlow {
        val listener = firestore.collection("books").document(bookId)
            .collection("highlights")
            .addSnapshotListener { snapshot, error ->
                when {
                    error != null -> {
                        Timber.e(error, "Error fetching highlights")
                        trySend(Result.failure(error))
                    }
                    snapshot != null -> {
                        try {
                            val highlights = snapshot.documents.map { it.data ?: emptyMap() }
                            trySend(Result.success(highlights))
                        } catch (e: Exception) {
                            Timber.e(e, "Error parsing highlights")
                            trySend(Result.failure(e))
                        }
                    }
                }
            }

        awaitClose {
            listener.remove()
        }
    }

    /**
     * Save notes to Firestore
     */
    suspend fun saveNote(
        bookId: String,
        userId: String,
        noteData: Map<String, Any>
    ): Result<String> = try {
        val docRef = firestore.collection("books").document(bookId)
            .collection("notes").document()
        docRef.set(noteData).await()
        Result.success(docRef.id)
    } catch (e: Exception) {
        Timber.e(e, "Error saving note")
        Result.failure(e)
    }

    /**
     * Get notes from Firestore
     */
    fun getNotes(bookId: String): Flow<Result<List<Map<String, Any>>>> = callbackFlow {
        val listener = firestore.collection("books").document(bookId)
            .collection("notes")
            .addSnapshotListener { snapshot, error ->
                when {
                    error != null -> {
                        Timber.e(error, "Error fetching notes")
                        trySend(Result.failure(error))
                    }
                    snapshot != null -> {
                        try {
                            val notes = snapshot.documents.map { it.data ?: emptyMap() }
                            trySend(Result.success(notes))
                        } catch (e: Exception) {
                            Timber.e(e, "Error parsing notes")
                            trySend(Result.failure(e))
                        }
                    }
                }
            }

        awaitClose {
            listener.remove()
        }
    }
}
