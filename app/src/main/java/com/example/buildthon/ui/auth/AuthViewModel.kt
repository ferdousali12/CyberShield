package com.example.buildthon.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.buildthon.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AuthUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isAuthenticated: Boolean = false
)

class AuthViewModel(
    private val authRepository: AuthRepository = AuthRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    init {
        checkCurrentUser()
    }

    private fun checkCurrentUser() {
        val user = authRepository.currentUser
        _uiState.update { it.copy(isAuthenticated = user != null) }
    }

    fun login(email: String, pass: String, onSuccess: () -> Unit) {
        if (email.isBlank() || pass.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Email and password cannot be empty") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val result = authRepository.login(email.trim(), pass)
            _uiState.update { it.copy(isLoading = false) }

            result.fold(
                onSuccess = {
                    _uiState.update { state -> state.copy(isAuthenticated = true, errorMessage = null) }
                    onSuccess()
                },
                onFailure = { error ->
                    _uiState.update { state -> state.copy(errorMessage = error.localizedMessage ?: "Authentication failed") }
                }
            )
        }
    }

    fun register(email: String, pass: String, confirmPass: String, onSuccess: () -> Unit) {
        if (email.isBlank() || pass.isBlank() || confirmPass.isBlank()) {
            _uiState.update { it.copy(errorMessage = "All fields are required") }
            return
        }
        if (pass != confirmPass) {
            _uiState.update { it.copy(errorMessage = "Passwords do not match") }
            return
        }
        if (pass.length < 6) {
            _uiState.update { it.copy(errorMessage = "Password must be at least 6 characters") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val result = authRepository.register(email.trim(), pass)
            _uiState.update { it.copy(isLoading = false) }

            result.fold(
                onSuccess = {
                    _uiState.update { state -> state.copy(isAuthenticated = true, errorMessage = null) }
                    onSuccess()
                },
                onFailure = { error ->
                    _uiState.update { state -> state.copy(errorMessage = error.localizedMessage ?: "Registration failed") }
                }
            )
        }
    }

    fun logout(onLogout: () -> Unit) {
        authRepository.logout()
        _uiState.update { AuthUiState(isAuthenticated = false) }
        onLogout()
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}
