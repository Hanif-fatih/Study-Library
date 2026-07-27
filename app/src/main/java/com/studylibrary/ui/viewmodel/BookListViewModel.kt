package com.studylibrary.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import com.studylibrary.data.model.Book
import com.studylibrary.data.repository.BookRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

/**
 * ViewModel for Book Library screen
 */
@HiltViewModel
class BookListViewModel @Inject constructor(
    private val bookRepository: BookRepository
) : ViewModel() {

    // State management
    private val _booksState = MutableStateFlow<BooksState>(BooksState.Loading)
    val booksState: StateFlow<BooksState> = _booksState.asStateFlow()

    private val _categoriesState = MutableStateFlow<List<String>>(emptyList())
    val categoriesState: StateFlow<List<String>> = _categoriesState.asStateFlow()

    private val _selectedCategory = MutableStateFlow<String?>(null)
    val selectedCategory: StateFlow<String?> = _selectedCategory.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _viewMode = MutableStateFlow(ViewMode.GRID)
    val viewMode: StateFlow<ViewMode> = _viewMode.asStateFlow()

    private val _sortBy = MutableStateFlow(SortBy.NEWEST)
    val sortBy: StateFlow<SortBy> = _sortBy.asStateFlow()

    init {
        loadBooks()
        loadCategories()
    }

    private fun loadBooks() {
        viewModelScope.launch {
            try {
                _booksState.value = BooksState.Loading
                bookRepository.getBooksWithSync().collect { result ->
                    result.onSuccess { books ->
                        val sortedBooks = sortBooks(books, _sortBy.value)
                        _booksState.value = BooksState.Success(sortedBooks)
                    }
                    result.onFailure { error ->
                        Timber.e(error, "Failed to load books")
                        _booksState.value = BooksState.Error(error.message ?: "Unknown error")
                    }
                }
            } catch (e: Exception) {
                Timber.e(e, "Error in loadBooks")
                _booksState.value = BooksState.Error(e.message ?: "Unknown error")
            }
        }
    }

    private fun loadCategories() {
        viewModelScope.launch {
            try {
                bookRepository.getCategories().collect { categories ->
                    _categoriesState.value = categories
                }
            } catch (e: Exception) {
                Timber.e(e, "Error loading categories")
            }
        }
    }

    fun setCategory(category: String?) {
        _selectedCategory.value = category
        loadBooks()
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun toggleViewMode() {
        _viewMode.value = if (_viewMode.value == ViewMode.GRID) ViewMode.LIST else ViewMode.GRID
    }

    fun setSortBy(sortBy: SortBy) {
        _sortBy.value = sortBy
        loadBooks()
    }

    fun toggleFavorite(bookId: String) {
        viewModelScope.launch {
            try {
                val currentState = _booksState.value
                if (currentState is BooksState.Success) {
                    val book = currentState.books.find { it.id == bookId } ?: return@launch
                    bookRepository.updateFavorite(bookId, !book.isFavorite)
                }
            } catch (e: Exception) {
                Timber.e(e, "Error toggling favorite")
            }
        }
    }

    private fun sortBooks(books: List<Book>, sortBy: SortBy): List<Book> {
        return when (sortBy) {
            SortBy.NEWEST -> books.sortedByDescending { it.dateAdded }
            SortBy.OLDEST -> books.sortedBy { it.dateAdded }
            SortBy.TITLE_ASC -> books.sortedBy { it.title }
            SortBy.TITLE_DESC -> books.sortedByDescending { it.title }
            SortBy.RATING -> books.sortedByDescending { it.rating }
        }
    }

    sealed class BooksState {
        object Loading : BooksState()
        data class Success(val books: List<Book>) : BooksState()
        data class Error(val message: String) : BooksState()
    }

    enum class ViewMode {
        GRID, LIST
    }

    enum class SortBy {
        NEWEST, OLDEST, TITLE_ASC, TITLE_DESC, RATING
    }
}
