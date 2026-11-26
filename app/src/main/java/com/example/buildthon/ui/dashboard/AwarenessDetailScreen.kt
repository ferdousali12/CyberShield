package com.example.buildthon.ui.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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

@Composable
fun AwarenessDetailScreen(
    articleId: String,
    onBackClick: () -> Unit,
    viewModel: AwarenessViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(articleId) {
        viewModel.selectArticle(articleId)
    }

    val article = uiState.selectedArticle

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        CyberTopBar(
            title = article?.category ?: "Learning Guide",
            onBackClick = onBackClick
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (article == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = PrimaryBlue)
            }
        } else {
            CyberCard(borderColor = PrimaryBlue) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CyberBadge(status = CyberStatus.INFO, label = article.category)
                    Text(
                        text = article.readTime,
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = article.title,
                    style = MaterialTheme.typography.headlineMedium,
                    color = TextPrimary
                )

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = BorderColor)
                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Detailed Explanation",
                    style = MaterialTheme.typography.titleMedium,
                    color = AccentCyan,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = article.detailedContent,
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextPrimary
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Practical Example Card Box
                Card(
                    colors = CardDefaults.cardColors(containerColor = DarkCardSurfaceVariant),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Lightbulb,
                                contentDescription = null,
                                tint = WarningOrange,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Practical Example",
                                style = MaterialTheme.typography.titleMedium,
                                color = WarningOrange,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = article.practicalExample,
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            CyberOutlinedButton(
                text = "BACK TO LEARNING HUB",
                onClick = onBackClick
            )
        }
    }
}
