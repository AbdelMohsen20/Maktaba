package com.ElOuedUniv.maktaba.data.repository

import com.ElOuedUniv.maktaba.data.model.Book

/**
 * Repository for managing book data
 * This follows the Repository pattern to abstract data sources
 */
class BookRepository {

    /**
     * TODO for Students (TP1 - Exercise 1):
     * Complete the book information for each book in the list below.
     * Add the following information for each book:
     * - isbn: Use a valid ISBN-13 format (e.g., "978-3-16-148410-0")
     * - nbPages: Add the actual number of pages
     *
     * Example:
     * Book(
     *     isbn = "978-0-13-468599-1",
     *     title = "Clean Code",
     *     nbPages = 464
     * )
     */
    private val booksList = listOf(
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

    /**
     * TODO for Students (TP1 - Exercise 2):
     * Add 5 more books to the list above.
     * Choose books related to Computer Science, Programming, or any topic you like.
     * Remember to include complete information (ISBN, title, nbPages).
     *
     * Tip: You can find ISBN numbers for books on:
     * - Google Books
     * - Amazon
     * - GoodReads
     */

    /**
     * Get all books from the repository
     * @return List of all books
     */
    fun getAllBooks(): List<Book> {
        return booksList
    }

    /**
     * Get a book by ISBN
     * @param isbn The ISBN of the book to find
     * @return The book if found, null otherwise
     */
    fun getBookByIsbn(isbn: String): Book? {
        return booksList.find { it.isbn == isbn }
    }
}
