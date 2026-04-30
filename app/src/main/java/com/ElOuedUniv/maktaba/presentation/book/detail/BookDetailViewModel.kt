package com.ElOuedUniv.maktaba.presentation.book.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ElOuedUniv.maktaba.domain.usecase.DeleteBookUseCase
import com.ElOuedUniv.maktaba.domain.usecase.GetBookByIsbnUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getBookByIsbnUseCase: GetBookByIsbnUseCase,
    private val deleteBookUseCase: DeleteBookUseCase
) : ViewModel() {

    private val isbn: String = checkNotNull(savedStateHandle["isbn"])
    
    private val _uiState = MutableStateFlow(BookDetailUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadBook()
    }

    private fun loadBook() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val book = getBookByIsbnUseCase(isbn)
                _uiState.update { it.copy(isLoading = false, book = book) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = e.message ?: "Failed to load book") }
            }
        }
    }

    fun onAction(action: BookDetailUiAction) {
        when (action) {
            BookDetailUiAction.OnBackClick -> { /* Handled in View */ }
            BookDetailUiAction.OnDeleteClick -> deleteBook()
        }
    }

    private fun deleteBook() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                deleteBookUseCase(isbn)
                _uiState.update { it.copy(isLoading = false, isDeleted = true) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = e.message ?: "Failed to delete book") }
            }
        }
    }
}
