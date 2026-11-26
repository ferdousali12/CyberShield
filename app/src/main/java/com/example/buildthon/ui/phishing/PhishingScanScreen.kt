package com.example.buildthon.ui.phishing

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
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
import com.example.buildthon.core.components.*
import com.example.buildthon.core.theme.*
import com.example.buildthon.data.model.ThreatLevel

@Composable
fun PhishingScanScreen(
    viewModel: PhishingViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        CyberTopBar(title = "Phishing & URL Scanner")

        Spacer(modifier = Modifier.height(16.dp))

        // Scanner Input Card
        CyberCard(borderColor = PrimaryBlue) {
            Text(
                text = "Threat Detection Engine",
                style = MaterialTheme.typography.titleLarge,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Paste a suspicious link to analyze heuristics, SSL validity, and phishing indicators securely.",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(20.dp))

            CyberTextField(
                value = uiState.urlInput,
                onValueChange = { viewModel.updateUrlInput(it) },
                label = "Website URL (e.g., https://example.com)",
                leadingIcon = Icons.Default.Language
            )

            if (uiState.errorMessage != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = uiState.errorMessage ?: "",
                    color = DangerRed,
                    style = MaterialTheme.typography.bodyMedium,
                    fontSize = 13.sp
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            if (uiState.isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        CircularProgressIndicator(
                            color = PrimaryBlue,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Analyzing URL threat signatures...",
                            color = TextPrimary,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            } else {
                CyberButton(
                    text = "SCAN URL",
                    icon = Icons.Default.Language,
                    onClick = { viewModel.scanUrl() }
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Scan Results Section
        uiState.scanResult?.let { result ->
            val (statusColor, badgeStatus, titleText) = when (result.threatLevel) {
                ThreatLevel.SAFE -> Triple(SuccessGreen, CyberStatus.SAFE, "URL APPEARS LOW RISK")
                ThreatLevel.SUSPICIOUS -> Triple(WarningOrange, CyberStatus.WARNING, "SUSPICIOUS URL DETECTED")
                ThreatLevel.DANGEROUS -> Triple(DangerRed, CyberStatus.DANGER, "HIGH RISK PHISHING THREAT")
            }

            CyberCard(borderColor = statusColor) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = when (result.threatLevel) {
                                ThreatLevel.SAFE -> Icons.Default.Security
                                else -> Icons.Default.Warning
                            },
                            contentDescription = null,
                            tint = statusColor,
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = titleText,
                                style = MaterialTheme.typography.titleMedium,
                                color = statusColor,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Risk Score: ${result.riskScore}/100",
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextSecondary,
                                fontSize = 12.sp
                            )
                        }
                    }
                    CyberBadge(status = badgeStatus, label = result.threatLevel.name)
                }

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = BorderColor)
                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Threat Analysis Findings:",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(8.dp))

                result.reasons.forEach { reason ->
                    Row(
                        modifier = Modifier.padding(vertical = 4.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Text(text = "• ", color = statusColor, fontWeight = FontWeight.Bold)
                        Text(
                            text = reason,
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Important Security Disclaimer
                Card(
                    colors = CardDefaults.cardColors(containerColor = DarkCardSurfaceVariant),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "⚠️ Disclaimer: No automated scanner can guarantee 100% safety. Always verify domain certificates before entering personal information.",
                        modifier = Modifier.padding(12.dp),
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextMuted,
                        fontSize = 12.sp
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                CyberOutlinedButton(
                    text = "SCAN ANOTHER URL",
                    icon = Icons.Default.Refresh,
                    onClick = { viewModel.resetScan() }
                )
            }
        }
    }
}
