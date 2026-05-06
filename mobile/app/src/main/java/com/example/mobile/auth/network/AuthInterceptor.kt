package com.example.mobile.auth.network

import com.example.mobile.auth.util.SessionManager
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(
    private val sessionManager: SessionManager
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val path = originalRequest.url.encodedPath
        val isAuthEndpoint = path.endsWith("/auth/login") || path.endsWith("/auth/register")

        if (isAuthEndpoint) {
            return chain.proceed(originalRequest)
        }

        val token = sessionManager.getAccessToken()
        if (token.isNullOrBlank()) {
            return chain.proceed(originalRequest)
        }

        val authorizedRequest = originalRequest.newBuilder()
            .addHeader("Authorization", "Bearer $token")
            .build()

        return chain.proceed(authorizedRequest)
    }
}
