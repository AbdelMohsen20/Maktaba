package com.ElOuedUniv.maktaba.presentation.book.add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ElOuedUniv.maktaba.data.model.Book
import com.ElOuedUniv.maktaba.domain.usecase.AddBookUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
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
                _uiState.update { it.copy(title = action.title, errorMessage = null) }
                validateInputs()
            }
            is AddBookUiAction.OnIsbnChange -> {
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
            AddBookUiAction.OnAddClick -> {
                if (_uiState.value.isFormValid) {
                    addBook()
                }
            }
        }
    }

    private fun validateInputs() {
        val title = _uiState.value.title
        val isbn = _uiState.value.isbn
        val nbPages = _uiState.value.nbPages

        val titleError = if (title.isBlank()) "Title cannot be empty" else null
        val isbnError = if (isbn.length != 13 || isbn.any { !it.isDigit() }) "ISBN must be 13 digits" else null
        val pagesInt = nbPages.toIntOrNull()
        val pagesError = if (pagesInt == null || pagesInt <= 0) "Pages must be a positive number" else null

        _uiState.update { 
            it.copy(
                titleError = titleError,
                isbnError = isbnError,
                nbPagesError = pagesError,
                isFormValid = titleError == null && isbnError == null && pagesError == null
            )
        }
    }

    private fun addBook() {
        val currentState = _uiState.value
        val book = Book(
            isbn = currentState.isbn,
            title = currentState.title,
            nbPages = currentState.nbPages.toIntOrNull() ?: 0,
            imageUrl = currentState.imageUri?.toString()
        )
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                addBookUseCase(book, currentState.imageUri)
                _uiState.update { it.copy(isLoading = false, isSuccess = true) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = e.message ?: "An error occurred") }
            }
        }
    }

    fun resetSuccess() {
        _uiState.update { it.copy(isSuccess = false) }
    }

    fun resetError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}
