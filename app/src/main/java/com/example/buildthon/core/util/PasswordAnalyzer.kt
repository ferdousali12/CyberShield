package com.example.buildthon.core.util

enum class PasswordStrength(val label: String, val score: Int) {
    VERY_WEAK("Very Weak", 1),
    WEAK("Weak", 2),
    MEDIUM("Medium", 3),
    STRONG("Strong", 4),
    VERY_STRONG("Very Strong", 5)
}

data class PasswordAnalysisResult(
    val strength: PasswordStrength,
    val score: Int, // 1 to 5
    val hasMinLength: Boolean,
    val hasUppercase: Boolean,
    val hasLowercase: Boolean,
    val hasDigit: Boolean,
    val hasSpecialChar: Boolean,
    val suggestions: List<String>
)

object PasswordAnalyzer {
    fun evaluate(password: String): PasswordAnalysisResult {
        if (password.isEmpty()) {
            return PasswordAnalysisResult(
                strength = PasswordStrength.VERY_WEAK,
                score = 0,
                hasMinLength = false,
                hasUppercase = false,
                hasLowercase = false,
                hasDigit = false,
                hasSpecialChar = false,
                suggestions = listOf("Enter a password to begin security analysis.")
            )
        }

        val hasMinLength = password.length >= 8
        val hasUppercase = password.any { it.isUpperCase() }
        val hasLowercase = password.any { it.isLowerCase() }
        val hasDigit = password.any { it.isDigit() }
        val hasSpecialChar = password.any { !it.isLetterOrDigit() }

        var score = 0
        if (password.length >= 8) score++
        if (password.length >= 12) score++
        if (hasUppercase) score++
        if (hasLowercase) score++
        if (hasDigit) score++
        if (hasSpecialChar) score++

        val strength = when {
            score <= 1 -> PasswordStrength.VERY_WEAK
            score == 2 -> PasswordStrength.WEAK
            score == 3 -> PasswordStrength.MEDIUM
            score == 4 -> PasswordStrength.STRONG
            else -> PasswordStrength.VERY_STRONG
        }

        val suggestions = mutableListOf<String>()
        if (!hasMinLength) suggestions.add("Increase password length to at least 8 characters (12+ recommended).")
        if (!hasUppercase) suggestions.add("Add uppercase letters (A-Z).")
        if (!hasLowercase) suggestions.add("Add lowercase letters (a-z).")
        if (!hasDigit) suggestions.add("Include numeric digits (0-9).")
        if (!hasSpecialChar) suggestions.add("Add special characters (!@#\$%^&*).")

        if (suggestions.isEmpty()) {
            suggestions.add("Excellent password entropy! Well protected against brute-force attacks.")
        }

        return PasswordAnalysisResult(
            strength = strength,
            score = score.coerceIn(1, 5),
            hasMinLength = hasMinLength,
            hasUppercase = hasUppercase,
            hasLowercase = hasLowercase,
            hasDigit = hasDigit,
            hasSpecialChar = hasSpecialChar,
            suggestions = suggestions
        )
    }
}
