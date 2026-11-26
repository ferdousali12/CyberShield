package com.example.cybershield.ui.phishing

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class PhishingScenario(
    val id: String,
    val title: String,
    val sender: String,
    val subject: String,
    val body: String,
    val isPhishing: Boolean,
    val redFlags: List<String>,
    val explanation: String,
    val recommendation: String
)

class PhishingSimulationViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(PhishingSimulationUiState())
    val uiState: StateFlow<PhishingSimulationUiState> = _uiState.asStateFlow()

    fun submitAnswer(userSaysPhishing: Boolean) {
        val state = _uiState.value
        if (state.isAnswerSubmitted || state.currentIndex >= state.scenarios.size) return

        val currentScenario = state.scenarios[state.currentIndex]
        val isCorrect = userSaysPhishing == currentScenario.isPhishing
        val newCorrectCount = if (isCorrect) state.correctCount + 1 else state.correctCount

        _uiState.update {
            it.copy(
                selectedChoice = userSaysPhishing,
                isAnswerSubmitted = true,
                correctCount = newCorrectCount
            )
        }
    }

    fun nextScenario() {
        val state = _uiState.value
        val nextIndex = state.currentIndex + 1
        if (nextIndex < state.scenarios.size) {
            _uiState.update {
                it.copy(
                    currentIndex = nextIndex,
                    selectedChoice = null,
                    isAnswerSubmitted = false
                )
            }
        } else {
            _uiState.update { it.copy(isSimulationFinished = true) }
        }
    }

    fun restartSimulation() {
        _uiState.update {
            PhishingSimulationUiState(
                scenarios = it.scenarios.shuffled()
            )
        }
    }
}

data class PhishingSimulationUiState(
    val scenarios: List<PhishingScenario> = listOf(
        PhishingScenario(
            id = "s1",
            title = "Fake Bank Security Alert",
            sender = "security@paypa1-support.com",
            subject = "URGENT: Unusual login attempt detected",
            body = "Dear Customer,\n\nWe detected a login attempt from an unrecognized device in Eastern Europe. Click the link below within 24 hours to verify your credentials or your account will be permanently suspended.\n\n[ VERIFY ACCOUNT NOW ]",
            isPhishing = true,
            redFlags = listOf(
                "Typosquatting domain ('paypa1-support.com' with number '1' instead of 'l').",
                "High urgency and threat of permanent account suspension.",
                "Generic salutation ('Dear Customer')."
            ),
            explanation = "This is a classic credential-harvesting phishing email designed to induce panic and force rushed clicks.",
            recommendation = "Never click links in urgent security emails. Open your banking app or official bookmarked website directly."
        ),
        PhishingScenario(
            id = "s2",
            title = "Social Media Security Notification",
            sender = "no-reply@instagram.com",
            subject = "Your password was changed successfully",
            body = "Hi alex_dev,\n\nThe password for your Instagram account was recently changed from a browser in New York, US.\n\nIf you made this change, you can safely ignore this email.\n\nIf you did NOT make this change, secure your account immediately by visiting: https://instagram.com/accounts/password/reset",
            isPhishing = false,
            redFlags = emptyList(),
            explanation = "This is a legitimate automated security notification from Instagram using official domains and secure paths.",
            recommendation = "Always inspect the sender domain carefully and verify HTTPS links before clicking."
        ),
        PhishingScenario(
            id = "s3",
            title = "Suspicious Delivery Notification",
            sender = "tracking@usps-package-delivery-update.xyz",
            subject = "Action Required: Package delivery failed due to incorrect address",
            body = "Your package (#US-89214) could not be delivered because your street number is missing. Pay $1.50 redelivery fee immediately to schedule shipment:\n\nhttp://usps-redelivery.xyz/track",
            isPhishing = true,
            redFlags = listOf(
                "Suspicious Top-Level Domain (.xyz) not used by official postal services.",
                "Request for a small redelivery fee via unsecured HTTP link.",
                "Unsolicited notification regarding a package you may not be expecting."
            ),
            explanation = "Package delivery phishing traps use small fees to harvest credit card numbers and personal details.",
            recommendation = "Check tracking numbers directly on official carrier websites (usps.com, ups.com, fedex.com)."
        ),
        PhishingScenario(
            id = "s4",
            title = "Fake Prize / Lottery Notification",
            sender = "winner@global-lottery-rewards.info",
            subject = "Congratulations! You won $5,000,000 in the International Sweepstakes",
            body = "You have been randomly selected as the grand prize winner! To claim your $5,000,000 cash prize, simply reply with your full name, phone number, and a $50 processing fee via gift card.",
            isPhishing = true,
            redFlags = listOf(
                "Unsolicited prize notification for a sweepstakes you never entered.",
                "Requirement to pay an upfront processing fee to receive money.",
                "Unprofessional sender domain (.info)."
            ),
            explanation = "Advance-fee scams promise huge rewards while demanding upfront payment or sensitive personal info.",
            recommendation = "Remember the golden rule: if it sounds too good to be true, it is a scam."
        )
    ),
    val currentIndex: Int = 0,
    val selectedChoice: Boolean? = null,
    val isAnswerSubmitted: Boolean = false,
    val correctCount: Int = 0,
    val isSimulationFinished: Boolean = false
)
