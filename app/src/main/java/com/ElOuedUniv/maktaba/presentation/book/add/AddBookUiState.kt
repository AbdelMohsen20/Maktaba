package com.ElOuedUniv.maktaba.presentation.book.add

data class AddBookUiState(
    val title: String = "",
    val isbn: String = "",
    val nbPages: String = "",
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null,
    val isFormValid: Boolean = false,
    val titleError: String? = null,
    val isbnError: String? = null,
    val pagesError: String? = null,
    val coverImageUri: String? = null,
    val author: String = "",
    val categoryId: String = "",
)
