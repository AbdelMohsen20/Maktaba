package com.ElOuedUniv.maktaba.presentation.book.detail

sealed class BookDetailUiAction {
    object ToggleEdit : BookDetailUiAction()
    object SaveBook : BookDetailUiAction()
    object CancelEdit : BookDetailUiAction()
    object DeleteBook : BookDetailUiAction()
    data class EditTitleChanged(val title: String) : BookDetailUiAction()
    data class EditAuthorChanged(val author: String) : BookDetailUiAction()
    data class EditNbPagesChanged(val nbPages: String) : BookDetailUiAction()
    data class EditCoverChanged(val imageUrl: String?) : BookDetailUiAction()
}
