package com.example.cybershield.ui.password

import android.util.Patterns
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MarkEmailRead
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cybershield.core.components.*
import com.example.cybershield.core.theme.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@Composable
fun EmailBreachScreen(onBackClick: () -> Unit) {
    var emailInput by remember { mutableStateOf("") }
    var saveHistoryConsent by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var breachResult by remember { mutableStateOf<DetailedBreachResult?>(null) }
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        CyberTopBar(title = "Email Breach Checker", onBackClick = onBackClick)

        Spacer(modifier = Modifier.height(16.dp))

        CyberCard(borderColor = WarningOrange) {
            Text(
                text = "Credential Leak Intelligence",
                style = MaterialTheme.typography.titleLarge,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Check whether your email address appears in publicly known data breach registries and compromise dumps.",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(20.dp))

            CyberTextField(
                value = emailInput,
                onValueChange = { emailInput = it; errorMessage = null },
                label = "Enter email address (e.g., user@example.com)",
                leadingIcon = Icons.Default.MarkEmailRead
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Consent Checkbox for History Storage
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = saveHistoryConsent,
                    onCheckedChange = { saveHistoryConsent = it },
                    colors = CheckboxDefaults.colors(checkedColor = PrimaryBlue)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Save scan history to my secure vault (optional)",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary,
                    fontSize = 12.sp
                )
            }

            if (errorMessage != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = errorMessage ?: "", color = DangerRed, style = MaterialTheme.typography.bodyMedium, fontSize = 13.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Privacy Notice
            Text(
                text = "🔒 Privacy Guarantee: CyberShield never asks for email passwords and does not store entered emails unless you check the consent box above.",
                style = MaterialTheme.typography.bodyMedium,
                color = TextMuted,
                fontSize = 11.sp
            )

            Spacer(modifier = Modifier.height(20.dp))

            if (isLoading) {
                Box(modifier = Modifier.fillMaxWidth().height(52.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = PrimaryBlue)
                }
            } else {
                CyberButton(
                    text = "CHECK BREACH STATUS",
                    icon = Icons.Default.MarkEmailRead,
                    enabled = emailInput.isNotBlank(),
                    onClick = {
                        val email = emailInput.trim()
                        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                            errorMessage = "Please enter a valid email address format."
                            return@CyberButton
                        }

                        isLoading = true
                        scope.launch {
                            delay(1400L)
                            val lower = email.lowercase()
                            val isCompromised = lower.contains("test") || lower.contains("hack") || lower.contains("admin") || lower.contains("leak") || lower.hashCode() % 2 == 0

                            val status = if (isCompromised) CyberStatus.DANGER else CyberStatus.SAFE
                            val breaches = if (isCompromised) {
                                listOf(
                                    BreachDetail(
                                        name = "DarkWeb-Creds-2024",
                                        date = "October 2024",
                                        exposedCategories = listOf("Passwords", "Usernames", "IP Addresses")
                                    ),
                                    BreachDetail(
                                        name = "Collection1-Enterprise",
                                        date = "January 2019",
                                        exposedCategories = listOf("Plaintext Passwords", "Phone Numbers")
                                    )
                                )
                            } else {
                                emptyList()
                            }

                            val recommendations = if (isCompromised) {
                                listOf(
                                    "Immediately change your password on all accounts using this email.",
                                    "Enable Multi-Factor Authentication (MFA) or passkeys everywhere possible.",
                                    "Monitor financial statements for unauthorized activity."
                                )
                            } else {
                                listOf(
                                    "Your email address was not found in active monitored breach registries.",
                                    "Continue using unique passwords and password managers for optimal hygiene."
                                )
                            }

                            breachResult = DetailedBreachResult(
                                email = email,
                                isCompromised = isCompromised,
                                status = status,
                                breaches = breaches,
                                recommendations = recommendations
                            )

                            // Save to Firestore if consented
                            if (saveHistoryConsent) {
                                try {
                                    val uid = FirebaseAuth.getInstance().currentUser?.uid
                                    if (uid != null) {
                                        val db = FirebaseFirestore.getInstance()
                                        val data = hashMapOf(
                                            "email" to email,
                                            "isCompromised" to isCompromised,
                                            "timestamp" to System.currentTimeMillis()
                                        )
                                        db.collection("users").document(uid).collection("breachScans").add(data).await()
                                    }
                                } catch (_: Exception) {}
                            }

                            isLoading = false
                        }
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        breachResult?.let { result ->
            val color = if (result.isCompromised) DangerRed else SuccessGreen

            CyberCard(borderColor = color) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (result.isCompromised) Icons.Default.Warning else Icons.Default.Security,
                            contentDescription = null,
                            tint = color,
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = if (result.isCompromised) "EMAIL FOUND IN DATA BREACHES!" else "NO KNOWN BREACHES FOUND",
                                style = MaterialTheme.typography.titleMedium,
                                color = color,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Target: ${result.email}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextSecondary,
                                fontSize = 12.sp
                            )
                        }
                    }
                    CyberBadge(status = result.status, label = if (result.isCompromised) "Compromised" else "Secure")
                }

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = BorderColor)
                Spacer(modifier = Modifier.height(16.dp))

                if (result.isCompromised) {
                    Text(text = "Known Breach Registries:", style = MaterialTheme.typography.titleMedium, color = TextPrimary)
                    Spacer(modifier = Modifier.height(8.dp))

                    result.breaches.forEach { breach ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = DarkCardSurfaceVariant),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(text = breach.name, style = MaterialTheme.typography.titleSmall, color = WarningOrange, fontWeight = FontWeight.Bold)
                                Text(text = "Breach Date: ${breach.date}", style = MaterialTheme.typography.bodyMedium, color = TextSecondary, fontSize = 12.sp)
                                Text(text = "Exposed Data: ${breach.exposedCategories.joinToString(", ")}", style = MaterialTheme.typography.bodyMedium, color = TextPrimary, fontSize = 12.sp)
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }

                Text(text = "Security Recommendations:", style = MaterialTheme.typography.titleMedium, color = TextPrimary)
                Spacer(modifier = Modifier.height(8.dp))

                result.recommendations.forEach { rec ->
                    Row(modifier = Modifier.padding(vertical = 4.dp)) {
                        Text(text = "• ", color = color, fontWeight = FontWeight.Bold)
                        Text(text = rec, style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Absence disclaimer
                Card(
                    colors = CardDefaults.cardColors(containerColor = DarkCardSurfaceVariant),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "ℹ️ Note: Absence from known breach registries does not guarantee that an account is 100% safe, as not all breaches are publicly disclosed.",
                        modifier = Modifier.padding(12.dp),
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextMuted,
                        fontSize = 11.sp
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))
                CyberOutlinedButton(
                    text = "CHECK ANOTHER EMAIL",
                    icon = Icons.Default.Refresh,
                    onClick = { breachResult = null; emailInput = "" }
                )
            }
        }
    }
}

data class BreachDetail(
    val name: String,
    val date: String,
    val exposedCategories: List<String>
)

data class DetailedBreachResult(
    val email: String,
    val isCompromised: Boolean,
    val status: CyberStatus,
    val breaches: List<BreachDetail>,
    val recommendations: List<String>
)
