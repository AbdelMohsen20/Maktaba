package com.ElOuedUniv.maktaba.data.repository

import com.ElOuedUniv.maktaba.data.model.Category
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class CategoryRepositoryImpl @Inject constructor() : CategoryRepository {

    private val _categoriesList = listOf(
        Category(
            id = "1",
            name = "Programming",
            description = "Books about software development and coding",
            imageUrl = "https://images.unsplash.com/photo-1517694712202-14dd9538aa97?auto=format&fit=crop&q=80&w=400"
        ),
        Category(
            id = "2",
            name = "Algorithms",
            description = "Books about algorithms and data structures",
            imageUrl = "https://images.unsplash.com/photo-1504384308090-c894fdcc538d?auto=format&fit=crop&q=80&w=400"
        ),
        Category(
            id = "3",
            name = "Databases",
            description = "Books about database design and management",
            imageUrl = "https://images.unsplash.com/photo-1544383835-bda2bc66a55d?auto=format&fit=crop&q=80&w=400"
        )
    )

    private val categoriesFlow = MutableSharedFlow<List<Category>>(replay = 1).apply {
        tryEmit(_categoriesList)
    }
    
    override fun getAllCategories(): Flow<List<Category>> = flow {
        delay(2000) // Simulate delay
        emitAll(categoriesFlow)
    }

    override fun getCategoryById(id: String): Category? {
        return _categoriesList.find { it.id == id }
    }
}
