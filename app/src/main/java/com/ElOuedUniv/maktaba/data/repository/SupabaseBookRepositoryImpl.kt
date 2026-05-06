package com.ElOuedUniv.maktaba.data.repository

import android.content.Context
import android.net.Uri
import android.util.Log
import com.ElOuedUniv.maktaba.data.model.Book
import dagger.hilt.android.qualifiers.ApplicationContext
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SupabaseBookRepositoryImpl @Inject constructor(
    private val supabaseClient: SupabaseClient,
    @ApplicationContext private val context: Context
) : BookRepository {

    private val _refreshFlow = MutableSharedFlow<Unit>(replay = 1).apply { tryEmit(Unit) }

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    override fun getAllBooks(): Flow<List<Book>> = _refreshFlow.flatMapLatest {
        flow {
            try {
                Log.d("SupabaseBookRepo", "Fetching books...")
                val books = supabaseClient.from("books").select().decodeList<Book>()
                emit(books)
            } catch (e: Exception) {
                Log.e("SupabaseBookRepo", "Error fetching books", e)
                emit(emptyList())
            }
        }
    }.flowOn(Dispatchers.IO)

    override suspend fun getBookByIsbn(isbn: String): Book? {
        return withContext(Dispatchers.IO) {
            try {
                supabaseClient.from("books")
                    .select {
                        filter { eq("isbn", isbn) }
                    }
                    .decodeSingleOrNull<Book>()
            } catch (e: Exception) {
                Log.e("SupabaseBookRepo", "Error fetching book by ISBN", e)
                null
            }
        }
    }

    override suspend fun addBook(book: Book, imageUri: Uri?) {
        withContext(Dispatchers.IO) {
            var finalImageUrl = book.imageUrl
            if (imageUri != null) {
                try {
                    val bytes = context.contentResolver.openInputStream(imageUri)?.readBytes()
                    if (bytes != null) {
                        val fileName = "${book.isbn}_${System.currentTimeMillis()}.jpg"
                        val bucket = supabaseClient.storage["book_covers"]
                        bucket.upload(fileName, bytes) { upsert = true }
                        finalImageUrl = bucket.publicUrl(fileName)
                    }
                } catch (e: Exception) {
                    Log.e("SupabaseBookRepo", "Storage upload failed", e)
                    throw e
                }
            }

            try {
                val bookToInsert = book.copy(imageUrl = finalImageUrl)
                supabaseClient.from("books").upsert(bookToInsert)
                _refreshFlow.emit(Unit)
            } catch (e: Exception) {
                Log.e("SupabaseBookRepo", "Database operation failed", e)
                throw e
            }
        }
    }

    override suspend fun deleteBook(isbn: String) {
        withContext(Dispatchers.IO) {
            try {
                supabaseClient.from("books").delete {
                    filter { eq("isbn", isbn) }
                }
                _refreshFlow.emit(Unit)
            } catch (e: Exception) {
                Log.e("SupabaseBookRepo", "Error deleting book", e)
                throw e
            }
        }
    }

    override suspend fun toggleFavorite(isbn: String, isFavorite: Boolean) {
        withContext(Dispatchers.IO) {
            try {
                supabaseClient.from("books").update(
                    {
                        set("is_favorite", isFavorite)
                    }
                ) {
                    filter { eq("isbn", isbn) }
                }
                _refreshFlow.emit(Unit)
            } catch (e: Exception) {
                Log.e("SupabaseBookRepo", "Error toggling favorite", e)
                throw e
            }
        }
    }

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    override fun searchBooks(query: String): Flow<List<Book>> = _refreshFlow.flatMapLatest {
        flow {
            try {
                val books = if (query.isBlank()) {
                    supabaseClient.from("books").select().decodeList<Book>()
                } else {
                    supabaseClient.from("books").select {
                        filter {
                            ilike("title", "%$query%")
                        }
                    }.decodeList<Book>()
                }
                emit(books)
            } catch (e: Exception) {
                Log.e("SupabaseBookRepo", "Search failed", e)
                emit(emptyList())
            }
        }
    }.flowOn(Dispatchers.IO)

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    override fun getBooksByCategory(categoryId: String): Flow<List<Book>> = _refreshFlow.flatMapLatest {
        flow {
            try {
                val books = supabaseClient.from("books").select {
                    filter { eq("category_id", categoryId) }
                }.decodeList<Book>()
                emit(books)
            } catch (e: Exception) {
                Log.e("SupabaseBookRepo", "Error fetching books by category", e)
                emit(emptyList())
            }
        }
    }.flowOn(Dispatchers.IO)
}
