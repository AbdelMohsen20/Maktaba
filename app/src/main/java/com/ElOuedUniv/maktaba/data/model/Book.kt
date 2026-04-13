package com.ElOuedUniv.maktaba.data.model

data class Book(
    val isbn: String,
    val title: String,
    val nbPages: Int,
    val imageUrl: String?,
    val author: String = "",
    val categoryId: String = ""
)

