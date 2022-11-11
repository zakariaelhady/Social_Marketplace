package com.example.krilia.models

data class Comments(
    val data: List<Comment>
)
data class Comment(
    val id: Int,
    val comment: String,
    val date: String,
    val name: String,
    val image: String,
    val auth: Boolean,
    val userId: Int
)
