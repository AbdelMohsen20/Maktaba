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
        Book(isbn = "978-0-13-468599-1", title = "Clean Code", nbPages = 464),
        Book(isbn = "978-0-13-595705-9", title = "The Pragmatic Programmer", nbPages = 352),
        Book(isbn = "978-0-201-63361-0", title = "Design Patterns", nbPages = 395),
        Book(isbn = "978-0-13-475759-9", title = "Refactoring", nbPages = 448),
        Book(isbn = "978-1-492-07800-5", title = "Head First Design Patterns", nbPages = 694),

        /** Exercise 2 :Add 5 more books to the list above. **/

        Book(isbn = "978-1-119-29964-6", title = "Android Application Development", nbPages = 500),
        Book(isbn = "978-0-7897-2569-1", title = "Absolute Beginner’s Guide to Databases", nbPages = 336),
        Book(isbn = "978-995-986-012-5", title = "الدَّاءُ وَالدَّوَاءُ", nbPages = 293),
        Book(isbn = "978-9953-62-063-3", title = "رِيَاضُ الصَّالِحِينَ", nbPages = 688),
        Book(isbn = "978-995-986-045-3", title = "زَادُ المَعَادِ فِي هَدْيِ خَيْرِ العِبَادِ", nbPages = 1780)

    )

    private val booksFlow = MutableSharedFlow<List<Book>>(replay = 1).apply {
        tryEmit(_booksList.toList())
    }
    
    override fun getAllBooks(): Flow<List<Book>> = flow {
        delay(1000) // Simulate network
        emitAll(booksFlow)
    }

    override fun getBookByIsbn(isbn: String): Book? {
        return _booksList.find { it.isbn == isbn }
    }

    override fun addBook(book: Book) {

            _booksList.add(book)
            booksFlow.tryEmit(_booksList.toList())
    }
}

