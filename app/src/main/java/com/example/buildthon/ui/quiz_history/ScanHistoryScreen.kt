package com.example.buildthon.ui.quiz_history

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.buildthon.core.components.*
import com.example.buildthon.core.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ScanHistoryScreen(
    onBackClick: () -> Unit = {},
    viewModel: HistoryViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Scans, 1: Quiz Results

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(16.dp)
    ) {
        CyberTopBar(
            title = "Activity & Scan History",
            onBackClick = onBackClick
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Tabs for Scans vs Quiz Results
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = DarkCardSurface,
            contentColor = PrimaryBlue
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text("URL Scans (${uiState.scans.size})", color = if (selectedTab == 0) PrimaryBlue else TextSecondary) },
                icon = { Icon(imageVector = Icons.Default.History, contentDescription = null, tint = if (selectedTab == 0) PrimaryBlue else TextSecondary) }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text("Quiz Results (${uiState.quizResults.size})", color = if (selectedTab == 1) PrimaryBlue else TextSecondary) },
                icon = { Icon(imageVector = Icons.Default.Quiz, contentDescription = null, tint = if (selectedTab == 1) PrimaryBlue else TextSecondary) }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = PrimaryBlue)
            }
        } else if (uiState.errorMessage != null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = uiState.errorMessage ?: "Error loading history",
                    color = DangerRed,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        } else {
            if (selectedTab == 0) {
                // URL Scans List
                if (uiState.scans.isEmpty()) {
                    EmptyHistoryState("No URL scans recorded yet.")
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1f),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(uiState.scans, key = { it.scanId }) { scan ->
                            val isPhishing = scan.result.equals("Phishing", ignoreCase = true) || scan.result.equals("Dangerous", ignoreCase = true)
                            val dateStr = formatTimestamp(scan.scannedAt)

                            CyberCard(borderColor = if (isPhishing) DangerRed else SuccessGreen) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = scan.target,
                                            style = MaterialTheme.typography.titleMedium,
                                            color = TextPrimary,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "Scanned on: $dateStr",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = TextSecondary,
                                            fontSize = 12.sp
                                        )
                                    }

                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        CyberBadge(
                                            status = if (isPhishing) CyberStatus.DANGER else CyberStatus.SAFE,
                                            label = scan.result
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        IconButton(onClick = { viewModel.deleteScan(scan.scanId) }) {
                                            Icon(
                                                imageVector = Icons.Default.Delete,
                                                contentDescription = "Delete Scan",
                                                tint = TextMuted
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                // Quiz Results List
                if (uiState.quizResults.isEmpty()) {
                    EmptyHistoryState("No quiz attempts recorded yet.")
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1f),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(uiState.quizResults, key = { it.resultId }) { quiz ->
                            val dateStr = formatTimestamp(quiz.completedAt)

                            CyberCard(borderColor = if (quiz.score >= 70) SuccessGreen else WarningOrange) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = "Cybersecurity Challenge",
                                            style = MaterialTheme.typography.titleMedium,
                                            color = TextPrimary,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "Completed on: $dateStr",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = TextSecondary,
                                            fontSize = 12.sp
                                        )
                                    }
                                    CyberBadge(
                                        status = if (quiz.score >= 70) CyberStatus.SAFE else CyberStatus.WARNING,
                                        label = "${quiz.score}% Score"
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EmptyHistoryState(message: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.Default.History,
                contentDescription = null,
                tint = TextMuted,
                modifier = Modifier.size(64.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = message,
                style = MaterialTheme.typography.titleMedium,
                color = TextSecondary
            )
        }
    }
}

fun formatTimestamp(timestamp: Long): String {
    val sdf = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
    return sdf.format(Date(timestamp))
}
