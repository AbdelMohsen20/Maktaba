package com.ElOuedUniv.maktaba.data.repository

import com.ElOuedUniv.maktaba.data.model.Category

class CategoryRepositoryImpl : CategoryRepository {

    private val categoriesList = listOf(
        Category(
            id = "1",
            name = "Programming",
            description = "Books about software development and coding"
        ),
        Category(
            id = "2",
            name = "Algorithms",
            description = "Books about algorithms and data structures"
        ),
        Category(
            id = "3",
            name = "Databases",
            description = "Books about database design and management"
        ),
        Category(
            id = "2",
            name = "Mobile Development",
            description = "Books about Android and mobile application development"
        ),
        Category(
            id = "4",
            name = "Islamic Studies",
            description = "Books about Islam, faith and religious guidance"
        ),
    )

    override fun getAllCategories(): List<Category> {
        return categoriesList
    }

    override fun getCategoryById(id: String): Category? {
        return categoriesList.find { it.id == id }
    }
}
