package com.example.cybershield.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

enum class ScoreCategory(val label: String, val minRange: Int, val maxRange: Int) {
    NEEDS_IMPROVEMENT("Needs Improvement", 0, 39),
    BASIC("Basic", 40, 59),
    GOOD("Good", 60, 79),
    EXCELLENT("Excellent", 80, 100);

    companion object {
        fun fromScore(score: Int): ScoreCategory = when {
            score >= 80 -> EXCELLENT
            score >= 60 -> GOOD
            score >= 40 -> BASIC
            else -> NEEDS_IMPROVEMENT
        }
    }
}

data class ScoreCategoryItem(
    val title: String,
    val weight: Int,
    val earnedPoints: Int,
    val status: ItemStatus,
    val description: String
)

enum class ItemStatus(val label: String) {
    VERIFIED("Verified"),
    INCOMPLETE("Incomplete"),
    UNKNOWN("Unknown")
}

data class SecurityScoreUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val overallScore: Int = 75,
    val category: ScoreCategory = ScoreCategory.GOOD,
    val categories: List<ScoreCategoryItem> = emptyList(),
    val suggestions: List<String> = emptyList()
)

class SecurityScoreViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(SecurityScoreUiState())
    val uiState: StateFlow<SecurityScoreUiState> = _uiState.asStateFlow()

    init {
        loadSecurityScore()
    }

    fun loadSecurityScore() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val uid = FirebaseAuth.getInstance().currentUser?.uid
                var awarenessCount = 2
                var quizScore = 80
                var dailyChallenges = 1
                var passwordChecks = 2
                var recommendationsReviewed = true

                if (uid != null) {
                    val db = FirebaseFirestore.getInstance()
                    val doc = db.collection("users").document(uid).collection("securityScore").document("profile").get().await()
                    if (doc.exists()) {
                        awarenessCount = doc.getLong("awarenessCompletedCount")?.toInt() ?: 2
                        quizScore = doc.getLong("quizBestScore")?.toInt() ?: 80
                        dailyChallenges = doc.getLong("dailyChallengesCompleted")?.toInt() ?: 1
                        passwordChecks = doc.getLong("passwordsCheckedCount")?.toInt() ?: 2
                        recommendationsReviewed = doc.getBoolean("recommendationsReviewed") ?: true
                    }
                }

                val awarenessPoints = (awarenessCount * 5).coerceAtMost(20)
                val quizPoints = ((quizScore / 100f) * 25).toInt().coerceAtMost(25)
                val challengePoints = (dailyChallenges * 10).coerceAtMost(20)
                val passwordPoints = if (passwordChecks > 0) 20 else 0
                val recPoints = if (recommendationsReviewed) 15 else 0

                val totalScore = (awarenessPoints + quizPoints + challengePoints + passwordPoints + recPoints).coerceIn(0, 100)
                val scoreCategory = ScoreCategory.fromScore(totalScore)

                val categoryItems = listOf(
                    ScoreCategoryItem(
                        title = "Security Awareness Lessons",
                        weight = 20,
                        earnedPoints = awarenessPoints,
                        status = if (awarenessCount >= 4) ItemStatus.VERIFIED else if (awarenessCount > 0) ItemStatus.INCOMPLETE else ItemStatus.UNKNOWN,
                        description = "$awarenessCount of 4 core awareness articles completed."
                    ),
                    ScoreCategoryItem(
                        title = "Cybersecurity Quiz Performance",
                        weight = 25,
                        earnedPoints = quizPoints,
                        status = if (quizScore >= 80) ItemStatus.VERIFIED else if (quizScore > 0) ItemStatus.INCOMPLETE else ItemStatus.UNKNOWN,
                        description = "Best quiz score: $quizScore%. Target is 80%+."
                    ),
                    ScoreCategoryItem(
                        title = "Daily Challenge Participation",
                        weight = 20,
                        earnedPoints = challengePoints,
                        status = if (dailyChallenges >= 2) ItemStatus.VERIFIED else ItemStatus.INCOMPLETE,
                        description = "$dailyChallenges daily security challenges completed."
                    ),
                    ScoreCategoryItem(
                        title = "Password Security Checklist",
                        weight = 20,
                        earnedPoints = passwordPoints,
                        status = if (passwordChecks > 0) ItemStatus.VERIFIED else ItemStatus.UNKNOWN,
                        description = if (passwordChecks > 0) "$passwordChecks password evaluations performed." else "No password checks recorded yet."
                    ),
                    ScoreCategoryItem(
                        title = "Security Recommendations Review",
                        weight = 15,
                        earnedPoints = recPoints,
                        status = if (recommendationsReviewed) ItemStatus.VERIFIED else ItemStatus.INCOMPLETE,
                        description = if (recommendationsReviewed) "Reviewed proactive defense recommendations." else "Pending recommendation review."
                    )
                )

                val suggestions = mutableListOf<String>()
                if (awarenessCount < 4) suggestions.add("Read remaining awareness articles in the Learning Hub to improve foundational threat knowledge.")
                if (quizScore < 80) suggestions.add("Retake the Cybersecurity Quiz and score 80% or higher to boost quiz performance points.")
                if (dailyChallenges < 2) suggestions.add("Complete additional Daily Cyber Challenges to maintain your security streak.")
                if (passwordChecks == 0) suggestions.add("Use the Password Strength Checker to evaluate your primary account passwords.")
                if (suggestions.isEmpty()) {
                    suggestions.add("Outstanding security posture! Maintain vigilance and keep your app updated.")
                }

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        overallScore = totalScore,
                        category = scoreCategory,
                        categories = categoryItems,
                        suggestions = suggestions
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.localizedMessage ?: "Failed to calculate security score"
                    )
                }
            }
        }
    }
}
