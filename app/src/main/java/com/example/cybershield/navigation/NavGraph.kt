package com.example.cybershield.navigation

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
import com.example.cybershield.core.theme.*
import com.example.cybershield.ui.auth.AuthViewModel
import com.example.cybershield.ui.auth.LoginScreen
import com.example.cybershield.ui.auth.ProfileScreen
import com.example.cybershield.ui.auth.RegisterScreen
import com.example.cybershield.ui.auth.SplashScreen
import com.example.cybershield.ui.dashboard.*
import com.example.cybershield.ui.password.*
import com.example.cybershield.ui.phishing.*
import com.example.cybershield.ui.quiz_history.*

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
            // Splash & Auth
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

            // Bottom Nav: Home
            composable(Screen.Home.route) {
                HomeScreen(
                    onNavigateToUrlScanner = { navController.navigate(Screen.Phishing.route) },
                    onNavigateToMessageScam = { navController.navigate(Screen.MessageScam.route) },
                    onNavigateToQrScanner = { navController.navigate(Screen.QrScanner.route) },
                    onNavigateToPasswordChecker = { navController.navigate(Screen.Password.route) },
                    onNavigateToPasswordGenerator = { navController.navigate(Screen.PasswordGenerator.route) },
                    onNavigateToEmailBreach = { navController.navigate(Screen.EmailBreach.route) },
                    onNavigateToDailyChallenge = { navController.navigate(Screen.DailyChallenge.route) },
                    onNavigateToPhishingSim = { navController.navigate(Screen.PhishingSim.route) },
                    onNavigateToQuiz = { navController.navigate(Screen.Quiz.route) },
                    onNavigateToSecurityScore = { navController.navigate(Screen.SecurityScore.route) },
                    onNavigateToHistory = { navController.navigate(Screen.History.route) }
                )
            }

            // Bottom Nav: Protect (Protection Center)
            composable(Screen.Protect.route) {
                ProtectionCenterScreen(
                    onNavigateToUrlScanner = { navController.navigate(Screen.Phishing.route) },
                    onNavigateToMessageScam = { navController.navigate(Screen.MessageScam.route) },
                    onNavigateToQrScanner = { navController.navigate(Screen.QrScanner.route) },
                    onNavigateToPasswordChecker = { navController.navigate(Screen.Password.route) },
                    onNavigateToPasswordGenerator = { navController.navigate(Screen.PasswordGenerator.route) },
                    onNavigateToEmailBreach = { navController.navigate(Screen.EmailBreach.route) },
                    onNavigateToSecurityScore = { navController.navigate(Screen.SecurityScore.route) },
                    onNavigateToIncidentGuide = { navController.navigate(Screen.IncidentGuide.route) }
                )
            }

            // Bottom Nav: Learn (Awareness Hub)
            composable(Screen.Learn.route) {
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

            // Tool Routes
            composable(Screen.Phishing.route) {
                PhishingScanScreen()
            }

            composable(Screen.MessageScam.route) {
                MessageScamScreen(onBackClick = { navController.popBackStack() })
            }

            composable(Screen.QrScanner.route) {
                QrScannerScreen(onBackClick = { navController.popBackStack() })
            }

            composable(Screen.Password.route) {
                PasswordCheckerScreen()
            }

            composable(Screen.PasswordGenerator.route) {
                PasswordGeneratorScreen(onBackClick = { navController.popBackStack() })
            }

            composable(Screen.EmailBreach.route) {
                EmailBreachScreen(onBackClick = { navController.popBackStack() })
            }

            composable(Screen.DailyChallenge.route) {
                DailyChallengeScreen(onBackClick = { navController.popBackStack() })
            }

            composable(Screen.PhishingSim.route) {
                PhishingSimulationScreen(onBackClick = { navController.popBackStack() })
            }

            composable(Screen.Quiz.route) {
                QuizScreen(
                    onNavigateToHistory = { navController.navigate(Screen.History.route) }
                )
            }

            composable(
                route = Screen.QuizResults.route,
                arguments = listOf(
                    navArgument("score") { type = NavType.IntType },
                    navArgument("total") { type = NavType.IntType }
                )
            ) { entry ->
                val score = entry.arguments?.getInt("score") ?: 0
                val total = entry.arguments?.getInt("total") ?: 5
                QuizResultsScreen(
                    score = score,
                    total = total,
                    onRetakeQuiz = {
                        navController.navigate(Screen.Quiz.route) {
                            popUpTo(Screen.Quiz.route) { inclusive = true }
                        }
                    },
                    onNavigateHome = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Home.route) { inclusive = true }
                        }
                    }
                )
            }

            composable(Screen.SecurityScore.route) {
                SecurityScoreScreen(onBackClick = { navController.popBackStack() })
            }

            composable(Screen.History.route) {
                ScanHistoryScreen(
                    onBackClick = { navController.popBackStack() }
                )
            }

            composable(Screen.IncidentGuide.route) {
                IncidentGuideScreen(onBackClick = { navController.popBackStack() })
            }
        }
    }
}
