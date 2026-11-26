package com.example.buildthon.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.buildthon.core.theme.*
import com.example.buildthon.ui.auth.AuthViewModel
import com.example.buildthon.ui.auth.LoginScreen
import com.example.buildthon.ui.auth.ProfileScreen
import com.example.buildthon.ui.auth.RegisterScreen
import com.example.buildthon.ui.auth.SplashScreen
import com.example.buildthon.ui.dashboard.AwarenessDetailScreen
import com.example.buildthon.ui.dashboard.AwarenessListScreen
import com.example.buildthon.ui.dashboard.HomeScreen
import com.example.buildthon.ui.password.PasswordCheckerScreen
import com.example.buildthon.ui.phishing.PhishingScanScreen
import com.example.buildthon.ui.quiz_history.QuizScreen
import com.example.buildthon.ui.quiz_history.ScanHistoryScreen

@Composable
fun CyberShieldNavGraph(
    navController: NavHostController = rememberNavController()
) {
    val authViewModel: AuthViewModel = viewModel()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val isBottomBarVisible = Screen.bottomBarScreens.any { it.route == currentRoute }

    Scaffold(
        containerColor = DarkBackground,
        bottomBar = {
            if (isBottomBarVisible) {
                NavigationBar(
                    containerColor = DarkCardSurface,
                    contentColor = PrimaryBlue
                ) {
                    Screen.bottomBarScreens.forEach { screen ->
                        val isSelected = currentRoute == screen.route
                        NavigationBarItem(
                            selected = isSelected,
                            onClick = {
                                if (currentRoute != screen.route) {
                                    navController.navigate(screen.route) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            },
                            icon = {
                                screen.icon?.let { icon ->
                                    Icon(
                                        imageVector = icon,
                                        contentDescription = screen.title,
                                        tint = if (isSelected) PrimaryBlue else TextMuted
                                    )
                                }
                            },
                            label = {
                                Text(
                                    text = screen.title,
                                    color = if (isSelected) PrimaryBlue else TextMuted,
                                    style = MaterialTheme.typography.labelMedium
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                indicatorColor = DarkCardSurfaceVariant
                            )
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Splash.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            // Splash Destination
            composable(Screen.Splash.route) {
                SplashScreen(
                    authViewModel = authViewModel,
                    onNavigateToHome = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Splash.route) { inclusive = true }
                        }
                    },
                    onNavigateToLogin = {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(Screen.Splash.route) { inclusive = true }
                        }
                    }
                )
            }

            // Auth Destinations (Member 1)
            composable(Screen.Login.route) {
                LoginScreen(
                    authViewModel = authViewModel,
                    onLoginSuccess = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    },
                    onNavigateToRegister = {
                        navController.navigate(Screen.Register.route)
                    }
                )
            }

            composable(Screen.Register.route) {
                RegisterScreen(
                    authViewModel = authViewModel,
                    onRegisterSuccess = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Register.route) { inclusive = true }
                        }
                    },
                    onNavigateToLogin = {
                        navController.popBackStack()
                    }
                )
            }

            composable(Screen.Profile.route) {
                ProfileScreen(
                    authViewModel = authViewModel,
                    onLogout = {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }

            // Dashboard Destinations (Member 2)
            composable(Screen.Home.route) {
                HomeScreen(
                    onNavigateToScanner = { navController.navigate(Screen.Phishing.route) },
                    onNavigateToPassword = { navController.navigate(Screen.Password.route) },
                    onNavigateToQuiz = { navController.navigate(Screen.Quiz.route) },
                    onNavigateToAwareness = { navController.navigate(Screen.Awareness.route) },
                    onNavigateToHistory = { navController.navigate(Screen.History.route) }
                )
            }

            composable(Screen.Awareness.route) {
                AwarenessListScreen(
                    onArticleClick = { articleId ->
                        navController.navigate(Screen.AwarenessDetail.createRoute(articleId))
                    }
                )
            }

            composable(
                route = Screen.AwarenessDetail.route,
                arguments = listOf(navArgument("articleId") { type = NavType.StringType })
            ) { backStackEntry ->
                val articleId = backStackEntry.arguments?.getString("articleId") ?: "1"
                AwarenessDetailScreen(
                    articleId = articleId,
                    onBackClick = { navController.popBackStack() }
                )
            }

            // Phishing Destinations (Member 3)
            composable(Screen.Phishing.route) {
                PhishingScanScreen()
            }

            // Password Destinations (Member 4)
            composable(Screen.Password.route) {
                PasswordCheckerScreen()
            }

            // Quiz & History Destinations (Member 5)
            composable(Screen.Quiz.route) {
                QuizScreen(
                    onNavigateToHistory = { navController.navigate(Screen.History.route) }
                )
            }

            composable(Screen.History.route) {
                ScanHistoryScreen(
                    onBackClick = { navController.popBackStack() }
                )
            }
        }
    }
}
