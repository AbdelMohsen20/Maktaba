package com.ElOuedUniv.maktaba.data.repository

import android.net.Uri
import com.ElOuedUniv.maktaba.data.model.Book
import kotlinx.coroutines.flow.Flow

interface BookRepository {
    
    fun getAllBooks(): Flow<List<Book>>
    
    suspend fun getBookByIsbn(isbn: String): Book?

    suspend fun addBook(book: Book, imageUri: Uri?)

    suspend fun deleteBook(isbn: String)
}
