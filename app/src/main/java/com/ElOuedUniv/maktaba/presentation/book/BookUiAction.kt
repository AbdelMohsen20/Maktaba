package com.ElOuedUniv.maktaba.presentation.book

sealed interface BookUiAction {
    object RefreshBooks : BookUiAction
    object OnAddBookClick : BookUiAction
    object OnDismissAddBook : BookUiAction
    object OnToggleGridColumns : BookUiAction
    data class OnAddBookConfirm(val title: String, val isbn: String, val nbPages: Int) : BookUiAction
    data class OnSearchQueryChange(val query: String) : BookUiAction
    data class OnToggleFavorite(val isbn: String, val isFavorite: Boolean) : BookUiAction
    data class OnFilterFavorites(val showOnlyFavorites: Boolean) : BookUiAction
}
