package com.ElOuedUniv.maktaba.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Category(
    val id: String,
    val name: String,
    val description: String,
    val imageUrl: String? = null
)
