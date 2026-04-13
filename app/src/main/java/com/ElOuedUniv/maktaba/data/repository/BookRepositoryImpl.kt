package com.ElOuedUniv.maktaba.data.repository

import com.ElOuedUniv.maktaba.data.model.Book
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class BookRepositoryImpl @Inject constructor() : BookRepository {

    private val _booksList = mutableListOf(
        Book(isbn = "11111", title = "Clean Code", nbPages = 10 ,imageUrl = null, author = ""),
        Book(isbn = "22222", title = "The Pragmatic Programmer", nbPages = 0 ,imageUrl = null ,author = ""),
        Book(isbn = "33333", title = "Design Patterns", nbPages = 0 , imageUrl = null ,author = ""),
        Book(isbn = "44444", title = "Refactoring", nbPages = 0 ,imageUrl = null, author = ""),
        Book(isbn = "55555", title = "Head First Design Patterns", nbPages = 0 ,imageUrl = null, author = "")
    )

    private val booksFlow = MutableSharedFlow<List<Book>>(replay = 1).apply {
        tryEmit(_booksList.toList())
    }
    
    override fun getAllBooks(): Flow<List<Book>> = flow {
        delay(2000) // Simulate delay
        emitAll(booksFlow)
    }

    override fun getBookByIsbn(isbn: String): Book? {
        return _booksList.find { it.isbn == isbn }
    }

    override fun addBook(book: Book) {
        _booksList.add(book)
        booksFlow.tryEmit(_booksList.toList())
    }

    override fun updateBook(book: Book) {
        val index = _booksList.indexOfFirst { it.isbn == book.isbn }
        if (index >= 0) {
            _booksList[index] = book
            booksFlow.tryEmit(_booksList.toList())
        }
    }

    override fun deleteBook(isbn: String) {
        val removed = _booksList.removeAll { it.isbn == isbn }
        if (removed) {
            booksFlow.tryEmit(_booksList.toList())
        }
    }
}
