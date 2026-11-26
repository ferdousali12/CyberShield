package com.example.buildthon.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val title: String, val icon: ImageVector? = null) {
    // Auth Routes
    object Splash : Screen("splash", "Splash")
    object Login : Screen("login", "Login")
    object Register : Screen("register", "Register")

    // Main Bottom Navigation Routes (Home, Learn, History, Profile)
    object Home : Screen("home", "Home", Icons.Default.Home)
    object Awareness : Screen("awareness", "Learn", Icons.Default.MenuBook)
    object AwarenessDetail : Screen("awareness_detail/{articleId}", "Article Detail") {
        fun createRoute(articleId: String) = "awareness_detail/$articleId"
    }
    object History : Screen("history", "History", Icons.Default.History)
    object Profile : Screen("profile", "Profile", Icons.Default.Person)

    // Secondary Feature Routes (Accessible from Dashboard Cards)
    object Phishing : Screen("phishing", "Scanner", Icons.Default.Search)
    object Password : Screen("password", "Password", Icons.Default.Lock)
    object Quiz : Screen("quiz", "Quiz", Icons.Default.Quiz)

    companion object {
        val bottomBarScreens = listOf(Home, Awareness, History, Profile)
    }
}
