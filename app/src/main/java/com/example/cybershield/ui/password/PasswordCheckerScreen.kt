package com.example.cybershield.ui.password

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.cybershield.core.components.CyberTopBar
import com.example.cybershield.core.theme.DarkBackground

@Composable
fun PasswordCheckerScreen(
    viewModel: PasswordViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        CyberTopBar(title = "Password Strength Checker")

        Spacer(modifier = Modifier.height(16.dp))

        PasswordCheckerComponent(
            passwordInput = uiState.passwordInput,
            isPasswordVisible = uiState.isPasswordVisible,
            analysisResult = uiState.analysisResult,
            onPasswordChange = { viewModel.updatePassword(it) },
            onToggleVisibility = { viewModel.toggleVisibility() },
            onClear = { viewModel.clearPassword() }
        )
    }
}
