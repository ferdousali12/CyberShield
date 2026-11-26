package com.example.cybershield.data.model

enum class ThreatLevel {
    SAFE, SUSPICIOUS, DANGEROUS
}

data class ScanResultModel(
    val url: String,
    val threatLevel: ThreatLevel,
    val riskScore: Int, // 0 to 100
    val reasons: List<String>,
    val timestamp: Long = System.currentTimeMillis()
)
