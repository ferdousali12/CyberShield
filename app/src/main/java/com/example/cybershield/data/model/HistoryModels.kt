package com.example.cybershield.data.model

data class ScanHistoryRecord(
    val scanId: String = "",
    val type: String = "URL",
    val target: String = "",
    val result: String = "Safe", // Safe / Phishing
    val scannedAt: Long = System.currentTimeMillis()
)

data class QuizResultRecord(
    val resultId: String = "",
    val score: Int = 0,
    val totalQuestions: Int = 10,
    val completedAt: Long = System.currentTimeMillis()
)
