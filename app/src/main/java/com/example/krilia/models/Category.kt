package com.example.krilia.models

import com.google.gson.annotations.SerializedName

data class Categories(
    val data: List<Category>
)

data class Category(
    val id: Int,
    val image: String,
    @SerializedName("categorie")
    val category: String,
    val icon: String
)
