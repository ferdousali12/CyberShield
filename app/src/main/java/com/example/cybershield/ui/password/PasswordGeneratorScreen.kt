package com.example.cybershield.ui.password

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cybershield.core.components.*
import com.example.cybershield.core.theme.*

@Composable
fun PasswordGeneratorScreen(onBackClick: () -> Unit) {
    var passwordLength by remember { mutableStateOf(16f) }
    var includeUppercase by remember { mutableStateOf(true) }
    var includeLowercase by remember { mutableStateOf(true) }
    var includeNumbers by remember { mutableStateOf(true) }
    var includeSymbols by remember { mutableStateOf(true) }
    var generatedPassword by remember { mutableStateOf(generateSecurePassword(16, true, true, true, true)) }
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        CyberTopBar(title = "Secure Password Generator", onBackClick = onBackClick)

        Spacer(modifier = Modifier.height(16.dp))

        CyberCard(borderColor = PrimaryBlue) {
            Text(
                text = "Cryptographic Passwords",
                style = MaterialTheme.typography.titleLarge,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Generate high-entropy passwords resilient against brute-force and dictionary attacks.",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Password Output Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(DarkCardSurfaceVariant, RoundedCornerShape(12.dp))
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = generatedPassword,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        ),
                        color = AccentCyan,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clip = ClipData.newPlainText("CyberShield Password", generatedPassword)
                        clipboard.setPrimaryClip(clip)
                        Toast.makeText(context, "Password copied to clipboard!", Toast.LENGTH_SHORT).show()
                    }) {
                        Icon(imageVector = Icons.Default.ContentCopy, contentDescription = "Copy", tint = PrimaryBlue)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Length: ${passwordLength.toInt()} characters",
                style = MaterialTheme.typography.bodyMedium,
                color = TextPrimary
            )
            Slider(
                value = passwordLength,
                onValueChange = { passwordLength = it },
                valueRange = 8f..32f,
                steps = 23,
                colors = SliderDefaults.colors(thumbColor = PrimaryBlue, activeTrackColor = PrimaryBlue)
            )

            Spacer(modifier = Modifier.height(16.dp))

            ToggleRow("Uppercase Letters (A-Z)", includeUppercase) { includeUppercase = it }
            ToggleRow("Lowercase Letters (a-z)", includeLowercase) { includeLowercase = it }
            ToggleRow("Numeric Digits (0-9)", includeNumbers) { includeNumbers = it }
            ToggleRow("Special Symbols (!@#$)", includeSymbols) { includeSymbols = it }

            Spacer(modifier = Modifier.height(24.dp))

            CyberButton(
                text = "GENERATE NEW PASSWORD",
                icon = Icons.Default.Refresh,
                onClick = {
                    generatedPassword = generateSecurePassword(
                        passwordLength.toInt(),
                        includeUppercase,
                        includeLowercase,
                        includeNumbers,
                        includeSymbols
                    )
                }
            )
        }
    }
}

@Composable
fun ToggleRow(label: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(checkedThumbColor = TextPrimary, checkedTrackColor = PrimaryBlue)
        )
    }
}

fun generateSecurePassword(length: Int, upper: Boolean, lower: Boolean, nums: Boolean, syms: Boolean): String {
    val uChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
    val lChars = "abcdefghijklmnopqrstuvwxyz"
    val nChars = "0123456789"
    val sChars = "!@#$%^&*()_+-=[]{}|;:,.<>?"

    var pool = ""
    if (upper) pool += uChars
    if (lower) pool += lChars
    if (nums) pool += nChars
    if (syms) pool += sChars

    if (pool.isEmpty()) pool = lChars

    return (1..length)
        .map { pool.random() }
        .joinToString("")
}
