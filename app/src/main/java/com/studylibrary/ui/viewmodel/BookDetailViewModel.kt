package com.studylibrary.ui.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.studylibrary.data.model.Book
import com.studylibrary.data.repository.BookRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

/**
 * ViewModel for Book Detail screen
 */
@HiltViewModel
class BookDetailViewModel @Inject constructor(
    private val bookRepository: BookRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val bookId: String = checkNotNull(savedStateHandle["bookId"])

    private val _bookState = MutableStateFlow<BookState>(BookState.Loading)
    val bookState: StateFlow<BookState> = _bookState.asStateFlow()

    private val _isFavorite = MutableStateFlow(false)
    val isFavorite: StateFlow<Boolean> = _isFavorite.asStateFlow()

    private val _isDownloading = MutableStateFlow(false)
    val isDownloading: StateFlow<Boolean> = _isDownloading.asStateFlow()

    private val _downloadProgress = MutableStateFlow(0)
    val downloadProgress: StateFlow<Int> = _downloadProgress.asStateFlow()

    init {
        loadBook()
    }

    private fun loadBook() {
        viewModelScope.launch {
            try {
                _bookState.value = BookState.Loading
                bookRepository.getBook(bookId).collect { book ->
                    if (book != null) {
                        _bookState.value = BookState.Success(book)
                        _isFavorite.value = book.isFavorite
                    } else {
                        _bookState.value = BookState.Error("Book not found")
                    }
                }
            } catch (e: Exception) {
                Timber.e(e, "Error loading book: $bookId")
                _bookState.value = BookState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun toggleFavorite() {
        viewModelScope.launch {
            try {
                val currentState = _bookState.value
                if (currentState is BookState.Success) {
                    val newFavoriteStatus = !_isFavorite.value
                    bookRepository.updateFavorite(bookId, newFavoriteStatus)
                    _isFavorite.value = newFavoriteStatus
                }
            } catch (e: Exception) {
                Timber.e(e, "Error toggling favorite")
            }
        }
    }

    fun startDownload() {
        _isDownloading.value = true
        // Download logic will be implemented with WorkManager
    }

    sealed class BookState {
        object Loading : BookState()
        data class Success(val book: Book) : BookState()
        data class Error(val message: String) : BookState()
    }
}
