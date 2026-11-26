package com.example.cybershield.ui.quiz_history

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.SupportAgent
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
fun IncidentGuideScreen(
    onBackClick: () -> Unit,
    viewModel: IncidentGuideViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        CyberTopBar(title = "Security Incident Guide", onBackClick = onBackClick)

        Spacer(modifier = Modifier.height(16.dp))

        CyberCard(borderColor = DangerRed) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Default.SupportAgent, contentDescription = null, tint = DangerRed, modifier = Modifier.size(32.dp))
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(text = "EMERGENCY RESPONSE PLAYBOOK", fontSize = 11.sp, color = DangerRed, fontWeight = FontWeight.Bold)
                    Text(text = "Offline guides for online security incidents", style = MaterialTheme.typography.titleMedium, color = TextPrimary)
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        uiState.guides.forEach { guide ->
            val isExpanded = uiState.expandedGuideId == guide.id

            CyberCard(
                modifier = Modifier.clickable { viewModel.toggleExpand(guide.id) },
                borderColor = if (isExpanded) PrimaryBlue else BorderColor
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = guide.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        modifier = Modifier.weight(1f)
                    )
                    Icon(
                        imageVector = if (isExpanded) Icons.Default.ExpandMore else Icons.Default.ChevronRight,
                        contentDescription = null,
                        tint = if (isExpanded) PrimaryBlue else TextMuted
                    )
                }

                AnimatedVisibility(visible = isExpanded) {
                    Column(modifier = Modifier.padding(top = 16.dp)) {
                        HorizontalDivider(color = BorderColor)
                        Spacer(modifier = Modifier.height(12.dp))

                        // Potential Risk
                        Text(text = "Potential Risk:", style = MaterialTheme.typography.titleSmall, color = DangerRed, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(text = guide.potentialRisk, style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                        Spacer(modifier = Modifier.height(12.dp))

                        // Immediate Steps
                        Text(text = "Immediate Action Steps:", style = MaterialTheme.typography.titleSmall, color = PrimaryBlue, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(4.dp))
                        guide.immediateSteps.forEachIndexed { idx, step ->
                            Row(modifier = Modifier.padding(vertical = 2.dp)) {
                                Text(text = "${idx + 1}. ", color = PrimaryBlue, fontWeight = FontWeight.Bold)
                                Text(text = step, style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // What to Avoid
                        Text(text = "What to Avoid:", style = MaterialTheme.typography.titleSmall, color = WarningOrange, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(4.dp))
                        guide.whatToAvoid.forEach { avoid ->
                            Row(modifier = Modifier.padding(vertical = 2.dp)) {
                                Text(text = "• ", color = WarningOrange, fontWeight = FontWeight.Bold)
                                Text(text = avoid, style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Recovery
                        Text(text = "Account Recovery:", style = MaterialTheme.typography.titleSmall, color = SuccessGreen, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(text = guide.recoveryRecommendations, style = MaterialTheme.typography.bodyMedium, color = TextSecondary)

                        Spacer(modifier = Modifier.height(12.dp))

                        // Reporting Resource
                        Text(text = "Official Reporting Resource:", style = MaterialTheme.typography.titleSmall, color = AccentCyan, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(text = guide.reportingResource, style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}
