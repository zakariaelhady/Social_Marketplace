package com.example.krilia.models


data class LoginData(
    val data: LoginResponse,
    val success: Boolean
)
data class LoginResponse (
    var success: Boolean,
    var  token: String,
    var user: User
)