package com.example.cybershield.ui.phishing

import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cybershield.core.components.*
import com.example.cybershield.core.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun MessageScamScreen(onBackClick: () -> Unit) {
    var messageInput by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var scanResult by remember { mutableStateOf<ScamScanResult?>(null) }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        CyberTopBar(title = "Message Scam Detector", onBackClick = onBackClick)

        Spacer(modifier = Modifier.height(16.dp))

        CyberCard(borderColor = WarningOrange) {
            Text(
                text = "SMS, WhatsApp & Email Fraud Analyzer",
                style = MaterialTheme.typography.titleLarge,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Paste suspicious text messages, notifications, or emails to inspect for phishing, impersonation, urgency traps, and financial fraud indicators.",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Quick Action Toolbar: Paste & Clear
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(
                    onClick = {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clip = clipboard.primaryClip
                        if (clip != null && clip.itemCount > 0) {
                            messageInput = clip.getItemAt(0).text?.toString() ?: ""
                            Toast.makeText(context, "Message pasted from clipboard", Toast.LENGTH_SHORT).show()
                        }
                    }
                ) {
                    Icon(imageVector = Icons.Default.ContentPaste, contentDescription = null, modifier = Modifier.size(16.dp), tint = PrimaryBlue)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "Paste", color = PrimaryBlue, fontSize = 12.sp)
                }

                Spacer(modifier = Modifier.width(8.dp))

                TextButton(
                    onClick = { messageInput = ""; scanResult = null }
                ) {
                    Icon(imageVector = Icons.Default.Clear, contentDescription = null, modifier = Modifier.size(16.dp), tint = TextMuted)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "Clear", color = TextMuted, fontSize = 12.sp)
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            OutlinedTextField(
                value = messageInput,
                onValueChange = { messageInput = it },
                label = { Text("Paste suspicious message here...", color = TextSecondary) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = DarkCardSurfaceVariant,
                    unfocusedContainerColor = DarkCardSurface,
                    focusedBorderColor = PrimaryBlue,
                    unfocusedBorderColor = BorderColor,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Privacy Notice
            Text(
                text = "🛡️ Privacy Note: CyberShield never reads your SMS or WhatsApp messages automatically. All analysis is performed locally via manual paste.",
                style = MaterialTheme.typography.bodyMedium,
                color = TextMuted,
                fontSize = 11.sp
            )

            Spacer(modifier = Modifier.height(20.dp))

            if (isLoading) {
                Box(modifier = Modifier.fillMaxWidth().height(52.dp), contentAlignment = Alignment.Center) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        CircularProgressIndicator(color = PrimaryBlue, modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(text = "Analyzing scam heuristics & language patterns...", color = TextPrimary)
                    }
                }
            } else {
                CyberButton(
                    text = "ANALYZE MESSAGE",
                    icon = Icons.AutoMirrored.Filled.Chat,
                    enabled = messageInput.isNotBlank(),
                    onClick = {
                        isLoading = true
                        scope.launch {
                            delay(1200L)
                            val lower = messageInput.lowercase()
                            val reasons = mutableListOf<String>()
                            var score = 5

                            if (lower.contains("urgent") || lower.contains("immediately") || lower.contains("suspended") || lower.contains("action required") || lower.contains("expires today")) {
                                score += 25
                                reasons.add("High Urgency: Uses panic-inducing phrasing to force rushed decisions.")
                            }
                            if (lower.contains("otp") || lower.contains("pin") || lower.contains("password") || lower.contains("code") || lower.contains("credentials")) {
                                score += 35
                                reasons.add("Credential Theft Risk: Requests confidential OTPs, PINs, or passwords.")
                            }
                            if (lower.contains("won") || lower.contains("prize") || lower.contains("lottery") || lower.contains("congratulations") || lower.contains("free gift")) {
                                score += 30
                                reasons.add("Fake Prize / Offer: Classic lure promising unearned rewards.")
                            }
                            if (lower.contains("bank") || lower.contains("irs") || lower.contains("paypal") || lower.contains("amazon") || lower.contains("netflix") || lower.contains("support")) {
                                score += 20
                                reasons.add("Impersonation Indicator: Poses as a trusted institution or authority.")
                            }
                            if (lower.contains("transfer") || lower.contains("gift card") || lower.contains("bitcoin") || lower.contains("crypto") || lower.contains("wire") || lower.contains("payment")) {
                                score += 30
                                reasons.add("Suspicious Financial Instruction: Demands unconventional payment methods.")
                            }
                            if (lower.contains("http") || lower.contains("bit.ly") || lower.contains("tinyurl") || lower.contains("click")) {
                                score += 25
                                reasons.add("Suspicious Link: Contains outbound URL or call-to-action link.")
                            }

                            val threat = when {
                                score >= 70 -> CyberStatus.DANGER
                                score >= 35 -> CyberStatus.WARNING
                                else -> CyberStatus.SAFE
                            }

                            if (reasons.isEmpty()) {
                                reasons.add("No high-risk social engineering or scam heuristics detected.")
                            }

                            scanResult = ScamScanResult(
                                score = score.coerceIn(0, 100),
                                threat = threat,
                                findings = reasons
                            )
                            isLoading = false
                        }
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        scanResult?.let { result ->
            val color = when (result.threat) {
                CyberStatus.SAFE -> SuccessGreen
                CyberStatus.WARNING -> WarningOrange
                CyberStatus.DANGER -> DangerRed
                else -> PrimaryBlue
            }

            val titleText = when (result.threat) {
                CyberStatus.DANGER -> "HIGH RISK SCAM DETECTED"
                CyberStatus.WARNING -> "SUSPICIOUS MESSAGE"
                else -> "MESSAGE APPEARS LOW RISK"
            }

            CyberCard(borderColor = color) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (result.threat == CyberStatus.SAFE) Icons.Default.Security else Icons.Default.Warning,
                            contentDescription = null,
                            tint = color,
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = titleText,
                                style = MaterialTheme.typography.titleMedium,
                                color = color,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Risk Score: ${result.score}/100",
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextSecondary,
                                fontSize = 12.sp
                            )
                        }
                    }
                    CyberBadge(status = result.threat, label = result.threat.name)
                }

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = BorderColor)
                Spacer(modifier = Modifier.height(16.dp))

                Text(text = "Detection Findings & Indicators:", style = MaterialTheme.typography.titleMedium, color = TextPrimary)
                Spacer(modifier = Modifier.height(8.dp))

                result.findings.forEach { finding ->
                    Row(modifier = Modifier.padding(vertical = 4.dp), verticalAlignment = Alignment.Top) {
                        Text(text = "• ", color = color, fontWeight = FontWeight.Bold)
                        Text(text = finding, style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Disclaimer
                Card(
                    colors = CardDefaults.cardColors(containerColor = DarkCardSurfaceVariant),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "⚠️ Disclaimer: Heuristic analysis cannot guarantee 100% safety. Never share OTPs, passwords, or personal funds based on unsolicited text messages.",
                        modifier = Modifier.padding(12.dp),
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextMuted,
                        fontSize = 12.sp
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        CyberButton(
                            text = "SHARE RESULT",
                            icon = Icons.Default.Share,
                            onClick = {
                                val sendIntent = Intent().apply {
                                    action = Intent.ACTION_SEND
                                    putExtra(Intent.EXTRA_TEXT, "CyberShield Scam Analysis:\nRisk Score: ${result.score}/100\nStatus: $titleText\nFindings:\n" + result.findings.joinToString("\n"))
                                    type = "text/plain"
                                }
                                val shareIntent = Intent.createChooser(sendIntent, null)
                                context.startActivity(shareIntent)
                            }
                        )
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        CyberOutlinedButton(
                            text = "SCAN ANOTHER",
                            icon = Icons.Default.Refresh,
                            onClick = { scanResult = null; messageInput = "" }
                        )
                    }
                }
            }
        }
    }
}

data class ScamScanResult(
    val score: Int,
    val threat: CyberStatus,
    val findings: List<String>
)
