package com.studylibrary.ui.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.studylibrary.data.model.Book
import com.studylibrary.data.model.Bookmark
import com.studylibrary.data.model.Highlight
import com.studylibrary.data.model.Note
import com.studylibrary.data.repository.BookRepository
import com.studylibrary.data.repository.LocalRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

/**
 * ViewModel for Reader screen with full reading features
 */
@HiltViewModel
class ReaderViewModel @Inject constructor(
    private val bookRepository: BookRepository,
    private val localRepository: LocalRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val bookId: String = checkNotNull(savedStateHandle["bookId"])

    private val _bookState = MutableStateFlow<BookState>(BookState.Loading)
    val bookState: StateFlow<BookState> = _bookState.asStateFlow()

    private val _currentPage = MutableStateFlow(0)
    val currentPage: StateFlow<Int> = _currentPage.asStateFlow()

    private val _totalPages = MutableStateFlow(0)
    val totalPages: StateFlow<Int> = _totalPages.asStateFlow()

    private val _readerMode = MutableStateFlow(ReaderMode.DAY)
    val readerMode: StateFlow<ReaderMode> = _readerMode.asStateFlow()

    private val _fontSize = MutableStateFlow(16)
    val fontSize: StateFlow<Int> = _fontSize.asStateFlow()

    private val _fontFamily = MutableStateFlow("sans-serif")
    val fontFamily: StateFlow<String> = _fontFamily.asStateFlow()

    private val _lineSpacing = MutableStateFlow(1.5f)
    val lineSpacing: StateFlow<Float> = _lineSpacing.asStateFlow()

    private val _isFullscreen = MutableStateFlow(false)
    val isFullscreen: StateFlow<Boolean> = _isFullscreen.asStateFlow()

    private val _bookmarks = MutableStateFlow<List<Bookmark>>(emptyList())
    val bookmarks: StateFlow<List<Bookmark>> = _bookmarks.asStateFlow()

    private val _highlights = MutableStateFlow<List<Highlight>>(emptyList())
    val highlights: StateFlow<List<Highlight>> = _highlights.asStateFlow()

    private val _notes = MutableStateFlow<List<Note>>(emptyList())
    val notes: StateFlow<List<Note>> = _notes.asStateFlow()

    init {
        loadBook()
        loadReadingProgress()
        loadBookmarks()
        loadHighlights()
        loadNotes()
    }

    private fun loadBook() {
        viewModelScope.launch {
            try {
                bookRepository.getBook(bookId).collect { book ->
                    if (book != null) {
                        _bookState.value = BookState.Success(book)
                        _currentPage.value = book.currentPage
                        _totalPages.value = book.totalPages
                    } else {
                        _bookState.value = BookState.Error("Book not found")
                    }
                }
            } catch (e: Exception) {
                Timber.e(e, "Error loading book")
                _bookState.value = BookState.Error(e.message ?: "Unknown error")
            }
        }
    }

    private fun loadReadingProgress() {
        viewModelScope.launch {
            try {
                localRepository.getProgress(bookId).collect { progress ->
                    if (progress != null) {
                        _currentPage.value = progress.currentPage
                        _totalPages.value = progress.totalPages
                    }
                }
            } catch (e: Exception) {
                Timber.e(e, "Error loading reading progress")
            }
        }
    }

    fun updateCurrentPage(page: Int) {
        _currentPage.value = page
        saveReadingProgress()
    }

    private fun saveReadingProgress() {
        viewModelScope.launch {
            try {
                val currentPage = _currentPage.value
                val totalPages = _totalPages.value
                if (totalPages > 0) {
                    bookRepository.updateReadingProgress(bookId, currentPage, totalPages)
                }
            } catch (e: Exception) {
                Timber.e(e, "Error saving reading progress")
            }
        }
    }

    fun setReaderMode(mode: ReaderMode) {
        _readerMode.value = mode
    }

    fun setFontSize(size: Int) {
        _fontSize.value = size
    }

    fun setFontFamily(family: String) {
        _fontFamily.value = family
    }

    fun setLineSpacing(spacing: Float) {
        _lineSpacing.value = spacing
    }

    fun toggleFullscreen() {
        _isFullscreen.value = !_isFullscreen.value
    }

    // ===== BOOKMARK OPERATIONS =====
    private fun loadBookmarks() {
        viewModelScope.launch {
            try {
                localRepository.getBookmarks(bookId).collect { bookmarks ->
                    _bookmarks.value = bookmarks
                }
            } catch (e: Exception) {
                Timber.e(e, "Error loading bookmarks")
            }
        }
    }

    fun addBookmark() {
        viewModelScope.launch {
            try {
                val bookmark = Bookmark(
                    bookId = bookId,
                    pageNumber = _currentPage.value,
                    chapterTitle = ""
                )
                localRepository.insertBookmark(bookmark)
            } catch (e: Exception) {
                Timber.e(e, "Error adding bookmark")
            }
        }
    }

    fun removeBookmark(pageNumber: Int) {
        viewModelScope.launch {
            try {
                val bookmark = localRepository.getBookmark(bookId, pageNumber) ?: return@launch
                localRepository.deleteBookmark(bookmark)
            } catch (e: Exception) {
                Timber.e(e, "Error removing bookmark")
            }
        }
    }

    fun jumpToBookmark(pageNumber: Int) {
        _currentPage.value = pageNumber
        saveReadingProgress()
    }

    // ===== HIGHLIGHT OPERATIONS =====
    private fun loadHighlights() {
        viewModelScope.launch {
            try {
                localRepository.getHighlights(bookId).collect { highlights ->
                    _highlights.value = highlights
                }
            } catch (e: Exception) {
                Timber.e(e, "Error loading highlights")
            }
        }
    }

    fun addHighlight(text: String, color: String) {
        viewModelScope.launch {
            try {
                val highlight = Highlight(
                    bookId = bookId,
                    text = text,
                    pageNumber = _currentPage.value,
                    color = color
                )
                localRepository.insertHighlight(highlight)
            } catch (e: Exception) {
                Timber.e(e, "Error adding highlight")
            }
        }
    }

    fun removeHighlight(highlightId: Int) {
        viewModelScope.launch {
            try {
                val highlights = _highlights.value
                val highlight = highlights.find { it.id == highlightId } ?: return@launch
                localRepository.deleteHighlight(highlight)
            } catch (e: Exception) {
                Timber.e(e, "Error removing highlight")
            }
        }
    }

    // ===== NOTE OPERATIONS =====
    private fun loadNotes() {
        viewModelScope.launch {
            try {
                localRepository.getNotes(bookId).collect { notes ->
                    _notes.value = notes
                }
            } catch (e: Exception) {
                Timber.e(e, "Error loading notes")
            }
        }
    }

    fun addNote(selectedText: String, noteContent: String) {
        viewModelScope.launch {
            try {
                val note = Note(
                    bookId = bookId,
                    selectedText = selectedText,
                    noteContent = noteContent,
                    pageNumber = _currentPage.value
                )
                localRepository.insertNote(note)
            } catch (e: Exception) {
                Timber.e(e, "Error adding note")
            }
        }
    }

    fun updateNote(noteId: Int, content: String) {
        viewModelScope.launch {
            try {
                val notes = _notes.value
                val note = notes.find { it.id == noteId } ?: return@launch
                localRepository.updateNote(note.copy(noteContent = content))
            } catch (e: Exception) {
                Timber.e(e, "Error updating note")
            }
        }
    }

    fun deleteNote(noteId: Int) {
        viewModelScope.launch {
            try {
                val notes = _notes.value
                val note = notes.find { it.id == noteId } ?: return@launch
                localRepository.deleteNote(note)
            } catch (e: Exception) {
                Timber.e(e, "Error deleting note")
            }
        }
    }

    sealed class BookState {
        object Loading : BookState()
        data class Success(val book: Book) : BookState()
        data class Error(val message: String) : BookState()
    }

    enum class ReaderMode {
        DAY, NIGHT, SEPIA
    }
}
