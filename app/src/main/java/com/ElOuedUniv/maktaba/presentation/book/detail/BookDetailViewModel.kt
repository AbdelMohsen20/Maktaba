package com.ElOuedUniv.maktaba.presentation.book.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ElOuedUniv.maktaba.domain.usecase.DeleteBookUseCase
import com.ElOuedUniv.maktaba.domain.usecase.GetBookByIsbnUseCase
import com.ElOuedUniv.maktaba.domain.usecase.UpdateBookUseCase
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
    private val updateBookUseCase: UpdateBookUseCase,
    private val deleteBookUseCase: DeleteBookUseCase
) : ViewModel() {

    private val isbn: String = checkNotNull(savedStateHandle["isbn"])
    
    private val _uiState = MutableStateFlow(BookDetailUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadBook()
    }

    private fun loadBook() {
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
        val book = getBookByIsbnUseCase(isbn)
        if (book != null) {
            _uiState.update {
                it.copy(
                    isLoading = false,
                    book = book,
                    editTitle = book.title,
                    editAuthor = book.author,
                    editIsbn = book.isbn,
                    editNbPages = book.nbPages.toString(),
                    editCoverImageUri = book.imageUrl,
                    isDeleted = false,
                    isSaveSuccessful = false
                )
            }
        } else {
            _uiState.update { it.copy(isLoading = false, errorMessage = "Book not found") }
        }
    }

    fun onAction(action: BookDetailUiAction) {
        when (action) {
            BookDetailUiAction.ToggleEdit -> {
                _uiState.update { it.copy(isEditing = true, isSaveSuccessful = false) }
            }
            BookDetailUiAction.CancelEdit -> {
                _uiState.update { state ->
                    state.book?.let { book ->
                        state.copy(
                            isEditing = false,
                            editTitle = book.title,
                            editAuthor = book.author,
                            editIsbn = book.isbn,
                            editNbPages = book.nbPages.toString(),
                            editCoverImageUri = book.imageUrl,
                            errorMessage = null
                        )
                    } ?: state.copy(isEditing = false)
                }
            }
            BookDetailUiAction.SaveBook -> {
                val currentState = _uiState.value
                val pages = currentState.editNbPages.toIntOrNull() ?: 0
                currentState.book?.let { existingBook ->
                    val updatedBook = existingBook.copy(
                        title = currentState.editTitle,
                        author = currentState.editAuthor,
                        nbPages = pages,
                        imageUrl = currentState.editCoverImageUri
                    )
                    viewModelScope.launch {
                        updateBookUseCase(updatedBook)
                        _uiState.update { it.copy(book = updatedBook, isEditing = false, isSaveSuccessful = true) }
                    }
                }
            }
            BookDetailUiAction.DeleteBook -> {
                viewModelScope.launch {
                    deleteBookUseCase(isbn)
                    _uiState.update { it.copy(isDeleted = true) }
                }
            }
            is BookDetailUiAction.EditTitleChanged -> {
                _uiState.update { it.copy(editTitle = action.title) }
            }
            is BookDetailUiAction.EditAuthorChanged -> {
                _uiState.update { it.copy(editAuthor = action.author) }
            }
            is BookDetailUiAction.EditNbPagesChanged -> {
                _uiState.update { it.copy(editNbPages = action.nbPages) }
            }
            is BookDetailUiAction.EditCoverChanged -> {
                _uiState.update { it.copy(editCoverImageUri = action.imageUrl) }
            }
        }
    }
}
