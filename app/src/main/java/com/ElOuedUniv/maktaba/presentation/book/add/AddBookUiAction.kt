package com.ElOuedUniv.maktaba.presentation.book.add



sealed class AddBookUiAction {
    data class OnTitleChange(val title: String) : AddBookUiAction()
    data class OnIsbnChange(val isbn: String) : AddBookUiAction()
    data class OnPagesChange(val pages: String) : AddBookUiAction()
    data class OnAuthorChange(val value: String) : AddBookUiAction()
    data class OnCategoryChange(val value: String) : AddBookUiAction()
    data class OnCoverImageSelected(val uriString: String) : AddBookUiAction()

    object OnAddClick : AddBookUiAction()
}
