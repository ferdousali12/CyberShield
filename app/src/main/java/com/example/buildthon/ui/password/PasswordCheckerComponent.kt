package com.example.buildthon.ui.password

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.buildthon.core.components.*
import com.example.buildthon.core.theme.*
import com.example.buildthon.core.util.PasswordAnalysisResult
import com.example.buildthon.core.util.PasswordStrength

@Composable
fun PasswordCheckerComponent(
    passwordInput: String,
    isPasswordVisible: Boolean,
    analysisResult: PasswordAnalysisResult,
    onPasswordChange: (String) -> Unit,
    onToggleVisibility: () -> Unit,
    onClear: () -> Unit,
    modifier: Modifier = Modifier
) {
    val (statusColor, cyberStatus) = when (analysisResult.strength) {
        PasswordStrength.VERY_WEAK -> DangerRed to CyberStatus.DANGER
        PasswordStrength.WEAK -> DangerRed to CyberStatus.DANGER
        PasswordStrength.MEDIUM -> WarningOrange to CyberStatus.WARNING
        PasswordStrength.STRONG -> PrimaryBlue to CyberStatus.INFO
        PasswordStrength.VERY_STRONG -> SuccessGreen to CyberStatus.SAFE
    }

    CyberCard(modifier = modifier, borderColor = statusColor) {
        Text(
            text = "Local Password Security Engine",
            style = MaterialTheme.typography.titleLarge,
            color = TextPrimary
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Analysis is performed 100% locally. Passwords are never logged, stored, or sent to any server.",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary,
            fontSize = 12.sp
        )

        Spacer(modifier = Modifier.height(20.dp))

        CyberTextField(
            value = passwordInput,
            onValueChange = onPasswordChange,
            label = "Enter password to test",
            leadingIcon = Icons.Default.Lock,
            visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions.Default,
            trailingIcon = {
                IconButton(onClick = onToggleVisibility) {
                    Icon(
                        imageVector = if (isPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                        contentDescription = "Toggle Visibility",
                        tint = TextSecondary
                    )
                }
            }
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Strength Header & Badge
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "STRENGTH RATING",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = TextSecondary
            )
            CyberBadge(
                status = cyberStatus,
                label = analysisResult.strength.label
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        LinearProgressIndicator(
            progress = { analysisResult.score / 5f },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp),
            color = statusColor,
            trackColor = DarkCardSurfaceVariant
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Requirements Checklist
        Text(
            text = "Complexity Checklist:",
            style = MaterialTheme.typography.titleMedium,
            color = TextPrimary
        )
        Spacer(modifier = Modifier.height(8.dp))

        RequirementRow(label = "At least 8 characters length", met = analysisResult.hasMinLength)
        RequirementRow(label = "Contains uppercase letter (A-Z)", met = analysisResult.hasUppercase)
        RequirementRow(label = "Contains lowercase letter (a-z)", met = analysisResult.hasLowercase)
        RequirementRow(label = "Contains numeric digit (0-9)", met = analysisResult.hasDigit)
        RequirementRow(label = "Contains special character (!@#\$%)", met = analysisResult.hasSpecialChar)

        Spacer(modifier = Modifier.height(16.dp))
        HorizontalDivider(color = BorderColor)
        Spacer(modifier = Modifier.height(16.dp))

        // Suggestions / Improvement Tips
        Text(
            text = "Security Advice & Suggestions:",
            style = MaterialTheme.typography.titleMedium,
            color = TextPrimary
        )
        Spacer(modifier = Modifier.height(8.dp))

        analysisResult.suggestions.forEach { suggestion ->
            Row(
                modifier = Modifier.padding(vertical = 4.dp),
                verticalAlignment = Alignment.Top
            ) {
                Text(text = "• ", color = statusColor, fontWeight = FontWeight.Bold)
                Text(
                    text = suggestion,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        CyberOutlinedButton(
            text = "CLEAR PASSWORD",
            onClick = onClear
        )
    }
}

@Composable
fun RequirementRow(label: String, met: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = if (met) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
            contentDescription = null,
            tint = if (met) SuccessGreen else TextMuted,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = if (met) TextPrimary else TextSecondary
        )
    }
}
