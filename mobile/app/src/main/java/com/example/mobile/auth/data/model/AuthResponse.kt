package com.example.mobile.auth.data.model

data class AuthResponse(
    val userId: String? = null,
    val username: String? = null,
    val institutionalEmail: String? = null,
    val firstName: String? = null,
    val lastName: String? = null,
    val role: String? = null,
    val token: String? = null,
    val message: String? = null
)
