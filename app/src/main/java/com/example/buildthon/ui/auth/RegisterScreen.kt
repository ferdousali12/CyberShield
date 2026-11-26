package com.example.buildthon.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.buildthon.core.components.CyberButton
import com.example.buildthon.core.components.CyberCard
import com.example.buildthon.core.components.CyberTextField
import com.example.buildthon.core.theme.*

@Composable
fun RegisterScreen(
    onRegisterSuccess: () -> Unit,
    onNavigateToLogin: () -> Unit,
    authViewModel: AuthViewModel
) {
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    val uiState by authViewModel.uiState.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Create Account",
                style = MaterialTheme.typography.headlineMedium,
                color = TextPrimary
            )

            Spacer(modifier = Modifier.height(24.dp))

            CyberCard {
                Text(
                    text = "Join CyberShield",
                    style = MaterialTheme.typography.titleLarge,
                    color = TextPrimary
                )

                Spacer(modifier = Modifier.height(16.dp))

                CyberTextField(
                    value = fullName,
                    onValueChange = { fullName = it; authViewModel.clearError() },
                    label = "Full Name",
                    leadingIcon = Icons.Default.Person
                )

                Spacer(modifier = Modifier.height(12.dp))

                CyberTextField(
                    value = email,
                    onValueChange = { email = it; authViewModel.clearError() },
                    label = "Email Address",
                    leadingIcon = Icons.Default.Email,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                )

                Spacer(modifier = Modifier.height(12.dp))

                CyberTextField(
                    value = password,
                    onValueChange = { password = it; authViewModel.clearError() },
                    label = "Password (min 6 chars)",
                    leadingIcon = Icons.Default.Lock,
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
                )

                Spacer(modifier = Modifier.height(12.dp))

                CyberTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it; authViewModel.clearError() },
                    label = "Confirm Password",
                    leadingIcon = Icons.Default.Lock,
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    trailingIcon = {
                        TextButton(onClick = { passwordVisible = !passwordVisible }) {
                            Text(text = if (passwordVisible) "Hide" else "Show", color = PrimaryBlue, fontSize = 12.sp)
                        }
                    }
                )

                if (uiState.errorMessage != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = uiState.errorMessage ?: "",
                        color = DangerRed,
                        style = MaterialTheme.typography.bodyMedium,
                        fontSize = 13.sp
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                if (uiState.isLoading) {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = PrimaryBlue)
                    }
                } else {
                    CyberButton(
                        text = "REGISTER",
                        onClick = {
                            authViewModel.register(email, password, confirmPassword, onRegisterSuccess)
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(onClick = onNavigateToLogin) {
                Text(
                    text = "Already have an account? Login",
                    color = PrimaryBlue,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}
