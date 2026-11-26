package com.example.cybershield.ui.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.cybershield.core.components.*
import com.example.cybershield.core.theme.*
import com.example.cybershield.data.model.AwarenessArticle

@Composable
fun AwarenessListScreen(
    onArticleClick: (String) -> Unit,
    viewModel: AwarenessViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(16.dp)
    ) {
        CyberTopBar(title = "Cybersecurity Awareness Hub")

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Master essential cybersecurity fundamentals, privacy habits, and threat defense strategies.",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(uiState.articles) { article ->
                AwarenessCard(
                    article = article,
                    onClick = { onArticleClick(article.id) }
                )
            }
        }
    }
}

@Composable
fun AwarenessCard(
    article: AwarenessArticle,
    onClick: () -> Unit
) {
    val icon: ImageVector = when (article.iconName) {
        "Security" -> Icons.Default.Security
        "Email" -> Icons.Default.Email
        "Lock" -> Icons.Default.Lock
        "VpnKey" -> Icons.Default.VpnKey
        "VisibilityOff" -> Icons.Default.VisibilityOff
        "Language" -> Icons.Default.Language
        "People" -> Icons.Default.People
        "Wifi" -> Icons.Default.Wifi
        else -> Icons.AutoMirrored.Filled.MenuBook
    }

    CyberCard(
        modifier = Modifier.clickable { onClick() },
        borderColor = BorderColor
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = PrimaryBlue,
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    CyberBadge(status = CyberStatus.INFO, label = article.category)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = article.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }
            }
            Text(
                text = article.readTime,
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
                fontSize = 12.sp
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = article.shortDescription,
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary
        )
    }
}
