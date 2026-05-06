package com.ElOuedUniv.maktaba.data.repository

import com.ElOuedUniv.maktaba.data.model.Category
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class SupabaseCategoryRepositoryImpl @Inject constructor(
    private val supabaseClient: SupabaseClient
) : CategoryRepository {

    override fun getAllCategories(): Flow<List<Category>> = flow {
        try {
            val categories = supabaseClient.from("categories").select().decodeList<Category>()
            emit(categories)
        } catch (e: Exception) {
            emit(emptyList())
        }
    }

    override fun getCategoryById(id: String): Category? {
        // Simplified for now
        return null
    }
}
