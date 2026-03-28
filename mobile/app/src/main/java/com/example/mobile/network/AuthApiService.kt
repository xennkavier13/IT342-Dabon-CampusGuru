package com.example.mobile.network

import com.example.mobile.data.model.AuthResponse
import com.example.mobile.data.model.LoginRequest
import com.example.mobile.data.model.RegisterRequest
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApiService {

    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): AuthResponse

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): AuthResponse
}
