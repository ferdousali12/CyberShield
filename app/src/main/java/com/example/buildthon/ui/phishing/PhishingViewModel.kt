package com.example.buildthon.ui.phishing

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.buildthon.data.model.ScanResultModel
import com.example.buildthon.data.repository.PhishingRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class PhishingUiState(
    val urlInput: String = "",
    val isLoading: Boolean = false,
    val scanResult: ScanResultModel? = null,
    val errorMessage: String? = null
)

class PhishingViewModel(
    private val repository: PhishingRepository = PhishingRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(PhishingUiState())
    val uiState: StateFlow<PhishingUiState> = _uiState.asStateFlow()

    fun updateUrlInput(input: String) {
        _uiState.update { it.copy(urlInput = input, errorMessage = null) }
    }

    fun scanUrl() {
        val url = _uiState.value.urlInput.trim()
        if (url.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Please enter a valid website URL.") }
            return
        }

        // Basic URL validation
        val isValid = Patterns.WEB_URL.matcher(url).matches() || url.contains(".")
        if (!isValid) {
            _uiState.update { it.copy(errorMessage = "Invalid URL format. Please include a domain (e.g., example.com).") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null, scanResult = null) }
            val result = repository.analyzeUrl(url)
            result.fold(
                onSuccess = { scanModel ->
                    _uiState.update { it.copy(isLoading = false, scanResult = scanModel) }
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = error.localizedMessage ?: "Network error during threat analysis. Please try again."
                        )
                    }
                }
            )
        }
    }

    fun resetScan() {
        _uiState.update { PhishingUiState() }
    }
}
