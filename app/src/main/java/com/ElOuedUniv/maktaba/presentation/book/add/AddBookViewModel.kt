package com.ElOuedUniv.maktaba.presentation.book.add

import androidx.lifecycle.ViewModel
import com.ElOuedUniv.maktaba.data.model.Book
import com.ElOuedUniv.maktaba.domain.usecase.AddBookUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class AddBookViewModel @Inject constructor(
    private val addBookUseCase: AddBookUseCase
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(AddBookUiState())
    val uiState = _uiState.asStateFlow()

    fun onAction(action: AddBookUiAction) {
        when (action) {
            is AddBookUiAction.OnTitleChange -> {
                _uiState.update { validateInputs(it.copy(title = action.title)) }
            }
            is AddBookUiAction.OnIsbnChange -> {
                _uiState.update { validateInputs(it.copy(isbn = action.isbn)) }
            }
            is AddBookUiAction.OnPagesChange -> {
                _uiState.update { validateInputs(it.copy(nbPages = action.pages)) }
            }
            is AddBookUiAction.OnAuthorChange -> {
                _uiState.update { validateInputs(it.copy(author = action.value)) }
            }
            is AddBookUiAction.OnCategoryChange -> {
                _uiState.update { validateInputs(it.copy(categoryId = action.value)) }
            }
            is AddBookUiAction.OnCoverImageSelected -> {
                _uiState.update { validateInputs(it.copy(coverImageUri = action.uriString)) }
            }
            AddBookUiAction.OnAddClick -> {
                addBook()
            }
        }
    }

    private fun addBook() {
        val currentState = _uiState.value
        if (!currentState.isFormValid) {
            _uiState.update { validateInputs(it) }
            return
        }

        val book = Book(
            isbn = currentState.isbn,
            title = currentState.title,
            nbPages = currentState.nbPages.toIntOrNull() ?: 0,
            imageUrl = currentState.coverImageUri,
            author = currentState.author,
            categoryId = currentState.categoryId
        )
        addBookUseCase(book)
        _uiState.update { it.copy(isSuccess = true) }
    }

    private fun validateInputs(state: AddBookUiState): AddBookUiState {
        val titleError = if (state.title.isBlank()) "Title cannot be empty" else null
        val isbnError = when {
            state.isbn.isBlank() -> "ISBN is required"
            state.isbn.length != 13 -> "ISBN must be exactly 13 digits"
            state.isbn.any { !it.isDigit() } -> "ISBN must contain only digits"
            else -> null
        }
        val pagesError = when {
            state.nbPages.isBlank() -> "Pages are required"
            state.nbPages.toIntOrNull() == null -> "Pages must be a number"
            state.nbPages.toInt() <= 0 -> "Pages must be a positive number"
            else -> null
        }
        val isFormValid = titleError == null && isbnError == null && pagesError == null
        return state.copy(
            titleError = titleError,
            isbnError = isbnError,
            pagesError = pagesError,
            isFormValid = isFormValid
        )
    }
}
