package com.example.buildthon.ui.quiz_history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.buildthon.data.model.QuizResultRecord
import com.example.buildthon.data.model.ScanHistoryRecord
import com.example.buildthon.data.repository.HistoryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class HistoryUiState(
    val isLoading: Boolean = false,
    val scans: List<ScanHistoryRecord> = emptyList(),
    val quizResults: List<QuizResultRecord> = emptyList(),
    val errorMessage: String? = null
)

class HistoryViewModel(
    private val repository: HistoryRepository = HistoryRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(HistoryUiState())
    val uiState: StateFlow<HistoryUiState> = _uiState.asStateFlow()

    init {
        loadHistory()
    }

    fun loadHistory() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            val scanResult = repository.getScans()
            val quizResult = repository.getQuizResults()

            _uiState.update { state ->
                state.copy(
                    isLoading = false,
                    scans = scanResult.getOrDefault(emptyList()),
                    quizResults = quizResult.getOrDefault(emptyList()),
                    errorMessage = if (scanResult.isFailure || quizResult.isFailure) "Failed to load history from Firestore." else null
                )
            }
        }
    }

    fun deleteScan(scanId: String) {
        viewModelScope.launch {
            val result = repository.deleteScan(scanId)
            result.onSuccess {
                _uiState.update { state ->
                    state.copy(scans = state.scans.filter { it.scanId != scanId })
                }
            }.onFailure { error ->
                _uiState.update { it.copy(errorMessage = error.localizedMessage ?: "Failed to delete record.") }
            }
        }
    }
}
