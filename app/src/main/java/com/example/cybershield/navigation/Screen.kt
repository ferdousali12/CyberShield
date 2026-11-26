package com.example.cybershield.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val title: String, val icon: ImageVector? = null) {
    // Auth Routes
    object Splash : Screen("splash", "Splash")
    object Login : Screen("login", "Login")
    object Register : Screen("register", "Register")

    // Main 4 Bottom Navigation Routes
    object Home : Screen("home", "Home", Icons.Default.Home)
    object Protect : Screen("protect", "Protect", Icons.Default.Security)
    object Learn : Screen("learn", "Learn", Icons.Default.MenuBook)
    object Profile : Screen("profile", "Profile", Icons.Default.Person)

    // Secondary Feature & Tool Routes
    object Phishing : Screen("phishing", "URL Scanner", Icons.Default.Language)
    object MessageScam : Screen("message_scam", "Message Scam Detector", Icons.AutoMirrored.Filled.Chat)
    object QrScanner : Screen("qr_scanner", "QR Scanner", Icons.Default.QrCodeScanner)
    object Password : Screen("password", "Password Checker", Icons.Default.Lock)
    object PasswordGenerator : Screen("password_generator", "Password Generator", Icons.Default.VpnKey)
    object EmailBreach : Screen("email_breach", "Email Breach Checker", Icons.Default.MarkEmailRead)
    object AwarenessDetail : Screen("awareness_detail/{articleId}", "Article Detail") {
        fun createRoute(articleId: String) = "awareness_detail/$articleId"
    }
    object DailyChallenge : Screen("daily_challenge", "Daily Challenge", Icons.Default.EmojiEvents)
    object PhishingSim : Screen("phishing_sim", "Phishing Simulation", Icons.Default.Psychology)
    object Quiz : Screen("quiz", "Cyber Quiz", Icons.Default.Quiz)
    object QuizResults : Screen("quiz_results/{score}/{total}", "Quiz Results") {
        fun createRoute(score: Int, total: Int) = "quiz_results/$score/$total"
    }
    object SecurityScore : Screen("security_score", "Security Score", Icons.Default.Speed)
    object History : Screen("history", "Scan History", Icons.Default.History)
    object IncidentGuide : Screen("incident_guide", "Incident Guide", Icons.Default.SupportAgent)

    companion object {
        val bottomBarScreens = listOf(Home, Protect, Learn, Profile)
    }
}
