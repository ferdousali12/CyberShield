package com.example.cybershield.ui.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.cybershield.core.components.*
import com.example.cybershield.core.theme.*

@Composable
fun HomeScreen(
    onNavigateToUrlScanner: () -> Unit,
    onNavigateToMessageScam: () -> Unit,
    onNavigateToQrScanner: () -> Unit,
    onNavigateToPasswordChecker: () -> Unit,
    onNavigateToPasswordGenerator: () -> Unit,
    onNavigateToEmailBreach: () -> Unit,
    onNavigateToDailyChallenge: () -> Unit,
    onNavigateToPhishingSim: () -> Unit,
    onNavigateToQuiz: () -> Unit,
    onNavigateToSecurityScore: () -> Unit,
    onNavigateToHistory: () -> Unit,
    viewModel: HomeViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // 1. Personalized Greeting Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Welcome back,",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
                Text(
                    text = uiState.userName,
                    style = MaterialTheme.typography.headlineMedium,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                )
            }
            CyberBadge(
                status = if (uiState.securityScore >= 75) CyberStatus.SAFE else CyberStatus.WARNING,
                label = if (uiState.securityScore >= 75) "Protected" else "At Risk"
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = PrimaryBlue)
            }
        } else if (uiState.errorMessage != null) {
            CyberCard(borderColor = DangerRed) {
                Text(text = "Error: ${uiState.errorMessage}", color = DangerRed)
                Spacer(modifier = Modifier.height(8.dp))
                CyberButton(text = "RETRY", onClick = { viewModel.loadDashboardData() })
            }
        } else {
            // 2. Personal Security Score with Circular Progress Indicator
            CyberCard(
                modifier = Modifier.clickable { onNavigateToSecurityScore() },
                borderColor = PrimaryBlue
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "PERSONAL SECURITY SCORE",
                            fontSize = 11.sp,
                            color = PrimaryBlue,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${uiState.securityScore} / 100",
                            style = MaterialTheme.typography.headlineLarge,
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Tap to view deep device audit & recommendations",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary,
                            fontSize = 12.sp
                        )
                    }

                    // Circular Progress Indicator
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.size(72.dp)
                    ) {
                        CircularProgressIndicator(
                            progress = { uiState.securityScore / 100f },
                            modifier = Modifier.size(64.dp),
                            color = if (uiState.securityScore >= 75) SuccessGreen else WarningOrange,
                            trackColor = DarkCardSurfaceVariant,
                            strokeWidth = 6.dp
                        )
                        Text(
                            text = "${uiState.securityScore}%",
                            style = MaterialTheme.typography.titleMedium,
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 3. Quick Action Cards (Scan URL, Message Detector, QR Scanner, Password Checker)
            Text(
                text = "Quick Protection Actions",
                style = MaterialTheme.typography.titleLarge,
                color = TextPrimary
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    QuickModuleCard(
                        title = "Scan URL",
                        subtitle = "Detect phishing links",
                        icon = Icons.Default.Language,
                        color = PrimaryBlue,
                        onClick = onNavigateToUrlScanner
                    )
                }
                Box(modifier = Modifier.weight(1f)) {
                    QuickModuleCard(
                        title = "Message Detector",
                        subtitle = "Inspect SMS / chat",
                        icon = Icons.AutoMirrored.Filled.Chat,
                        color = WarningOrange,
                        onClick = onNavigateToMessageScam
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    QuickModuleCard(
                        title = "QR Scanner",
                        subtitle = "Verify QR codes",
                        icon = Icons.Default.QrCodeScanner,
                        color = AccentCyan,
                        onClick = onNavigateToQrScanner
                    )
                }
                Box(modifier = Modifier.weight(1f)) {
                    QuickModuleCard(
                        title = "Password Checker",
                        subtitle = "Entropy & strength",
                        icon = Icons.Default.Lock,
                        color = SuccessGreen,
                        onClick = onNavigateToPasswordChecker
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 4. Daily Cyber Challenge Card
            CyberCard(
                modifier = Modifier.clickable { onNavigateToDailyChallenge() },
                borderColor = AccentCyan
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.EmojiEvents,
                        contentDescription = null,
                        tint = AccentCyan,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "DAILY CYBER CHALLENGE",
                            fontSize = 11.sp,
                            color = AccentCyan,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Complete today's security scenario & earn +50 XP",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextPrimary
                        )
                    }
                    Icon(imageVector = Icons.Default.ChevronRight, contentDescription = null, tint = TextMuted)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 5. Daily Security Tip
            CyberCard(borderColor = BorderColor) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Lightbulb,
                        contentDescription = null,
                        tint = WarningOrange,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "DAILY SECURITY TIP",
                            fontSize = 11.sp,
                            color = WarningOrange,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = uiState.dailyTip,
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 6. Recent Security Activity
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Recent Security Activity",
                    style = MaterialTheme.typography.titleLarge,
                    color = TextPrimary
                )
                TextButton(onClick = onNavigateToHistory) {
                    Text(text = "View All", color = PrimaryBlue)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (uiState.recentScans.isEmpty()) {
                CyberCard(borderColor = BorderColor) {
                    Text(
                        text = "No recent scan history found. Run a URL scan or password check to populate telemetry.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )
                }
            } else {
                uiState.recentScans.forEach { scan ->
                    val isSafe = scan.result.equals("Safe", ignoreCase = true)
                    val badgeStatus = if (isSafe) CyberStatus.SAFE else CyberStatus.DANGER
                    val statusColor = if (isSafe) SuccessGreen else DangerRed

                    Spacer(modifier = Modifier.height(8.dp))
                    CyberCard(borderColor = statusColor.copy(alpha = 0.5f)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                Icon(
                                    imageVector = if (isSafe) Icons.Default.Security else Icons.Default.Warning,
                                    contentDescription = null,
                                    tint = statusColor,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = scan.target,
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = TextPrimary,
                                        fontWeight = FontWeight.Bold,
                                        maxLines = 1
                                    )
                                    Text(
                                        text = "${scan.type} • ${scan.result}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = TextSecondary,
                                        fontSize = 12.sp
                                    )
                                }
                            }
                            CyberBadge(status = badgeStatus, label = scan.result)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 7. Latest Cybersecurity Alerts
            Text(
                text = "Latest Cybersecurity Alerts",
                style = MaterialTheme.typography.titleLarge,
                color = TextPrimary
            )

            Spacer(modifier = Modifier.height(8.dp))

            uiState.latestAlerts.forEach { alert ->
                Spacer(modifier = Modifier.height(8.dp))
                CyberCard(borderColor = DangerRed.copy(alpha = 0.6f)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Warning,
                                    contentDescription = null,
                                    tint = DangerRed,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = alert.title,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = TextPrimary,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = alert.description,
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextSecondary,
                                fontSize = 12.sp
                            )
                        }
                        CyberBadge(status = CyberStatus.DANGER, label = alert.severity)
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun QuickModuleCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    color: Color,
    onClick: () -> Unit
) {
    CyberCard(
        modifier = Modifier.clickable { onClick() },
        borderColor = BorderColor
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(32.dp))
        Spacer(modifier = Modifier.height(12.dp))
        Text(text = title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = TextPrimary)
        Spacer(modifier = Modifier.height(2.dp))
        Text(text = subtitle, style = MaterialTheme.typography.bodyMedium, color = TextSecondary, fontSize = 11.sp)
    }
}
