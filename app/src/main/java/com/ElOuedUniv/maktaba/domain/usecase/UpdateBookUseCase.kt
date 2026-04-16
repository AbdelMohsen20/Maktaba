package com.ElOuedUniv.maktaba.domain.usecase

import com.ElOuedUniv.maktaba.data.model.Book
import com.ElOuedUniv.maktaba.data.repository.BookRepository

class UpdateBookUseCase(
    private val bookRepository: BookRepository
) {
    operator fun invoke(book: Book) {
        bookRepository.updateBook(book)
    }
}
