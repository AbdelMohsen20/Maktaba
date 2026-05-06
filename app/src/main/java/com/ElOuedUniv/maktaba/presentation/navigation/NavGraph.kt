package com.ElOuedUniv.maktaba.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.ElOuedUniv.maktaba.presentation.book.BookListView
import com.ElOuedUniv.maktaba.presentation.book.add.AddBookView
import com.ElOuedUniv.maktaba.presentation.book.add.EditBookView
import com.ElOuedUniv.maktaba.presentation.book.detail.BookDetailView
import com.ElOuedUniv.maktaba.presentation.category.CategoryListView
import com.ElOuedUniv.maktaba.presentation.onboarding.OnboardingView

@Composable
fun NavGraph(
    navController: NavHostController = rememberNavController(),
    startDestination: String = Screen.Onboarding.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.Onboarding.route) {
            OnboardingView(
                onNavigateToLibrary = {
                    navController.navigate(Screen.BookList.route) {
                        popUpTo(Screen.Onboarding.route) { inclusive = true }
                    }
                }
            )
        }
        
        composable(
            route = Screen.BookList.route,
            arguments = listOf(
                navArgument("categoryId") { 
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null 
                },
                navArgument("categoryName") { 
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null 
                }
            )
        ) {
            BookListView(
                onCategoriesClick = { navController.navigate(Screen.CategoryList.route) },
                onAddBookClick = { navController.navigate(Screen.AddBook.route) },
                onBookClick = { isbn -> 
                    navController.navigate(Screen.BookDetail.createRoute(isbn))
                }
            )
        }
        
        composable(Screen.BookDetail.route) {
            BookDetailView(
                onBackClick = { navController.popBackStack() },
                onEditClick = { isbn -> navController.navigate(Screen.EditBook.createRoute(isbn)) }
            )
        }
        
        composable(Screen.EditBook.route) {
            EditBookView(onBackClick = { navController.popBackStack() })
        }
        
        composable(Screen.CategoryList.route) {
            CategoryListView(
                onBackClick = { navController.popBackStack() },
                onCategoryClick = { category ->
                    navController.navigate(Screen.BookList.createRoute(category.id, category.name))
                }
            )
        }
        
        composable(Screen.AddBook.route) {
            AddBookView(onBackClick = { navController.popBackStack() })
        }
    }
}
