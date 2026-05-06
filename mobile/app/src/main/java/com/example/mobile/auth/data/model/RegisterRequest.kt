package com.example.mobile.auth.data.model

data class RegisterRequest(
    val username: String,
    val institutionalEmail: String,
    val password: String,
    val firstName: String,
    val lastName: String,
    val role: String
)
