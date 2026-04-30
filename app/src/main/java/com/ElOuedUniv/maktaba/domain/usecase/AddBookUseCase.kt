package com.ElOuedUniv.maktaba.domain.usecase

import android.net.Uri
import com.ElOuedUniv.maktaba.data.model.Book
import com.ElOuedUniv.maktaba.data.repository.BookRepository
import javax.inject.Inject

class AddBookUseCase @Inject constructor(
    private val bookRepository: BookRepository
) {
    suspend operator fun invoke(book: Book, imageUri: Uri?) {
        bookRepository.addBook(book, imageUri)
    }
}
