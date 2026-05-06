package com.ElOuedUniv.maktaba.presentation.book.add

import android.net.Uri

sealed class AddBookUiAction {
    data class OnTitleChange(val title: String) : AddBookUiAction()
    data class OnAuthorChange(val author: String) : AddBookUiAction()
    data class OnIsbnChange(val isbn: String) : AddBookUiAction()
    data class OnPagesChange(val pages: String) : AddBookUiAction()
    data class OnImagePicked(val uri: Uri?) : AddBookUiAction()
    data class OnCategorySelected(val categoryId: String) : AddBookUiAction()
    object OnAddClick : AddBookUiAction()
}
