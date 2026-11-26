package com.example.cybershield.ui.password

import androidx.lifecycle.ViewModel
import com.example.cybershield.core.util.PasswordAnalysisResult
import com.example.cybershield.core.util.PasswordAnalyzer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class PasswordUiState(
    val passwordInput: String = "",
    val isPasswordVisible: Boolean = false,
    val analysisResult: PasswordAnalysisResult = PasswordAnalyzer.evaluate("")
)

class PasswordViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(PasswordUiState())
    val uiState: StateFlow<PasswordUiState> = _uiState.asStateFlow()

    fun updatePassword(password: String) {
        val analysis = PasswordAnalyzer.evaluate(password)
        _uiState.update {
            it.copy(
                passwordInput = password,
                analysisResult = analysis
            )
        }
    }

    fun toggleVisibility() {
        _uiState.update { it.copy(isPasswordVisible = !it.isPasswordVisible) }
    }

    fun clearPassword() {
        _uiState.update {
            PasswordUiState()
        }
    }
}
