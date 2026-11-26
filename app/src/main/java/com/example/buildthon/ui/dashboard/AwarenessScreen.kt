package com.example.buildthon.ui.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.buildthon.core.components.*
import com.example.buildthon.core.theme.*

data class ArticlePlaceholder(val title: String, val category: String, val readTime: String)

@Composable
fun AwarenessScreen(
    onBackClick: () -> Unit
) {
    val articles = listOf(
        ArticlePlaceholder("How to Identify Phishing Emails", "Social Engineering", "4 min read"),
        ArticlePlaceholder("Password Hygiene & 2FA Setup", "Identity Protection", "5 min read"),
        ArticlePlaceholder("Public Wi-Fi Safety Tips", "Network Security", "3 min read"),
        ArticlePlaceholder("Ransomware Prevention Guide", "Malware Defense", "6 min read")
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(16.dp)
    ) {
        CyberTopBar(
            title = "Awareness Hub",
            onBackClick = onBackClick
        )

        Spacer(modifier = Modifier.height(16.dp))

        CyberCard(borderColor = AccentCyan) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Default.MenuBook, contentDescription = null, tint = AccentCyan, modifier = Modifier.size(28.dp))
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(text = "Member 2: Learning Materials", fontSize = 11.sp, color = AccentCyan, fontWeight = FontWeight.Bold)
                    Text(text = "Cybersecurity Knowledge Base", style = MaterialTheme.typography.titleMedium, color = TextPrimary)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(articles) { article ->
                CyberCard {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            CyberBadge(status = CyberStatus.INFO, label = article.category)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(text = article.title, style = MaterialTheme.typography.titleMedium, color = TextPrimary)
                            Text(text = article.readTime, style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                        }
                        Icon(imageVector = Icons.Default.Security, contentDescription = null, tint = PrimaryBlue)
                    }
                }
            }
        }
    }
}
