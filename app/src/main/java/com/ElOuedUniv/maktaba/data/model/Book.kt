package com.ElOuedUniv.maktaba.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Book(
    @SerialName("isbn")
    val isbn: String,
    @SerialName("title")
    val title: String,
    @SerialName("author")
    val author: String? = null,
    @SerialName("nb_pages")
    val nbPages: Int,
    @SerialName("image_url")
    val imageUrl: String? = null,
    @SerialName("is_finished")
    val isFinished: Boolean = false,
    @SerialName("category_id")
    val categoryId: String? = null,
    @SerialName("is_favorite")
    val isFavorite: Boolean = false
)
