package com.example.krilia.models

data class ProductItem(
    var data: List<PItem>
)

data class PItem(
    val id: Int,
    val title : String,
    val price: Float,
    val image: String,
    val productLikes: Int,
    val liked: String,
    val saved: String,
    val savedIcon: String,
)
