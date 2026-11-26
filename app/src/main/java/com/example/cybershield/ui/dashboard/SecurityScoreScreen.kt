package com.example.cybershield.ui.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Warning
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
fun SecurityScoreScreen(
    onBackClick: () -> Unit,
    viewModel: SecurityScoreViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CyberTopBar(title = "Personal Security Score", onBackClick = onBackClick)

        Spacer(modifier = Modifier.height(16.dp))

        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = PrimaryBlue)
            }
        } else if (uiState.errorMessage != null) {
            CyberCard(borderColor = DangerRed) {
                Text(text = "Error: ${uiState.errorMessage}", color = DangerRed)
                Spacer(modifier = Modifier.height(8.dp))
                CyberButton(text = "RETRY", onClick = { viewModel.loadSecurityScore() })
            }
        } else {
            val statusColor = when (uiState.category) {
                ScoreCategory.EXCELLENT -> SuccessGreen
                ScoreCategory.GOOD -> PrimaryBlue
                ScoreCategory.BASIC -> WarningOrange
                ScoreCategory.NEEDS_IMPROVEMENT -> DangerRed
            }

            // Overall Score Card with Circular Indicator
            CyberCard(borderColor = statusColor) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "SECURITY RATING: ${uiState.category.label.uppercase()}",
                            fontSize = 11.sp,
                            color = statusColor,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${uiState.overallScore} / 100",
                            style = MaterialTheme.typography.headlineLarge,
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = when (uiState.category) {
                                ScoreCategory.EXCELLENT -> "Excellent security hygiene & awareness."
                                ScoreCategory.GOOD -> "Good overall posture with minor areas to optimize."
                                ScoreCategory.BASIC -> "Basic safeguards active. Review recommendations below."
                                ScoreCategory.NEEDS_IMPROVEMENT -> "High exposure detected. Action required."
                            },
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary,
                            fontSize = 12.sp
                        )
                    }

                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.size(76.dp)
                    ) {
                        CircularProgressIndicator(
                            progress = { uiState.overallScore / 100f },
                            modifier = Modifier.size(68.dp),
                            color = statusColor,
                            trackColor = DarkCardSurfaceVariant,
                            strokeWidth = 6.dp
                        )
                        Text(
                            text = "${uiState.overallScore}%",
                            style = MaterialTheme.typography.titleMedium,
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Score Category Reference
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                CategoryBadgeRef("0-39: Needs", ScoreCategory.NEEDS_IMPROVEMENT == uiState.category)
                CategoryBadgeRef("40-59: Basic", ScoreCategory.BASIC == uiState.category)
                CategoryBadgeRef("60-79: Good", ScoreCategory.GOOD == uiState.category)
                CategoryBadgeRef("80-100: Elite", ScoreCategory.EXCELLENT == uiState.category)
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Category Breakdown",
                style = MaterialTheme.typography.titleLarge,
                color = TextPrimary,
                modifier = Modifier.align(Alignment.Start)
            )

            Spacer(modifier = Modifier.height(12.dp))

            uiState.categories.forEach { item ->
                val itemColor = when (item.status) {
                    ItemStatus.VERIFIED -> SuccessGreen
                    ItemStatus.INCOMPLETE -> WarningOrange
                    ItemStatus.UNKNOWN -> TextMuted
                }

                CyberCard(borderColor = BorderColor) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = when (item.status) {
                                        ItemStatus.VERIFIED -> Icons.Default.CheckCircle
                                        ItemStatus.INCOMPLETE -> Icons.Default.Warning
                                        ItemStatus.UNKNOWN -> Icons.Default.Info
                                    },
                                    contentDescription = null,
                                    tint = itemColor,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = item.title,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = item.description,
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextSecondary,
                                fontSize = 12.sp
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column(horizontalAlignment = Alignment.End) {
                            CyberBadge(
                                status = when (item.status) {
                                    ItemStatus.VERIFIED -> CyberStatus.SAFE
                                    ItemStatus.INCOMPLETE -> CyberStatus.WARNING
                                    ItemStatus.UNKNOWN -> CyberStatus.INFO
                                },
                                label = item.status.label
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "${item.earnedPoints}/${item.weight} pts",
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextSecondary,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Actionable Improvement Suggestions
            Text(
                text = "Improvement Recommendations",
                style = MaterialTheme.typography.titleLarge,
                color = TextPrimary,
                modifier = Modifier.align(Alignment.Start)
            )

            Spacer(modifier = Modifier.height(12.dp))

            CyberCard(borderColor = PrimaryBlue) {
                Text(text = "Action Plan", style = MaterialTheme.typography.titleMedium, color = PrimaryBlue, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                uiState.suggestions.forEach { suggestion ->
                    Row(modifier = Modifier.padding(vertical = 4.dp), verticalAlignment = Alignment.Top) {
                        Text(text = "• ", color = PrimaryBlue, fontWeight = FontWeight.Bold)
                        Text(text = suggestion, style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Explainable Security Disclaimer
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkCardSurfaceVariant),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "ℹ️ Educational Disclaimer: This security score is based on educational telemetry, quiz performance, and checklist completion. A high score reflects proactive awareness but does not guarantee that your device, network, or accounts are immune to cyber attacks.",
                    modifier = Modifier.padding(12.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextMuted,
                    fontSize = 12.sp
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            CyberOutlinedButton(
                text = "REFRESH SCORE",
                icon = Icons.Default.Refresh,
                onClick = { viewModel.loadSecurityScore() }
            )

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun CategoryBadgeRef(label: String, isSelected: Boolean) {
    Box(
        modifier = Modifier
            .background(
                color = if (isSelected) DarkCardSurfaceVariant else DarkCardSurface,
                shape = RoundedCornerShape(8.dp)
            )
            .padding(horizontal = 8.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = if (isSelected) PrimaryBlue else TextMuted,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
    }
}
