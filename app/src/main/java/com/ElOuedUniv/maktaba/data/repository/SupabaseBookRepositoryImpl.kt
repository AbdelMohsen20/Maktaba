package com.ElOuedUniv.maktaba.data.repository

import android.content.Context
import android.net.Uri
import android.util.Log
import com.ElOuedUniv.maktaba.data.model.Book
import dagger.hilt.android.qualifiers.ApplicationContext
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SupabaseBookRepositoryImpl @Inject constructor(
    private val supabaseClient: SupabaseClient,
    @ApplicationContext private val context: Context
) : BookRepository {

    private val _refreshFlow = MutableSharedFlow<Unit>(replay = 1).apply { tryEmit(Unit) }

    override fun getAllBooks(): Flow<List<Book>> = flow {
        _refreshFlow.collect {
            try {
                val books = supabaseClient.postgrest["books"].select().decodeList<Book>()
                emit(books)
            } catch (e: Exception) {
                Log.e("SupabaseBookRepo", "Error fetching books", e)
                emit(emptyList())
            }
        }
    }

    override suspend fun getBookByIsbn(isbn: String): Book? {
        return withContext(Dispatchers.IO) {
            try {
                supabaseClient.postgrest["books"]
                    .select {
                        filter {
                            eq("isbn", isbn)
                        }
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
                        
                        Log.d("SupabaseBookRepo", "Attempting to upload: $fileName")
                        
                        // Uploading to bucket
                        bucket.upload(fileName, bytes) {
                            upsert = true
                        }
                        
                        // Get public URL
                        finalImageUrl = bucket.publicUrl(fileName)
                        Log.d("SupabaseBookRepo", "Upload successful: $finalImageUrl")
                    }
                } catch (e: Exception) {
                    Log.e("SupabaseBookRepo", "Storage upload failed", e)
                    // Rethrow to inform the UI that the upload (and thus adding the book) failed
                    throw e 
                }
            }

            try {
                val bookToInsert = book.copy(imageUrl = finalImageUrl)
                supabaseClient.postgrest["books"].insert(bookToInsert)
                Log.d("SupabaseBookRepo", "Book DB insertion successful")
                _refreshFlow.emit(Unit)
            } catch (e: Exception) {
                Log.e("SupabaseBookRepo", "Database insertion failed", e)
                throw e
            }
        }
    }

    override suspend fun deleteBook(isbn: String) {
        withContext(Dispatchers.IO) {
            try {
                supabaseClient.postgrest["books"].delete {
                    filter {
                        eq("isbn", isbn)
                    }
                }
                _refreshFlow.emit(Unit)
            } catch (e: Exception) {
                Log.e("SupabaseBookRepo", "Error deleting book", e)
                throw e
            }
        }
    }
}
