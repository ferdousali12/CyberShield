package com.example.cybershield.ui.quiz_history

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.cybershield.core.components.*
import com.example.cybershield.core.theme.*

@Composable
fun QuizScreen(
    onNavigateToHistory: () -> Unit = {},
    viewModel: QuizViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    if (uiState.isQuizFinished) {
        QuizResultScreen(
            correctCount = uiState.correctCount,
            incorrectCount = uiState.incorrectCount,
            totalQuestions = uiState.questions.size,
            percentageScore = uiState.percentageScore,
            onRestart = { viewModel.startQuiz() },
            onViewHistory = onNavigateToHistory
        )
    } else if (uiState.questions.isNotEmpty()) {
        val currentQuestion = uiState.questions[uiState.currentIndex]
        val selectedOptionIndex = uiState.selectedAnswers[uiState.currentIndex]

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(DarkBackground)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            CyberTopBar(title = "Cybersecurity Challenge Quiz")

            Spacer(modifier = Modifier.height(16.dp))

            // Progress Header Card
            CyberCard(borderColor = PrimaryBlue) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Quiz, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(28.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Question ${uiState.currentIndex + 1} of ${uiState.questions.size}",
                            style = MaterialTheme.typography.titleMedium,
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    CyberBadge(status = CyberStatus.INFO, label = "Active Quiz")
                }

                Spacer(modifier = Modifier.height(16.dp))

                LinearProgressIndicator(
                    progress = { (uiState.currentIndex + 1).toFloat() / uiState.questions.size },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp),
                    color = PrimaryBlue,
                    trackColor = DarkCardSurfaceVariant
                )

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = currentQuestion.questionText,
                    style = MaterialTheme.typography.titleLarge,
                    color = TextPrimary
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Options List
                currentQuestion.options.forEachIndexed { index, option ->
                    val isSelected = selectedOptionIndex == index
                    val isCorrect = index == currentQuestion.correctAnswerIndex
                    val isSubmitted = uiState.isAnswerSubmitted

                    val borderColor = when {
                        isSubmitted && isCorrect -> SuccessGreen
                        isSubmitted && isSelected && !isCorrect -> DangerRed
                        isSelected -> PrimaryBlue
                        else -> BorderColor
                    }

                    val containerColor = when {
                        isSubmitted && isCorrect -> SuccessGreenBg.copy(alpha = 0.3f)
                        isSubmitted && isSelected && !isCorrect -> DangerRedBg.copy(alpha = 0.3f)
                        isSelected -> DarkCardSurfaceVariant
                        else -> DarkCardSurface
                    }

                    OutlinedButton(
                        onClick = { viewModel.selectOption(index) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, borderColor),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = containerColor,
                            contentColor = TextPrimary
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = option,
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.weight(1f)
                            )
                            if (isSubmitted && isCorrect) {
                                Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = SuccessGreen)
                            }
                        }
                    }
                }

                if (uiState.isAnswerSubmitted) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Card(
                        colors = CardDefaults.cardColors(containerColor = DarkCardSurfaceVariant),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Icon(imageVector = Icons.Default.Info, contentDescription = null, tint = AccentCyan, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "Explanation:",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = AccentCyan,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = currentQuestion.explanation,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = TextSecondary,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                if (!uiState.isAnswerSubmitted) {
                    CyberButton(
                        text = "SUBMIT ANSWER",
                        onClick = { viewModel.submitAnswer() },
                        enabled = selectedOptionIndex != null
                    )
                } else {
                    CyberButton(
                        text = if (uiState.currentIndex < uiState.questions.size - 1) "NEXT QUESTION" else "VIEW FINAL RESULTS",
                        onClick = { viewModel.nextQuestion() },
                        containerColor = SuccessGreen
                    )
                }
            }
        }
    }
}

@Composable
fun QuizResultScreen(
    correctCount: Int,
    incorrectCount: Int,
    totalQuestions: Int,
    percentageScore: Int,
    onRestart: () -> Unit,
    onViewHistory: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CyberTopBar(title = "Quiz Results")

        Spacer(modifier = Modifier.height(24.dp))

        CyberCard(borderColor = if (percentageScore >= 70) SuccessGreen else WarningOrange) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "CHALLENGE COMPLETED",
                    fontSize = 12.sp,
                    color = AccentCyan,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "$percentageScore%",
                    style = MaterialTheme.typography.headlineLarge.copy(fontSize = 56.sp),
                    color = if (percentageScore >= 70) SuccessGreen else WarningOrange,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = if (percentageScore >= 70) "Outstanding Security Awareness!" else "Keep Learning & Try Again!",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary
                )

                Spacer(modifier = Modifier.height(24.dp))
                HorizontalDivider(color = BorderColor)
                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    ScoreStatBox(label = "Total", value = "$totalQuestions", color = PrimaryBlue)
                    ScoreStatBox(label = "Correct", value = "$correctCount", color = SuccessGreen)
                    ScoreStatBox(label = "Incorrect", value = "$incorrectCount", color = DangerRed)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        CyberButton(
            text = "RESTART QUIZ",
            icon = Icons.Default.Refresh,
            onClick = onRestart,
            containerColor = PrimaryBlue
        )

        Spacer(modifier = Modifier.height(12.dp))

        CyberOutlinedButton(
            text = "VIEW SCAN HISTORY",
            onClick = onViewHistory
        )
    }
}

@Composable
fun ScoreStatBox(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value, style = MaterialTheme.typography.headlineMedium, color = color, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = label, style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
    }
}
