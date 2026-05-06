package com.ElOuedUniv.maktaba.presentation.navigation

sealed class Screen(val route: String) {
    object Onboarding : Screen("onboarding")
    object BookList : Screen("book_list?categoryId={categoryId}&categoryName={categoryName}") {
        fun createRoute(categoryId: String? = null, categoryName: String? = null): String {
            return if (categoryId != null && categoryName != null) {
                "book_list?categoryId=$categoryId&categoryName=$categoryName"
            } else {
                "book_list"
            }
        }
    }
    object BookDetail : Screen("book_detail/{isbn}") {
        fun createRoute(isbn: String) = "book_detail/$isbn"
    }
    object CategoryList : Screen("category_list")
    object AddBook : Screen("add_book")
    object EditBook : Screen("edit_book/{isbn}") {
        fun createRoute(isbn: String) = "edit_book/$isbn"
    }
}
