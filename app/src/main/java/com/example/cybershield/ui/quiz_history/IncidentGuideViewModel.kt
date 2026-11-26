package com.example.cybershield.ui.quiz_history

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class IncidentGuide(
    val id: String,
    val title: String,
    val potentialRisk: String,
    val immediateSteps: List<String>,
    val whatToAvoid: List<String>,
    val recoveryRecommendations: String,
    val reportingResource: String
)

data class IncidentGuideUiState(
    val guides: List<IncidentGuide> = listOf(
        IncidentGuide(
            id = "g1",
            title = "1. I clicked a suspicious link",
            potentialRisk = "Malware download, session hijacking, or redirection to a credential-harvesting phishing page.",
            immediateSteps = listOf(
                "Disconnect your device from Wi-Fi and mobile data immediately.",
                "Close your browser tab and clear browser cache and cookies.",
                "Run a system-level malware or antivirus scan."
            ),
            whatToAvoid = listOf(
                "Do not enter passwords, OTPs, or personal data on the opened page.",
                "Do not download any files, installers, or APKs offered on the site."
            ),
            recoveryRecommendations = "If you entered credentials, change your password immediately from a trusted device and enable Multi-Factor Authentication (2FA).",
            reportingResource = "Report phishing links to Google Safe Browsing or APWG (Antiphishing Working Group)."
        ),
        IncidentGuide(
            id = "g2",
            title = "2. My social media account was hacked",
            potentialRisk = "Unauthorized posting, direct message scams sent to friends and family, and loss of private photos.",
            immediateSteps = listOf(
                "Attempt to log in and use 'Forgot Password' to reset your credentials.",
                "Check account settings and remove unauthorized linked third-party apps or trusted devices.",
                "Notify your contacts that your account was temporarily compromised."
            ),
            whatToAvoid = listOf(
                "Do not pay ransoms or extortion demands from the hacker.",
                "Do not ignore security alert emails sent from the platform."
            ),
            recoveryRecommendations = "Submit an account recovery support ticket with official photo identification if supported by the platform.",
            reportingResource = "Use official platform support centers (Instagram Help, Facebook Help, X Support)."
        ),
        IncidentGuide(
            id = "g3",
            title = "3. I shared my password accidentally",
            potentialRisk = "Unauthorized access to your personal email, financial accounts, or cloud storage vaults.",
            immediateSteps = listOf(
                "Change the compromised password immediately on the affected service.",
                "Check if the same password was reused on other websites and change those immediately.",
                "Enable Two-Factor Authentication (2FA) across all critical accounts."
            ),
            whatToAvoid = listOf(
                "Do not wait to see if anything happens.",
                "Do not use easily guessable passwords for your new credentials."
            ),
            recoveryRecommendations = "Transition to a secure password manager to generate and store unique passwords for every account.",
            reportingResource = "Review recent login activity logs on sensitive accounts (Google, Apple, Microsoft)."
        ),
        IncidentGuide(
            id = "g4",
            title = "4. I received a fraudulent message",
            potentialRisk = "Social engineering, financial fraud, or malware installation via SMS, WhatsApp, or email.",
            immediateSteps = listOf(
                "Do not click any links or download attached files.",
                "Block the sender's phone number or account immediately.",
                "Report and delete the message."
            ),
            whatToAvoid = listOf(
                "Do not reply to the scammer or engage in conversation.",
                "Do not call phone numbers listed in suspicious text messages."
            ),
            recoveryRecommendations = "Use CyberShield's Message Scam Detector to verify similar messages in the future.",
            reportingResource = "Forward scam SMS to 7726 (SPAM) in supported regions or report to your mobile carrier."
        ),
        IncidentGuide(
            id = "g5",
            title = "5. I lost money through an online scam",
            potentialRisk = "Financial loss, identity theft, and recurring fraudulent charges on cards.",
            immediateSteps = listOf(
                "Contact your bank or credit card company immediately to freeze cards and dispute fraudulent charges.",
                "Change online banking passwords and transaction PINs.",
                "Gather all transaction receipts, chat logs, and emails as evidence."
            ),
            whatToAvoid = listOf(
                "Do not trust unofficial recovery services claiming they can retrieve lost crypto or funds for a fee (recovery scams).",
                "Do not delete conversation or receipt evidence."
            ),
            recoveryRecommendations = "File a formal report with local law enforcement and national consumer protection agencies.",
            reportingResource = "Report to the Federal Trade Commission (FTC) at reportfraud.ftc.gov or IC3 (Internet Crime Complaint Center)."
        ),
        IncidentGuide(
            id = "g6",
            title = "6. My email appeared in a data breach",
            potentialRisk = "Credential stuffing attacks where automated bots test your leaked password across other websites.",
            immediateSteps = listOf(
                "Use CyberShield's Email Breach Checker to identify which registries leaked your data.",
                "Change passwords on all critical accounts tied to that email address.",
                "Turn on Multi-Factor Authentication (MFA)."
            ),
            whatToAvoid = listOf(
                "Do not panic; a breach of your email address does not automatically mean your password was plaintext leaked.",
                "Do not ignore the warning."
            ),
            recoveryRecommendations = "Use unique, randomly generated passwords for every online service.",
            reportingResource = "Check HaveIBeenPwned or CyberShield Breach Intelligence."
        ),
        IncidentGuide(
            id = "g7",
            title = "7. I installed a suspicious application",
            potentialRisk = "Data exfiltration, background surveillance, unauthorized premium SMS charges, or ransomware.",
            immediateSteps = listOf(
                "Disconnect your device from the internet immediately.",
                "Go to device Settings > Apps and uninstall the suspicious application.",
                "Revoke any device administrator permissions granted to the app."
            ),
            whatToAvoid = listOf(
                "Do not open banking apps or enter sensitive passwords while the app is installed.",
                "Do not ignore unusual battery drain or data usage."
            ),
            recoveryRecommendations = "If malicious behavior persists, perform a factory reset of your device after backing up critical personal files.",
            reportingResource = "Report malicious apps on Google Play Protect or Apple App Store."
        )
    ),
    val expandedGuideId: String? = null
)

class IncidentGuideViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(IncidentGuideUiState())
    val uiState: StateFlow<IncidentGuideUiState> = _uiState.asStateFlow()

    fun toggleExpand(guideId: String) {
        _uiState.update {
            it.copy(
                expandedGuideId = if (it.expandedGuideId == guideId) null else guideId
            )
        }
    }
}
