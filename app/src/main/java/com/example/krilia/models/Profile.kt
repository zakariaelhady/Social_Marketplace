package com.example.krilia.models

data class Profile(
    var data: ProfileData
)
data class ProfileData(
    val authuser : User,
    val id : String,
    val totalProducts: Int,
    val auth: Boolean
)
data class User(
    val id: Int,
    val name: String,
    val email: String,
    val image: String,
    val location: String,
    val country: String,
    val phone: String,
    val created_at: String,
    val updated_at: String
)

