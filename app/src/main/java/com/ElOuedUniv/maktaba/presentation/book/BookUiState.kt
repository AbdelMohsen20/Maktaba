package com.ElOuedUniv.maktaba.presentation.book

import com.ElOuedUniv.maktaba.data.model.Book

data class BookUiState(
    val books: List<Book> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isAddingBook: Boolean = false,
    val gridColumns: Int = 2,
    val searchQuery: String = "",
    val showFavoritesOnly: Boolean = false,
    val categoryId: String? = null,
    val categoryName: String? = null
)
