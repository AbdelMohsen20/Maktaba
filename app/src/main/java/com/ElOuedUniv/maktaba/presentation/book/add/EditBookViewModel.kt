package com.ElOuedUniv.maktaba.presentation.book.add

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ElOuedUniv.maktaba.data.model.Book
import com.ElOuedUniv.maktaba.data.repository.BookRepository
import com.ElOuedUniv.maktaba.domain.usecase.AddBookUseCase
import com.ElOuedUniv.maktaba.domain.usecase.GetCategoriesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditBookViewModel @Inject constructor(
    private val bookRepository: BookRepository,
    private val getCategoriesUseCase: GetCategoriesUseCase,
    private val addBookUseCase: AddBookUseCase, // Reusing for update if implementation supports upsert
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    
    private val isbn: String = checkNotNull(savedStateHandle["isbn"])
    
    private val _uiState = MutableStateFlow(AddBookUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadCategories()
        loadBook()
    }

    private fun loadCategories() {
        viewModelScope.launch {
            getCategoriesUseCase().collect { categories ->
                _uiState.update { it.copy(categories = categories) }
            }
        }
    }

    private fun loadBook() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val book = bookRepository.getBookByIsbn(isbn)
            if (book != null) {
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        title = book.title,
                        author = book.author ?: "",
                        isbn = book.isbn,
                        nbPages = book.nbPages.toString(),
                        imageUrl = book.imageUrl,
                        selectedCategoryId = book.categoryId,
                        isFormValid = true
                    ) 
                }
            } else {
                _uiState.update { it.copy(isLoading = false, errorMessage = "Book not found") }
            }
        }
    }

    fun onAction(action: AddBookUiAction) {
        when (action) {
            is AddBookUiAction.OnTitleChange -> {
                _uiState.update { it.copy(title = action.title, errorMessage = null) }
                validateInputs()
            }
            is AddBookUiAction.OnAuthorChange -> {
                _uiState.update { it.copy(author = action.author, errorMessage = null) }
                validateInputs()
            }
            is AddBookUiAction.OnIsbnChange -> {
                // Usually ISBN shouldn't be edited as it's the PK, but let's allow it if needed or disable in UI
                _uiState.update { it.copy(isbn = action.isbn, errorMessage = null) }
                validateInputs()
            }
            is AddBookUiAction.OnPagesChange -> {
                _uiState.update { it.copy(nbPages = action.pages, errorMessage = null) }
                validateInputs()
            }
            is AddBookUiAction.OnImagePicked -> {
                _uiState.update { it.copy(imageUri = action.uri, errorMessage = null) }
            }
            is AddBookUiAction.OnCategorySelected -> {
                _uiState.update { it.copy(selectedCategoryId = action.categoryId) }
            }
            AddBookUiAction.OnAddClick -> {
                if (_uiState.value.isFormValid) {
                    updateBook()
                }
            }
        }
    }

    private fun validateInputs() {
        val title = _uiState.value.title
        val author = _uiState.value.author
        val isbn = _uiState.value.isbn
        val nbPages = _uiState.value.nbPages

        val titleError = if (title.isBlank()) "Title cannot be empty" else null
        val authorError = if (author.isBlank()) "Author cannot be empty" else null
        val isbnError = if (isbn.length != 13 || isbn.any { !it.isDigit() }) "ISBN must be 13 digits" else null
        val pagesInt = nbPages.toIntOrNull()
        val pagesError = if (pagesInt == null || pagesInt <= 0) "Pages must be a positive number" else null

        _uiState.update { 
            it.copy(
                titleError = titleError,
                authorError = authorError,
                isbnError = isbnError,
                nbPagesError = pagesError,
                isFormValid = titleError == null && authorError == null && isbnError == null && pagesError == null
            )
        }
    }

    private fun updateBook() {
        val currentState = _uiState.value
        val book = Book(
            isbn = currentState.isbn,
            title = currentState.title,
            author = currentState.author,
            nbPages = currentState.nbPages.toIntOrNull() ?: 0,
            imageUrl = currentState.imageUrl, // Preserve old URL if new one not picked
            categoryId = currentState.selectedCategoryId
        )
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                addBookUseCase(book, currentState.imageUri) // Supabase insert with upsert=true works as update
                _uiState.update { it.copy(isLoading = false, isSuccess = true) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = e.message ?: "An error occurred") }
            }
        }
    }

    fun resetSuccess() { _uiState.update { it.copy(isSuccess = false) } }
    fun resetError() { _uiState.update { it.copy(errorMessage = null) } }
}
