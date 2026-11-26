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
import java.text.SimpleDateFormat
import java.util.*

data class ChallengeQuestion(
    val id: String,
    val category: String,
    val scenario: String,
    val options: List<String>,
    val correctIndex: Int,
    val explanation: String
)

data class DailyChallengeUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val currentDate: String = "",
    val question: ChallengeQuestion? = null,
    val selectedOption: Int? = null,
    val isSubmitted: Boolean = false,
    val isCorrect: Boolean = false,
    val alreadyCompletedToday: Boolean = false,
    val streakCount: Int = 5,
    val pointsEarned: Int = 0
)

class DailyChallengeViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(DailyChallengeUiState())
    val uiState: StateFlow<DailyChallengeUiState> = _uiState.asStateFlow()

    private val questionBank = listOf(
        ChallengeQuestion(
            id = "q1",
            category = "Phishing Email",
            scenario = "You receive an email from 'Netflix Support' stating your billing failed and asking you to click a link to update payment details immediately.",
            options = listOf(
                "Click the link and enter your credit card info",
                "Log into Netflix directly via app/browser to check account status",
                "Reply to the email asking if it's legitimate",
                "Forward the email to friends"
            ),
            correctIndex = 1,
            explanation = "Always navigate directly to official apps or bookmarked websites rather than clicking links in unsolicited billing alerts."
        ),
        ChallengeQuestion(
            id = "q2",
            category = "Fake Shopping Site",
            scenario = "You find an online store selling luxury headphones for 90% off. The domain is `audio-flash-sale.xyz` and it only accepts cryptocurrency.",
            options = listOf(
                "Buy immediately before stock runs out",
                "Recognize red flags (suspicious .xyz TLD, extreme discount, crypto-only) and avoid",
                "Use a debit card to purchase",
                "Share the deal on social media"
            ),
            correctIndex = 1,
            explanation = "Extremely high discounts on unknown .xyz domains combined with untraceable payment methods are hallmarks of scam shops."
        ),
        ChallengeQuestion(
            id = "q3",
            category = "OTP Scam",
            scenario = "A caller claiming to be your bank's fraud department says an unauthorized transaction occurred and asks for the 6-digit OTP you just received.",
            options = listOf(
                "Read the OTP so they can cancel the transaction",
                "Hang up immediately. Legitimate banks never ask for OTPs over phone calls",
                "Ask for their employee ID and verify",
                "Give them a partial OTP"
            ),
            correctIndex = 1,
            explanation = "OTPs are your cryptographic keys. Legitimate bank representatives will never ask for your OTP or password over the phone."
        ),
        ChallengeQuestion(
            id = "q4",
            category = "Public Wi-Fi",
            scenario = "At an airport, you connect to an open Wi-Fi network named 'Free_Airport_WiFi' without a password and notice login prompts.",
            options = listOf(
                "Log into your bank accounts since it's convenient",
                "Use a VPN or avoid accessing sensitive accounts on unsecured public networks",
                "Disable all security warnings",
                "Share files over local network"
            ),
            correctIndex = 1,
            explanation = "Open public Wi-Fi networks can be rogue access points. Always use a trusted VPN or cellular data for sensitive logins."
        ),
        ChallengeQuestion(
            id = "q5",
            category = "Social Engineering",
            scenario = "You receive an urgent Slack message from your company CEO asking you to quickly purchase gift cards for an executive client meeting.",
            options = listOf(
                "Buy the gift cards right away to impress the CEO",
                "Verify the request via an independent communication channel (phone call or in-person)",
                "Ignore company policy and pay with personal funds",
                "Send credit card details over chat"
            ),
            correctIndex = 1,
            explanation = "CEO fraud / Business Email Compromise (BEC) uses urgency and authority pressure. Always verify out-of-band."
        )
    )

    init {
        loadTodayChallenge()
    }

    fun loadTodayChallenge() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val todayStr = dateFormat.format(Date())

                val uid = FirebaseAuth.getInstance().currentUser?.uid
                var completedToday = false
                var streak = 5

                if (uid != null) {
                    val db = FirebaseFirestore.getInstance()
                    val doc = db.collection("users").document(uid).collection("dailyChallenges").document(todayStr).get().await()
                    if (doc.exists()) {
                        completedToday = doc.getBoolean("completed") ?: false
                    }

                    val streakDoc = db.collection("users").document(uid).collection("streaks").document("current").get().await()
                    if (streakDoc.exists()) {
                        streak = streakDoc.getLong("currentStreak")?.toInt() ?: 5
                    }
                }

                // Select question based on date hash
                val qIndex = Math.abs(todayStr.hashCode()) % questionBank.size
                val todayQuestion = questionBank[qIndex]

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        currentDate = todayStr,
                        question = todayQuestion,
                        alreadyCompletedToday = completedToday,
                        isSubmitted = completedToday,
                        selectedOption = if (completedToday) todayQuestion.correctIndex else null,
                        isCorrect = completedToday,
                        streakCount = streak
                    )
                }
            } catch (e: Exception) {
                val fallbackDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        currentDate = fallbackDate,
                        question = questionBank[0],
                        streakCount = 5
                    )
                }
            }
        }
    }

    fun selectOption(index: Int) {
        if (!_uiState.value.isSubmitted && !_uiState.value.alreadyCompletedToday) {
            _uiState.update { it.copy(selectedOption = index) }
        }
    }

    fun submitAnswer() {
        val state = _uiState.value
        if (state.selectedOption == null || state.isSubmitted || state.alreadyCompletedToday) return

        val correct = state.selectedOption == state.question?.correctIndex
        val newStreak = if (correct) state.streakCount + 1 else state.streakCount

        _uiState.update {
            it.copy(
                isSubmitted = true,
                isCorrect = correct,
                alreadyCompletedToday = true,
                streakCount = newStreak,
                pointsEarned = if (correct) 50 else 0
            )
        }

        viewModelScope.launch {
            try {
                val uid = FirebaseAuth.getInstance().currentUser?.uid
                if (uid != null) {
                    val db = FirebaseFirestore.getInstance()
                    val data = hashMapOf(
                        "completed" to true,
                        "correct" to correct,
                        "date" to state.currentDate,
                        "timestamp" to System.currentTimeMillis()
                    )
                    db.collection("users").document(uid).collection("dailyChallenges").document(state.currentDate).set(data).await()

                    val streakData = hashMapOf(
                        "currentStreak" to newStreak,
                        "lastCompletedDate" to state.currentDate
                    )
                    db.collection("users").document(uid).collection("streaks").document("current").set(streakData).await()
                }
            } catch (_: Exception) {}
        }
    }
}
