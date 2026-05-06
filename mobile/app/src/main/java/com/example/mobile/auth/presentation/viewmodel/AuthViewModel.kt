package com.example.mobile.auth.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mobile.auth.data.model.AuthResponse
import com.example.mobile.auth.data.repository.AuthRepository
import kotlinx.coroutines.launch

class AuthViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _authResult = MutableLiveData<AuthResponse?>()
    val authResult: LiveData<AuthResponse?> = _authResult

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    fun login(identifier: String, password: String) {
        if (identifier.isBlank() || password.isBlank()) {
            _errorMessage.value = "Email and password are required"
            return
        }

        executeAuth { authRepository.login(identifier, password) }
    }

    fun register(
        firstName: String,
        lastName: String,
        institutionalEmail: String,
        password: String,
        confirmPassword: String,
        role: String
    ) {
        if (firstName.isBlank() || lastName.isBlank() || institutionalEmail.isBlank() || password.isBlank()) {
            _errorMessage.value = "First name, last name, email, and password are required"
            return
        }

        if (password != confirmPassword) {
            _errorMessage.value = "Passwords do not match"
            return
        }

        executeAuth {
            authRepository.register(
                firstName = firstName,
                lastName = lastName,
                institutionalEmail = institutionalEmail,
                password = password,
                role = role
            )
        }
    }

    fun consumeError() {
        _errorMessage.value = null
    }

    private fun executeAuth(action: suspend () -> Result<AuthResponse>) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            action()
                .onSuccess { response ->
                    _authResult.value = response
                }
                .onFailure { throwable ->
                    _errorMessage.value = throwable.message ?: "Authentication error"
                }

            _isLoading.value = false
        }
    }
}
