package com.example.cybershield.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cybershield.data.model.QuizResultRecord
import com.example.cybershield.data.model.ScanHistoryRecord
import com.example.cybershield.data.repository.HistoryRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class CyberAlert(
    val id: String,
    val title: String,
    val description: String,
    val severity: String,
    val timestamp: Long
)

data class HomeUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val userName: String = "Cyber Cadet",
    val userEmail: String = "",
    val securityScore: Int = 85,
    val recentScans: List<ScanHistoryRecord> = emptyList(),
    val quizResults: List<QuizResultRecord> = emptyList(),
    val dailyTip: String = "Always inspect sender addresses and HTTPS certificates before entering sensitive credentials.",
    val latestAlerts: List<CyberAlert> = listOf(
        CyberAlert("1", "Zero-Day Vulnerability Alert", "Critical flaw patched in Android framework. Ensure OS updates are installed.", "High", System.currentTimeMillis() - 7200000),
        CyberAlert("2", "Phishing Campaign Warning", "Spike in fake tax refund SMS messages detected targeting mobile users.", "Medium", System.currentTimeMillis() - 86400000)
    ),
    val dailyChallengeCompleted: Boolean = false
)

class HomeViewModel(
    private val historyRepository: HistoryRepository = HistoryRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadDashboardData()
    }

    fun loadDashboardData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val currentUser = FirebaseAuth.getInstance().currentUser
                val email = currentUser?.email ?: ""
                val name = currentUser?.displayName ?: email.substringBefore("@").replaceFirstChar { it.uppercase() }

                val scansResult = historyRepository.getScans()
                val quizResult = historyRepository.getQuizResults()

                val scans = scansResult.getOrElse { emptyList() }
                val quizzes = quizResult.getOrElse { emptyList() }

                // Calculate dynamic security score based on scan ratios & quiz scores
                var score = 80
                if (scans.isNotEmpty()) {
                    val safeCount = scans.count { it.result.equals("Safe", ignoreCase = true) }
                    val ratio = safeCount.toFloat() / scans.size.toFloat()
                    score = (70 + (ratio * 25)).toInt().coerceIn(40, 100)
                }

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        userName = if (name.isBlank()) "Cyber Cadet" else name,
                        userEmail = email,
                        recentScans = scans.take(3),
                        quizResults = quizzes.take(3),
                        securityScore = score
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.localizedMessage ?: "Failed to load dashboard data"
                    )
                }
            }
        }
    }
}
