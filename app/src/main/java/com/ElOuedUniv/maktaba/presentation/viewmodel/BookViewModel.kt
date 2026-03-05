package com.ElOuedUniv.maktaba.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ElOuedUniv.maktaba.data.model.Book
import com.ElOuedUniv.maktaba.domain.usecase.GetBooksUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class BookViewModel(
    private val getBooksUseCase: GetBooksUseCase
) : ViewModel() {

    private val _books = MutableStateFlow<List<Book>>(emptyList())
    val books: StateFlow<List<Book>> = _books.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun getLongBooks(): List<Book> {
        return books.value.filter { it.nbPages > 400 }
    }

    fun totalPages(): Int {
        return books.value.sumOf { it.nbPages }
    }

    init {
        loadBooks()
    }

    private fun loadBooks() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val bookList = getBooksUseCase()
                _books.value = bookList
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun refreshBooks() {
        loadBooks()
    }
}

