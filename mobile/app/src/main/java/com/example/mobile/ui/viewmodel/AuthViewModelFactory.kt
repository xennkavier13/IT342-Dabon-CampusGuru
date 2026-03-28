package com.example.mobile.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.mobile.data.repository.AuthRepository
import com.example.mobile.network.RetrofitClient
import com.example.mobile.util.SessionManager

class AuthViewModelFactory(
    private val context: Context
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            val appContext = context.applicationContext
            val repository = AuthRepository(
                authApiService = RetrofitClient.createAuthApi(appContext),
                sessionManager = SessionManager(appContext)
            )
            return AuthViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
