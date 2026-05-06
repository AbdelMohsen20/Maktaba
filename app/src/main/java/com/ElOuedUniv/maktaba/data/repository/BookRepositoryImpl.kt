package com.ElOuedUniv.maktaba.data.repository

import android.net.Uri
import com.ElOuedUniv.maktaba.data.model.Book
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class BookRepositoryImpl @Inject constructor() : BookRepository {

    private val _booksList = mutableListOf(
        Book(isbn = "9780132350884", title = "Clean Code", nbPages = 464, imageUrl = "https://covers.openlibrary.org/b/isbn/9780132350884-L.jpg"),
        Book(isbn = "9780201616224", title = "The Pragmatic Programmer", nbPages = 352, imageUrl = "https://covers.openlibrary.org/b/isbn/9780201616224-L.jpg"),
        Book(isbn = "9780201633610", title = "Design Patterns", nbPages = 395, imageUrl = "https://covers.openlibrary.org/b/isbn/9780201633610-L.jpg"),
        Book(isbn = "9780201485677", title = "Refactoring", nbPages = 461, imageUrl = "https://covers.openlibrary.org/b/isbn/9780201485677-L.jpg"),
        Book(isbn = "9780596007126", title = "Head First Design Patterns", nbPages = 694, imageUrl = "https://covers.openlibrary.org/b/isbn/9780596007126-L.jpg")
    )

    private val booksFlow = MutableSharedFlow<List<Book>>(replay = 1).apply {
        tryEmit(_booksList.toList())
    }
    
    override fun getAllBooks(): Flow<List<Book>> = flow {
        delay(1000) // Simulate delay
        emitAll(booksFlow)
    }

    override suspend fun getBookByIsbn(isbn: String): Book? {
        return _booksList.find { it.isbn == isbn }
    }

    override suspend fun addBook(book: Book, imageUri: Uri?) {
        val bookWithImage = if (imageUri != null) {
            book.copy(imageUrl = imageUri.toString())
        } else {
            book
        }
        _booksList.add(bookWithImage)
        booksFlow.tryEmit(_booksList.toList())
    }

    override suspend fun deleteBook(isbn: String) {
        _booksList.removeIf { it.isbn == isbn }
        booksFlow.tryEmit(_booksList.toList())
    }

    override suspend fun toggleFavorite(isbn: String, isFavorite: Boolean) {
        val index = _booksList.indexOfFirst { it.isbn == isbn }
        if (index != -1) {
            _booksList[index] = _booksList[index].copy(isFavorite = isFavorite)
            booksFlow.tryEmit(_booksList.toList())
        }
    }

    override fun searchBooks(query: String): Flow<List<Book>> {
        return booksFlow.map { list ->
            list.filter { it.title.contains(query, ignoreCase = true) }
        }
    }

    override fun getBooksByCategory(categoryId: String): Flow<List<Book>> {
        return booksFlow.map { list ->
            list.filter { it.categoryId == categoryId }
        }
    }
}
