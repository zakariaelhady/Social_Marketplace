package com.example.krilia.models

data class RegisterData(
    val data: RegisterResponse,
    val success: Boolean
)

data class RegisterResponse (
    var  token: String,
    var id: Int
)