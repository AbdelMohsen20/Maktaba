package com.ElOuedUniv.maktaba.presentation.book

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ElOuedUniv.maktaba.data.model.Book
import com.ElOuedUniv.maktaba.data.repository.BookRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookViewModel @Inject constructor(
    private val bookRepository: BookRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val categoryId: String? = savedStateHandle["categoryId"]
    private val categoryName: String? = savedStateHandle["categoryName"]

    private val _uiState = MutableStateFlow(BookUiState(
        categoryId = categoryId,
        categoryName = categoryName
    ))
    val uiState: StateFlow<BookUiState> = _uiState.asStateFlow()

    private var allBooks: List<Book> = emptyList()

    init {
        loadBooks()
    }

    private fun loadBooks() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            val flow = if (categoryId != null) {
                bookRepository.getBooksByCategory(categoryId)
            } else {
                bookRepository.getAllBooks()
            }
            
            flow.catch { e ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = e.message) }
                }
                .collect { bookList ->
                    applyFilters(bookList)
                }
        }
    }

    private fun applyFilters(books: List<Book>? = null) {
        if (books != null) allBooks = books
        
        var filteredList = allBooks
        
        if (_uiState.value.showFavoritesOnly) {
            filteredList = filteredList.filter { it.isFavorite }
        }
        
        if (_uiState.value.searchQuery.isNotBlank()) {
            filteredList = filteredList.filter { 
                it.title.contains(_uiState.value.searchQuery, ignoreCase = true) ||
                it.author?.contains(_uiState.value.searchQuery, ignoreCase = true) == true
            }
        }
        
        _uiState.update { it.copy(isLoading = false, books = filteredList) }
    }

    fun onAction(action: BookUiAction) {
        when (action) {
            BookUiAction.RefreshBooks -> loadBooks()
            BookUiAction.OnAddBookClick -> _uiState.update { it.copy(isAddingBook = true) }
            BookUiAction.OnDismissAddBook -> _uiState.update { it.copy(isAddingBook = false) }
            BookUiAction.OnToggleGridColumns -> {
                _uiState.update { state ->
                    val nextColumns = when (state.gridColumns) {
                        1 -> 2
                        2 -> 3
                        else -> 1
                    }
                    state.copy(gridColumns = nextColumns)
                }
            }
            is BookUiAction.OnAddBookConfirm -> {
                val newBook = Book(
                    isbn = action.isbn,
                    title = action.title,
                    nbPages = action.nbPages
                )
                viewModelScope.launch {
                    try {
                        bookRepository.addBook(newBook, null)
                    } catch (e: Exception) {
                        _uiState.update { it.copy(errorMessage = e.message) }
                    }
                }
                _uiState.update { it.copy(isAddingBook = false) }
            }
            is BookUiAction.OnSearchQueryChange -> {
                _uiState.update { it.copy(searchQuery = action.query) }
                applyFilters()
            }
            is BookUiAction.OnToggleFavorite -> {
                viewModelScope.launch {
                    try {
                        bookRepository.toggleFavorite(action.isbn, action.isFavorite)
                    } catch (e: Exception) {
                        _uiState.update { it.copy(errorMessage = e.message) }
                    }
                }
            }
            is BookUiAction.OnFilterFavorites -> {
                _uiState.update { it.copy(showFavoritesOnly = action.showOnlyFavorites) }
                applyFilters()
            }
        }
    }
}
