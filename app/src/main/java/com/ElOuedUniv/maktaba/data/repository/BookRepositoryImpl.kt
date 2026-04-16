package com.ElOuedUniv.maktaba.data.repository

import android.content.Context
import android.net.Uri
import com.ElOuedUniv.maktaba.data.model.Book
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import javax.inject.Inject

class BookRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : BookRepository {

    private val booksFile = File(context.filesDir, "books.json")
    private val imagesDir = File(context.filesDir, "book_images").apply {
        if (!exists()) mkdirs()
    }

    private val _booksList = loadBooks().toMutableList()

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
        val storedBook = book.copy(imageUrl = copyImageToLocalIfNeeded(book.imageUrl, book.isbn))
        _booksList.add(storedBook)
        persistBooks()
        booksFlow.tryEmit(_booksList.toList())
    }

    override fun updateBook(book: Book) {
        val index = _booksList.indexOfFirst { it.isbn == book.isbn }
        if (index >= 0) {
            val storedBook = book.copy(imageUrl = copyImageToLocalIfNeeded(book.imageUrl, book.isbn))
            _booksList[index] = storedBook
            persistBooks()
            booksFlow.tryEmit(_booksList.toList())
        }
    }

    override fun deleteBook(isbn: String) {
        val removed = _booksList.removeAll { it.isbn == isbn }
        if (removed) {
            persistBooks()
            booksFlow.tryEmit(_booksList.toList())
        }
    }

    private fun loadBooks(): List<Book> {
        if (!booksFile.exists()) return defaultBooks()

        return try {
            val text = booksFile.readText()
            val jsonArray = JSONArray(text)
            val books = mutableListOf<Book>()

            for (i in 0 until jsonArray.length()) {
                val item = jsonArray.getJSONObject(i)
                val imageUrl = item.optString("imageUrl", null)?.takeIf { it.isNotBlank() }
                val validImageUrl = imageUrl?.let { path ->
                    if (path.startsWith("file://") || path.startsWith(context.filesDir.path)) {
                        val file = File(Uri.parse(path).path ?: path)
                        if (file.exists()) path else null
                    } else {
                        path
                    }
                }
                books.add(
                    Book(
                        isbn = item.getString("isbn"),
                        title = item.getString("title"),
                        nbPages = item.optInt("nbPages", 0),
                        imageUrl = validImageUrl,
                        author = item.optString("author", ""),
                        categoryId = item.optString("categoryId", "")
                    )
                )
            }
            books
        } catch (_: Exception) {
            defaultBooks()
        }
    }

    private fun persistBooks() {
        val jsonArray = JSONArray()
        _booksList.forEach { book ->
            jsonArray.put(book.toJson())
        }
        booksFile.writeText(jsonArray.toString())
    }

    private fun defaultBooks(): List<Book> {
        return listOf(
            Book(isbn = "11111", title = "Clean Code", nbPages = 10, imageUrl = null, author = ""),
            Book(isbn = "22222", title = "The Pragmatic Programmer", nbPages = 0, imageUrl = null, author = ""),
            Book(isbn = "33333", title = "Design Patterns", nbPages = 0, imageUrl = null, author = ""),
            Book(isbn = "44444", title = "Refactoring", nbPages = 0, imageUrl = null, author = ""),
            Book(isbn = "55555", title = "Head First Design Patterns", nbPages = 0, imageUrl = null, author = "")
        )
    }

    private fun Book.toJson(): JSONObject {
        return JSONObject().apply {
            put("isbn", isbn)
            put("title", title)
            put("nbPages", nbPages)
            put("imageUrl", imageUrl ?: JSONObject.NULL)
            put("author", author)
            put("categoryId", categoryId)
        }
    }

    private fun copyImageToLocalIfNeeded(imageUrl: String?, isbn: String): String? {
        if (imageUrl.isNullOrBlank()) return null

        val uri = Uri.parse(imageUrl)
        if (uri.scheme == "file" && uri.path?.startsWith(context.filesDir.path) == true) {
            return imageUrl
        }

        return try {
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                val destination = File(imagesDir, "$isbn.jpg")
                destination.outputStream().use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
                Uri.fromFile(destination).toString()
            }
        } catch (_: Exception) {
            null
        }
    }
}
