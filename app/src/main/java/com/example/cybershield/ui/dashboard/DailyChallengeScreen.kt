package com.example.cybershield.ui.dashboard

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
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
fun DailyChallengeScreen(
    onBackClick: () -> Unit,
    viewModel: DailyChallengeViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        CyberTopBar(title = "Daily Cyber Challenge", onBackClick = onBackClick)

        Spacer(modifier = Modifier.height(16.dp))

        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = PrimaryBlue)
            }
        } else if (uiState.errorMessage != null) {
            CyberCard(borderColor = DangerRed) {
                Text(text = "Error: ${uiState.errorMessage}", color = DangerRed)
                Spacer(modifier = Modifier.height(8.dp))
                CyberButton(text = "RETRY", onClick = { viewModel.loadTodayChallenge() })
            }
        } else {
            // Streak Header Card
            CyberCard(borderColor = AccentCyan) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.EmojiEvents, contentDescription = null, tint = AccentCyan, modifier = Modifier.size(32.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "STREAK: ${uiState.streakCount} DAYS ACTIVE", fontSize = 11.sp, color = AccentCyan, fontWeight = FontWeight.Bold)
                        Text(text = "Date: ${uiState.currentDate} • Earn +50 XP", style = MaterialTheme.typography.titleMedium, color = TextPrimary)
                    }
                    if (uiState.alreadyCompletedToday) {
                        CyberBadge(status = CyberStatus.SAFE, label = "Claimed")
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            uiState.question?.let { question ->
                CyberCard(borderColor = PrimaryBlue) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = question.category.uppercase(), fontSize = 11.sp, color = PrimaryBlue, fontWeight = FontWeight.Bold)
                        if (uiState.alreadyCompletedToday) {
                            Text(text = "Completed for today", color = SuccessGreen, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = question.scenario,
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPrimary
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    question.options.forEachIndexed { index, optionText ->
                        val isSelected = uiState.selectedOption == index
                        val isCorrectOption = index == question.correctIndex

                        val borderColor = when {
                            uiState.isSubmitted && isCorrectOption -> SuccessGreen
                            uiState.isSubmitted && isSelected -> DangerRed
                            isSelected -> PrimaryBlue
                            else -> BorderColor
                        }

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clickable(enabled = !uiState.isSubmitted && !uiState.alreadyCompletedToday) {
                                    viewModel.selectOption(index)
                                },
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) DarkCardSurfaceVariant else DarkCardSurface
                            ),
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.dp, borderColor)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = isSelected,
                                    onClick = {
                                        if (!uiState.isSubmitted && !uiState.alreadyCompletedToday) {
                                            viewModel.selectOption(index)
                                        }
                                    },
                                    colors = RadioButtonDefaults.colors(selectedColor = PrimaryBlue)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(text = optionText, style = MaterialTheme.typography.bodyMedium, color = TextPrimary)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    if (!uiState.isSubmitted && !uiState.alreadyCompletedToday) {
                        CyberButton(
                            text = "SUBMIT ANSWER",
                            enabled = uiState.selectedOption != null,
                            onClick = { viewModel.submitAnswer() }
                        )
                    }

                    if (uiState.isSubmitted) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = if (uiState.isCorrect) SuccessGreen.copy(alpha = 0.15f) else DangerRed.copy(alpha = 0.15f)
                            ),
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, if (uiState.isCorrect) SuccessGreen else DangerRed)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = if (uiState.isCorrect) "🎉 Correct! +50 XP reward credited." else "❌ Incorrect. Review the security rationale below.",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = if (uiState.isCorrect) SuccessGreen else DangerRed,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "Explanation: ${question.explanation}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = TextSecondary
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        CyberOutlinedButton(text = "BACK TO HOME", onClick = onBackClick)
                    }
                }
            }
        }
    }
}
