package com.ElOuedUniv.maktaba.presentation.book.detail

import com.ElOuedUniv.maktaba.data.model.Book

data class BookDetailUiState(
    val book: Book? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isEditing: Boolean = false,
    val editTitle: String = "",
    val editAuthor: String = "",
    val editIsbn: String = "",
    val editNbPages: String = "",
    val editCoverImageUri: String? = null,
    val isDeleted: Boolean = false,
    val isSaveSuccessful: Boolean = false
)
