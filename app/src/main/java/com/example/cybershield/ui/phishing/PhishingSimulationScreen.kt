package com.example.cybershield.ui.phishing

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.cybershield.core.components.*
import com.example.cybershield.core.theme.*

@Composable
fun PhishingSimulationScreen(
    onBackClick: () -> Unit,
    viewModel: PhishingSimulationViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        CyberTopBar(title = "Phishing Simulation", onBackClick = onBackClick)

        Spacer(modifier = Modifier.height(16.dp))

        if (uiState.isSimulationFinished) {
            // Completion Card
            CyberCard(borderColor = SuccessGreen) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(imageVector = Icons.Default.EmojiEvents, contentDescription = null, tint = SuccessGreen, modifier = Modifier.size(64.dp))
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = "SIMULATION COMPLETED!", style = MaterialTheme.typography.headlineMedium, color = TextPrimary, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Score: ${uiState.correctCount} / ${uiState.scenarios.size} Correct (+120 XP)", style = MaterialTheme.typography.titleMedium, color = SuccessGreen)
                    Spacer(modifier = Modifier.height(24.dp))
                    CyberButton(
                        text = "RESTART SIMULATION",
                        icon = Icons.Default.Refresh,
                        onClick = { viewModel.restartSimulation() }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    CyberOutlinedButton(text = "BACK TO HOME", onClick = onBackClick)
                }
            }
        } else {
            val scenario = uiState.scenarios[uiState.currentIndex]

            // Progress Card
            CyberCard(borderColor = PrimaryBlue) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Psychology, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "Scenario ${uiState.currentIndex + 1} of ${uiState.scenarios.size}", style = MaterialTheme.typography.titleMedium, color = TextPrimary, fontWeight = FontWeight.Bold)
                    }
                    CyberBadge(status = CyberStatus.INFO, label = scenario.title)
                }

                Spacer(modifier = Modifier.height(12.dp))
                LinearProgressIndicator(
                    progress = { (uiState.currentIndex + 1).toFloat() / uiState.scenarios.size },
                    modifier = Modifier.fillMaxWidth().height(6.dp),
                    color = PrimaryBlue,
                    trackColor = DarkCardSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Simulated Email / Message Card
            CyberCard(borderColor = BorderColor) {
                Text(text = "From: ${scenario.sender}", style = MaterialTheme.typography.bodyMedium, color = WarningOrange, fontWeight = FontWeight.Bold)
                Text(text = "Subject: ${scenario.subject}", style = MaterialTheme.typography.titleMedium, color = TextPrimary, fontWeight = FontWeight.Bold)

                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(color = BorderColor)
                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = scenario.body,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (!uiState.isAnswerSubmitted) {
                Text(text = "Is this message Legitimate or a Phishing Scam?", style = MaterialTheme.typography.titleMedium, color = TextPrimary)
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        CyberButton(
                            text = "LEGITIMATE",
                            containerColor = SuccessGreen,
                            onClick = { viewModel.submitAnswer(false) }
                        )
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        CyberButton(
                            text = "PHISHING SCAM",
                            containerColor = DangerRed,
                            onClick = { viewModel.submitAnswer(true) }
                        )
                    }
                }
            } else {
                val userSaysPhishing = uiState.selectedChoice == true
                val isCorrect = userSaysPhishing == scenario.isPhishing
                val bannerColor = if (isCorrect) SuccessGreen else DangerRed

                CyberCard(borderColor = bannerColor) {
                    Text(
                        text = if (isCorrect) "🎉 Correct Decision!" else "❌ Incorrect Decision!",
                        style = MaterialTheme.typography.titleLarge,
                        color = bannerColor,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Actual Status: ${if (scenario.isPhishing) "Phishing Scam" else "Legitimate Message"}",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPrimary
                    )

                    if (scenario.redFlags.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(text = "Highlighted Warning Signs:", style = MaterialTheme.typography.titleSmall, color = WarningOrange, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(4.dp))
                        scenario.redFlags.forEach { flag ->
                            Row(modifier = Modifier.padding(vertical = 2.dp)) {
                                Text(text = "• ", color = WarningOrange, fontWeight = FontWeight.Bold)
                                Text(text = flag, style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Text(text = "Explanation:", style = MaterialTheme.typography.titleSmall, color = PrimaryBlue, fontWeight = FontWeight.Bold)
                    Text(text = scenario.explanation, style = MaterialTheme.typography.bodyMedium, color = TextSecondary)

                    Spacer(modifier = Modifier.height(12.dp))
                    Text(text = "Security Recommendation:", style = MaterialTheme.typography.titleSmall, color = SuccessGreen, fontWeight = FontWeight.Bold)
                    Text(text = scenario.recommendation, style = MaterialTheme.typography.bodyMedium, color = TextSecondary)

                    Spacer(modifier = Modifier.height(20.dp))
                    CyberButton(
                        text = if (uiState.currentIndex < uiState.scenarios.size - 1) "NEXT SCENARIO" else "FINISH SIMULATION",
                        onClick = { viewModel.nextScenario() },
                        containerColor = PrimaryBlue
                    )
                }
            }
        }
    }
}
