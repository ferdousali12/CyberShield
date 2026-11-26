package com.example.cybershield.ui.auth

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.cybershield.core.components.*
import com.example.cybershield.core.theme.*

@Composable
fun ProfileScreen(
    authViewModel: AuthViewModel,
    onLogout: () -> Unit,
    viewModel: ProfileViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var editNameInput by remember(uiState.displayName) { mutableStateOf(uiState.displayName) }

    // Snackbar handling
    LaunchedEffect(uiState.errorMessage, uiState.successMessage) {
        uiState.errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessages()
        }
        uiState.successMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessages()
        }
    }

    // Progress animation for security score
    val animatedProgress by animateFloatAsState(
        targetValue = uiState.securityScore / 100f,
        animationSpec = tween(durationMillis = 1200),
        label = "SecurityScoreAnimation"
    )

    Scaffold(
        containerColor = DarkBackground,
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(DarkBackground)
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CyberTopBar(title = "User Profile")

            Spacer(modifier = Modifier.height(16.dp))

            // 1. Header: Avatar with Initials on blue-to-cyan gradient
            Box(
                modifier = Modifier
                    .size(90.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(PrimaryBlue, AccentCyan)
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                val initials = uiState.displayName
                    .split(" ")
                    .filter { it.isNotBlank() }
                    .take(2)
                    .map { it.first().uppercase() }
                    .joinToString("")
                    .ifEmpty { "CS" }

                Text(
                    text = initials,
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 32.sp
                    ),
                    color = TextPrimary
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = uiState.displayName,
                    style = MaterialTheme.typography.titleLarge,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(6.dp))

                // If demo user, show DEMO MODE badge in amber (#F59E0B) and hide Edit button
                if (uiState.isDemoUser) {
                    CyberBadge(status = CyberStatus.WARNING, label = "DEMO MODE")
                } else {
                    IconButton(
                        onClick = {
                            editNameInput = uiState.displayName
                            viewModel.setEditDialogVisible(true)
                        },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit Profile",
                            tint = PrimaryBlue,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Text(
                text = uiState.email,
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(20.dp))

            // 2. Stats Section: Row of 3 Stat Cards
            if (uiState.isLoading) {
                Box(modifier = Modifier.fillMaxWidth().height(80.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = PrimaryBlue)
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        StatCard(title = "URL Scans", value = "${uiState.totalScans}", icon = Icons.Default.Language, color = PrimaryBlue)
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        StatCard(title = "Threats", value = "${uiState.threatsDetected}", icon = Icons.Default.Warning, color = DangerRed)
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        StatCard(title = "Quiz Avg", value = "${uiState.quizAverageScore}%", icon = Icons.Default.Quiz, color = SuccessGreen)
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 3. Security Score: Circular Progress Indicator in cyan (AccentCyan)
            CyberCard(borderColor = BorderColor) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Security, contentDescription = null, tint = AccentCyan, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "SECURITY STATUS",
                                fontSize = 11.sp,
                                color = AccentCyan,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${uiState.securityScore} / 100",
                            style = MaterialTheme.typography.headlineMedium,
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "Level: ", style = MaterialTheme.typography.bodyMedium, color = TextSecondary, fontSize = 12.sp)
                            CyberBadge(
                                status = when (uiState.securityLevelLabel) {
                                    "High" -> CyberStatus.SAFE
                                    "Medium" -> CyberStatus.WARNING
                                    else -> CyberStatus.DANGER
                                },
                                label = uiState.securityLevelLabel
                            )
                        }
                    }

                    // Circular Progress Indicator in Cyan (AccentCyan)
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.size(72.dp)
                    ) {
                        CircularProgressIndicator(
                            progress = { animatedProgress },
                            modifier = Modifier.size(64.dp),
                            color = AccentCyan,
                            trackColor = DarkCardSurfaceVariant,
                            strokeWidth = 6.dp
                        )
                        Text(
                            text = "${uiState.securityScore}%",
                            style = MaterialTheme.typography.titleMedium,
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 4. Account Information Card
            CyberCard(borderColor = BorderColor) {
                Text(
                    text = "Account Information",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(12.dp))

                InfoRow(label = "Member Since", value = uiState.memberSince, icon = Icons.Default.CalendarToday)
                Spacer(modifier = Modifier.height(8.dp))
                HorizontalDivider(color = BorderColor)
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.MilitaryTech, contentDescription = null, tint = AccentCyan, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(text = "Security Rank", color = TextSecondary)
                    }
                    CyberBadge(
                        status = when (uiState.rank) {
                            "Cyber Guardian" -> CyberStatus.SAFE
                            "Defender" -> CyberStatus.SAFE
                            "Cyber Cadet" -> CyberStatus.INFO
                            else -> CyberStatus.WARNING
                        },
                        label = uiState.rank
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 6. Settings Card with Toggles
            CyberCard(borderColor = BorderColor) {
                Text(
                    text = "Preferences & Settings",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(12.dp))

                SettingSwitchRow(
                    title = "Dark Mode",
                    subtitle = "Always enabled for Cyber Navy UI",
                    checked = uiState.darkModeEnabled,
                    onCheckedChange = { viewModel.setDarkMode(it) }
                )

                Spacer(modifier = Modifier.height(8.dp))
                HorizontalDivider(color = BorderColor)
                Spacer(modifier = Modifier.height(8.dp))

                SettingSwitchRow(
                    title = "Daily Security Tip Notification",
                    subtitle = "Receive morning threat alerts & reminders",
                    checked = uiState.dailyTipsEnabled,
                    onCheckedChange = { viewModel.setDailyTips(it) }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // About CyberShield Card
            CyberCard(borderColor = BorderColor) {
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
                    text = "CyberShield 2.0 is a mobile cybersecurity suite designed to safeguard users against phishing, weak passwords, and digital threats.",
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
                    Text(text = "v2.0.0 (Build 2025)", color = TextPrimary, fontWeight = FontWeight.Medium)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 7. Logout Button with Confirmation Dialog
            CyberOutlinedButton(
                text = "LOGOUT",
                icon = Icons.Default.ExitToApp,
                onClick = { viewModel.setLogoutDialogVisible(true) }
            )

            // 8. Delete Account Button (Hidden for demo account)
            if (!uiState.isDemoUser) {
                Spacer(modifier = Modifier.height(12.dp))
                TextButton(
                    onClick = { viewModel.setDeleteDialogVisible(true) },
                    colors = ButtonDefaults.textButtonColors(contentColor = DangerRed)
                ) {
                    Icon(imageVector = Icons.Default.DeleteForever, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Delete Account", fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }

        // 5. Edit Profile Dialog
        if (uiState.showEditDialog && !uiState.isDemoUser) {
            AlertDialog(
                onDismissRequest = { viewModel.setEditDialogVisible(false) },
                containerColor = DarkCardSurface,
                title = { Text("Edit Profile", color = TextPrimary, fontWeight = FontWeight.Bold) },
                text = {
                    Column {
                        Text("Update your display name below:", color = TextSecondary, fontSize = 13.sp)
                        Spacer(modifier = Modifier.height(12.dp))
                        CyberTextField(
                            value = editNameInput,
                            onValueChange = { editNameInput = it },
                            label = "Display Name",
                            leadingIcon = Icons.Default.Person
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = { viewModel.updateDisplayName(editNameInput) {} },
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                    ) {
                        Text("Save", color = TextPrimary, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { viewModel.setEditDialogVisible(false) }) {
                        Text("Cancel", color = TextSecondary)
                    }
                }
            )
        }

        // 7. Logout Confirmation Dialog
        if (uiState.showLogoutDialog) {
            AlertDialog(
                onDismissRequest = { viewModel.setLogoutDialogVisible(false) },
                containerColor = DarkCardSurface,
                title = { Text("Confirm Logout", color = TextPrimary, fontWeight = FontWeight.Bold) },
                text = { Text("Are you sure you want to log out of CyberShield?", color = TextSecondary) },
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.setLogoutDialogVisible(false)
                            authViewModel.logout(onLogout)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = DangerRed)
                    ) {
                        Text("Logout", color = TextPrimary, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { viewModel.setLogoutDialogVisible(false) }) {
                        Text("Cancel", color = TextSecondary)
                    }
                }
            )
        }

        // 8. Delete Account Confirmation Dialog
        if (uiState.showDeleteDialog && !uiState.isDemoUser) {
            AlertDialog(
                onDismissRequest = { viewModel.setDeleteDialogVisible(false) },
                containerColor = DarkCardSurface,
                title = { Text("Delete Account", color = DangerRed, fontWeight = FontWeight.Bold) },
                text = { Text("Warning: This action is permanent and will delete all your scan history, quiz records, and Firebase account data.", color = TextSecondary) },
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.setDeleteDialogVisible(false)
                            viewModel.deleteAccount {
                                authViewModel.logout(onLogout)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = DangerRed)
                    ) {
                        Text("Delete", color = TextPrimary, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { viewModel.setDeleteDialogVisible(false) }) {
                        Text("Cancel", color = TextSecondary)
                    }
                }
            )
        }
    }
}

@Composable
fun StatCard(title: String, value: String, icon: ImageVector, color: Color) {
    CyberCard(borderColor = BorderColor) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = TextPrimary)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = title, style = MaterialTheme.typography.bodyMedium, color = TextSecondary, fontSize = 11.sp)
        }
    }
}

@Composable
fun InfoRow(label: String, value: String, icon: ImageVector) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = icon, contentDescription = null, tint = AccentCyan, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Text(text = label, color = TextSecondary)
        }
        Text(text = value, color = TextPrimary, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun SettingSwitchRow(title: String, subtitle: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, color = TextPrimary, fontWeight = FontWeight.Medium)
            Text(text = subtitle, color = TextSecondary, fontSize = 11.sp)
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(checkedThumbColor = TextPrimary, checkedTrackColor = PrimaryBlue)
        )
    }
}
