package com.ElOuedUniv.maktaba.presentation.book.add

import android.net.Uri
import com.ElOuedUniv.maktaba.data.model.Category

data class AddBookUiState(
    val title: String = "",
    val author: String = "",
    val isbn: String = "",
    val nbPages: String = "",
    val imageUri: Uri? = null,
    val imageUrl: String? = null,
    val selectedCategoryId: String? = null,
    val categories: List<Category> = emptyList(),
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val isFormValid: Boolean = false,
    val titleError: String? = null,
    val authorError: String? = null,
    val isbnError: String? = null,
    val nbPagesError: String? = null,
    val errorMessage: String? = null
)
