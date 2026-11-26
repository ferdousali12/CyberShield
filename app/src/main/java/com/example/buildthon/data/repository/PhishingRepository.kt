package com.example.buildthon.data.repository

import com.example.buildthon.data.model.ScanResultModel
import com.example.buildthon.data.model.ThreatLevel
import kotlinx.coroutines.delay

class PhishingRepository {

    /**
     * Performs threat analysis.
     * Security Note: In production, this calls a secure backend API (e.g., Firebase Cloud Functions
     * or custom server proxy) holding API keys (Safe Browsing / VirusTotal), ensuring keys are never exposed in the APK.
     */
    suspend fun analyzeUrl(url: String): Result<ScanResultModel> {
        return try {
            delay(1500L) // Simulate secure network latency

            val normalizedUrl = url.lowercase().trim()
            val reasons = mutableListOf<String>()
            var riskScore = 10 // Base baseline risk

            // Heuristic Checks
            if (!normalizedUrl.startsWith("https://")) {
                riskScore += 30
                reasons.add("Insecure protocol: URL does not use HTTPS encryption.")
            }

            if (normalizedUrl.contains("login") || normalizedUrl.contains("verify") || normalizedUrl.contains("secure") || normalizedUrl.contains("account")) {
                riskScore += 35
                reasons.add("Contains high-risk credential harvesting keywords ('login', 'verify', 'secure').")
            }

            if (normalizedUrl.contains(".xyz") || normalizedUrl.contains(".info") || normalizedUrl.contains(".top") || normalizedUrl.contains(".free")) {
                riskScore += 40
                reasons.add("Uses a suspicious Top-Level Domain (TLD) frequently associated with phishing campaigns.")
            }

            if (normalizedUrl.contains("@") || normalizedUrl.contains("--")) {
                riskScore += 45
                reasons.add("Contains obfuscation patterns or ambiguous redirect characters.")
            }

            val threatLevel = when {
                riskScore >= 70 -> ThreatLevel.DANGEROUS
                riskScore >= 35 -> ThreatLevel.SUSPICIOUS
                else -> ThreatLevel.SAFE
            }

            if (reasons.isEmpty()) {
                reasons.add("Domain exhibits standard formatting with no known heuristic threat signatures detected.")
            }

            val result = ScanResultModel(
                url = url,
                threatLevel = threatLevel,
                riskScore = riskScore.coerceIn(0, 100),
                reasons = reasons
            )

            Result.success(result)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
