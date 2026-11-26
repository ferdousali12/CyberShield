package com.example.cybershield.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cybershield.core.util.Constants
import com.example.cybershield.data.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.userProfileChangeRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

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

    fun loginDemo(onSuccess: () -> Unit) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val firebaseAuth = try {
                FirebaseAuth.getInstance()
            } catch (_: Exception) {
                _uiState.update { state ->
                    state.copy(
                        isLoading = false,
                        errorMessage = "Demo login failed. Check your internet connection."
                    )
                }
                return@launch
            }

            try {
                // Attempt to sign in with demo credentials
                val authResult = firebaseAuth.signInWithEmailAndPassword(Constants.DEMO_EMAIL, Constants.DEMO_PASSWORD).await()
                val user = authResult.user
                if (user?.displayName.isNullOrBlank()) {
                    val profileUpdates = userProfileChangeRequest {
                        displayName = Constants.DEMO_DISPLAY_NAME
                    }
                    user?.updateProfile(profileUpdates)?.await()
                }
                _uiState.update { state -> state.copy(isLoading = false, isAuthenticated = true, errorMessage = null) }
                onSuccess()
            } catch (_: Exception) {
                // If sign-in fails (e.g. demo user hasn't been created in Firebase Auth yet), automatically register the demo user!
                try {
                    val createResult = firebaseAuth.createUserWithEmailAndPassword(Constants.DEMO_EMAIL, Constants.DEMO_PASSWORD).await()
                    val user = createResult.user
                    val profileUpdates = userProfileChangeRequest {
                        displayName = Constants.DEMO_DISPLAY_NAME
                    }
                    user?.updateProfile(profileUpdates)?.await()

                    _uiState.update { state -> state.copy(isLoading = false, isAuthenticated = true, errorMessage = null) }
                    onSuccess()
                } catch (_: Exception) {
                    _uiState.update { state ->
                        state.copy(
                            isLoading = false,
                            errorMessage = "Demo login failed. Check your internet connection."
                        )
                    }
                }
            }
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
