package com.example.buildthon.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.buildthon.core.components.*
import com.example.buildthon.core.theme.*
import com.google.firebase.auth.FirebaseAuth

@Composable
fun ProfileScreen(
    authViewModel: AuthViewModel,
    onLogout: () -> Unit
) {
    val currentUser = FirebaseAuth.getInstance().currentUser
    val userEmail = currentUser?.email ?: "cadet@cybershield.edu"
    val userName = currentUser?.displayName ?: userEmail.substringBefore("@").replaceFirstChar { it.uppercase() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CyberTopBar(title = "User Profile")

        Spacer(modifier = Modifier.height(20.dp))

        // Profile Avatar
        Box(
            modifier = Modifier
                .size(90.dp)
                .clip(CircleShape)
                .background(DarkCardSurfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = null,
                tint = PrimaryBlue,
                modifier = Modifier.size(48.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = userName,
            style = MaterialTheme.typography.titleLarge,
            color = TextPrimary
        )

        Text(
            text = userEmail,
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Account Information Card
        CyberCard(borderColor = PrimaryBlue) {
            Text(
                text = "Account Information",
                style = MaterialTheme.typography.titleMedium,
                color = TextPrimary,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Security, contentDescription = null, tint = SuccessGreen)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(text = "Security Level", color = TextPrimary)
                }
                CyberBadge(status = CyberStatus.SAFE, label = "High")
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = BorderColor)
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "Account Type", color = TextSecondary)
                Text(text = "Standard Cyber Cadet", color = TextPrimary, fontWeight = FontWeight.Medium)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // About CyberShield Card
        CyberCard(borderColor = AccentCyan) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Default.Info, contentDescription = null, tint = AccentCyan, modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "About CyberShield",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "CyberShield is a mobile cybersecurity application built with Kotlin, Jetpack Compose, and Firebase, designed to safeguard users against phishing, weak passwords, and digital threats.",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = BorderColor)
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "Application Version", color = TextSecondary)
                Text(text = "v1.0.0 (Build 2025)", color = TextPrimary, fontWeight = FontWeight.Medium)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        CyberOutlinedButton(
            text = "LOGOUT",
            icon = Icons.Default.ExitToApp,
            onClick = {
                authViewModel.logout(onLogout)
            }
        )

        Spacer(modifier = Modifier.height(24.dp))
    }
}
