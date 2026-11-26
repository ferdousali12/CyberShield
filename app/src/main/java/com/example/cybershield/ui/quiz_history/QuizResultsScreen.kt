package com.example.cybershield.ui.quiz_history

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cybershield.core.components.*
import com.example.cybershield.core.theme.*

@Composable
fun QuizResultsScreen(
    score: Int,
    total: Int,
    onRetakeQuiz: () -> Unit,
    onNavigateHome: () -> Unit
) {
    val percentage = if (total > 0) (score * 100) / total else 0
    val status = when {
        percentage >= 80 -> CyberStatus.SAFE
        percentage >= 50 -> CyberStatus.WARNING
        else -> CyberStatus.DANGER
    }

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

        Box(
            modifier = Modifier
                .size(110.dp)
                .background(DarkCardSurfaceVariant, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = Icons.Default.EmojiEvents, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(56.dp))
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "$score / $total Correct",
            style = MaterialTheme.typography.headlineLarge,
            color = TextPrimary,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        CyberBadge(status = status, label = "$percentage% Score")

        Spacer(modifier = Modifier.height(24.dp))

        CyberCard(borderColor = PrimaryBlue) {
            Text(
                text = "Performance Summary",
                style = MaterialTheme.typography.titleMedium,
                color = TextPrimary,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = when {
                    percentage >= 80 -> "Outstanding performance! You possess elite threat awareness and security hygiene."
                    percentage >= 50 -> "Good understanding of core concepts, with room to sharpen phishing detection skills."
                    else -> "Review the Awareness Hub articles to reinforce your cybersecurity fundamentals."
                },
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        CyberButton(
            text = "RETAKE QUIZ",
            icon = Icons.Default.Refresh,
            onClick = onRetakeQuiz
        )

        Spacer(modifier = Modifier.height(12.dp))

        CyberOutlinedButton(
            text = "RETURN TO HOME",
            icon = Icons.Default.Home,
            onClick = onNavigateHome
        )
    }
}
