package com.example.mobile.data.repository

import com.example.mobile.data.model.AuthResponse
import com.example.mobile.data.model.LoginRequest
import com.example.mobile.data.model.RegisterRequest
import com.example.mobile.network.AuthApiService
import com.example.mobile.util.SessionManager
import org.json.JSONObject
import retrofit2.HttpException

class AuthRepository(
    private val authApiService: AuthApiService,
    private val sessionManager: SessionManager
) {

    suspend fun register(
        firstName: String,
        lastName: String,
        institutionalEmail: String,
        password: String,
        role: String
    ): Result<AuthResponse> {
        val normalizedEmail = institutionalEmail.trim().lowercase()
        return runCatching {
            authApiService.register(
                RegisterRequest(
                    username = normalizedEmail,
                    institutionalEmail = normalizedEmail,
                    password = password,
                    firstName = firstName.trim(),
                    lastName = lastName.trim(),
                    role = role
                )
            ).also { response ->
                sessionManager.saveAccessToken(response.access_token)
            }
        }.fold(
            onSuccess = { Result.success(it) },
            onFailure = { throwable -> Result.failure(mapException(throwable)) }
        )
    }

    suspend fun login(usernameOrEmail: String, password: String): Result<AuthResponse> {
        return runCatching {
            authApiService.login(
                LoginRequest(
                    username = usernameOrEmail.trim().lowercase(),
                    password = password
                )
            ).also { response ->
                sessionManager.saveAccessToken(response.access_token)
            }
        }.fold(
            onSuccess = { Result.success(it) },
            onFailure = { throwable -> Result.failure(mapException(throwable)) }
        )
    }

    private fun mapException(throwable: Throwable): Throwable {
        if (throwable is HttpException) {
            val code = throwable.code()
            val bodyMessage = throwable.response()?.errorBody()?.string()?.let { parseMessage(it) }

            val message = when {
                !bodyMessage.isNullOrBlank() -> bodyMessage
                code == 401 -> "Invalid credentials"
                code == 409 -> "User already exists"
                else -> "Authentication failed ($code)"
            }

            return Exception(message)
        }

        return Exception(throwable.message ?: "Network error. Please try again.")
    }

    private fun parseMessage(rawJson: String): String? {
        return runCatching {
            val json = JSONObject(rawJson)
            when {
                json.has("message") -> json.getString("message")
                json.has("error") -> json.getString("error")
                else -> null
            }
        }.getOrNull()
    }
}
