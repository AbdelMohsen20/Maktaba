package com.ElOuedUniv.maktaba.domain.usecase

import com.ElOuedUniv.maktaba.data.model.Book
import com.ElOuedUniv.maktaba.data.repository.BookRepository

class DeleteBookUseCase(
    private val bookRepository: BookRepository
) {
    operator fun invoke(isbn: String) {
        bookRepository.deleteBook(isbn)
    }
}
