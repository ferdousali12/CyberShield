package com.example.cybershield.ui.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cybershield.core.components.*
import com.example.cybershield.core.theme.*

@Composable
fun ProtectionCenterScreen(
    onNavigateToUrlScanner: () -> Unit,
    onNavigateToMessageScam: () -> Unit,
    onNavigateToQrScanner: () -> Unit,
    onNavigateToPasswordChecker: () -> Unit,
    onNavigateToPasswordGenerator: () -> Unit,
    onNavigateToEmailBreach: () -> Unit,
    onNavigateToSecurityScore: () -> Unit,
    onNavigateToIncidentGuide: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        CyberTopBar(title = "Protection Center")

        Spacer(modifier = Modifier.height(12.dp))

        // Security Health Banner
        CyberCard(borderColor = PrimaryBlue) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "PERIMETER DEFENSE",
                        fontSize = 11.sp,
                        color = PrimaryBlue,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "All Security Modules Active",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Tap Personal Security Score for deep device audit.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                }
                CyberBadge(status = CyberStatus.SAFE, label = "Protected")
            }

            Spacer(modifier = Modifier.height(16.dp))

            CyberButton(
                text = "VIEW SECURITY SCORE",
                icon = Icons.Default.Speed,
                onClick = onNavigateToSecurityScore
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Threat Detection & Scanning",
            style = MaterialTheme.typography.titleLarge,
            color = TextPrimary
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Tool 1: URL Scanner
        ToolItemCard(
            title = "URL & Phishing Scanner",
            subtitle = "Analyze links for malware, malicious TLDs & SSL issues",
            icon = Icons.Default.Language,
            iconColor = PrimaryBlue,
            onClick = onNavigateToUrlScanner
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Tool 2: Message Scam Detector
        ToolItemCard(
            title = "Message Scam Detector",
            subtitle = "Inspect SMS or WhatsApp messages for fraud & urgency tactics",
            icon = Icons.AutoMirrored.Filled.Chat,
            iconColor = WarningOrange,
            onClick = onNavigateToMessageScam
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Tool 3: QR Code Scanner
        ToolItemCard(
            title = "QR Code Safety Inspector",
            subtitle = "Preview and verify QR codes before opening destinations",
            icon = Icons.Default.QrCodeScanner,
            iconColor = AccentCyan,
            onClick = onNavigateToQrScanner
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Identity & Credential Vault",
            style = MaterialTheme.typography.titleLarge,
            color = TextPrimary
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Tool 4: Password Checker
        ToolItemCard(
            title = "Password Strength Checker",
            subtitle = "Evaluate entropy, length & brute-force resistance",
            icon = Icons.Default.Lock,
            iconColor = SuccessGreen,
            onClick = onNavigateToPasswordChecker
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Tool 5: Password Generator
        ToolItemCard(
            title = "Secure Password Generator",
            subtitle = "Generate robust cryptographic passwords instantly",
            icon = Icons.Default.VpnKey,
            iconColor = PrimaryBlue,
            onClick = onNavigateToPasswordGenerator
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Tool 6: Email Breach Checker
        ToolItemCard(
            title = "Email Breach Checker",
            subtitle = "Check if your email address has appeared in data leaks",
            icon = Icons.Default.MarkEmailRead,
            iconColor = WarningOrange,
            onClick = onNavigateToEmailBreach
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Emergency Response",
            style = MaterialTheme.typography.titleLarge,
            color = TextPrimary
        )

        Spacer(modifier = Modifier.height(12.dp))

        ToolItemCard(
            title = "Security Incident Guide",
            subtitle = "Step-by-step containment steps if compromised",
            icon = Icons.Default.SupportAgent,
            iconColor = DangerRed,
            onClick = onNavigateToIncidentGuide
        )

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
fun ToolItemCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    iconColor: Color,
    onClick: () -> Unit
) {
    CyberCard(
        modifier = Modifier.clickable { onClick() },
        borderColor = BorderColor
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(DarkCardSurfaceVariant, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary,
                    fontSize = 12.sp
                )
            }
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = TextMuted
            )
        }
    }
}
