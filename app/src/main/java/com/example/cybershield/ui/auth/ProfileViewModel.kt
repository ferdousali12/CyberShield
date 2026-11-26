package com.example.cybershield.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cybershield.core.util.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.userProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.*

data class ProfileUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null,
    val displayName: String = "Cyber Cadet",
    val email: String = "",
    val memberSince: String = "January 2025",
    val totalScans: Int = 0,
    val threatsDetected: Int = 0,
    val quizAverageScore: Int = 0,
    val securityScore: Int = 75,
    val rank: String = "Cyber Cadet",
    val securityLevelLabel: String = "Medium",
    val isDemoUser: Boolean = false,
    val darkModeEnabled: Boolean = true,
    val dailyTipsEnabled: Boolean = true,
    val showEditDialog: Boolean = false,
    val showLogoutDialog: Boolean = false,
    val showDeleteDialog: Boolean = false
)

class ProfileViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        loadUserProfileData()
    }

    fun loadUserProfileData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val auth = FirebaseAuth.getInstance()
                val user = auth.currentUser
                val email = user?.email ?: ""
                val name = user?.displayName ?: email.substringBefore("@").replaceFirstChar { it.uppercase() }
                val isDemo = email.equals(Constants.DEMO_EMAIL, ignoreCase = true)
                
                val creationTimestamp = user?.metadata?.creationTimestamp ?: System.currentTimeMillis()
                val memberSinceStr = SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(Date(creationTimestamp))

                var scansCount = 0
                var threatsCount = 0
                var quizAvg = 0

                val uid = user?.uid
                if (uid != null) {
                    val db = FirebaseFirestore.getInstance()
                    
                    // Fetch Scans
                    try {
                        val scansSnapshot = db.collection("users").document(uid).collection("scans").get().await()
                        scansCount = scansSnapshot.size()
                        threatsCount = scansSnapshot.documents.count { doc ->
                            val res = doc.getString("result") ?: ""
                            res.equals("Phishing", ignoreCase = true) || res.equals("Dangerous", ignoreCase = true)
                        }
                    } catch (_: Exception) {}

                    // Fetch Quiz Results
                    try {
                        val quizSnapshot = db.collection("users").document(uid).collection("quizResults").get().await()
                        if (!quizSnapshot.isEmpty) {
                            var totalScore = 0
                            var count = 0
                            for (doc in quizSnapshot.documents) {
                                val score = doc.getLong("percentage")?.toInt() ?: doc.getLong("score")?.toInt() ?: 0
                                totalScore += score
                                count++
                            }
                            if (count > 0) {
                                quizAvg = totalScore / count
                            }
                        }
                    } catch (_: Exception) {}
                }

                // Calculate Security Score (0-100)
                val scanPoints = if (scansCount > 0) 25 else 10
                val threatBonus = if (threatsCount == 0) 25 else 15
                val quizPoints = ((quizAvg / 100f) * 30).toInt().coerceAtMost(30)
                val baseActivity = 20

                val securityScore = (scanPoints + threatBonus + quizPoints + baseActivity).coerceIn(0, 100)

                val rank = when {
                    securityScore >= 80 -> "Cyber Guardian"
                    securityScore >= 60 -> "Defender"
                    securityScore >= 40 -> "Cyber Cadet"
                    else -> "Beginner"
                }

                val levelLabel = when {
                    securityScore >= 71 -> "High"
                    securityScore >= 41 -> "Medium"
                    else -> "Low"
                }

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        displayName = if (name.isBlank()) "Cyber Cadet" else name,
                        email = email,
                        memberSince = memberSinceStr,
                        totalScans = scansCount,
                        threatsDetected = threatsCount,
                        quizAverageScore = quizAvg,
                        securityScore = securityScore,
                        rank = rank,
                        securityLevelLabel = levelLabel,
                        isDemoUser = isDemo
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.localizedMessage ?: "Failed to load profile data"
                    )
                }
            }
        }
    }

    fun updateDisplayName(newName: String, onComplete: () -> Unit) {
        if (_uiState.value.isDemoUser) {
            _uiState.update { it.copy(errorMessage = "Demo account profile cannot be modified.") }
            return
        }
        if (newName.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Display name cannot be empty") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val user = FirebaseAuth.getInstance().currentUser
                val profileUpdates = userProfileChangeRequest {
                    displayName = newName.trim()
                }
                user?.updateProfile(profileUpdates)?.await()

                val uid = user?.uid
                if (uid != null) {
                    val db = FirebaseFirestore.getInstance()
                    db.collection("users").document(uid).collection("profile").document("info")
                        .set(hashMapOf("displayName" to newName.trim(), "email" to user.email), SetOptions.merge())
                        .await()
                }

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        displayName = newName.trim(),
                        showEditDialog = false,
                        successMessage = "Profile updated successfully!"
                    )
                }
                onComplete()
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.localizedMessage ?: "Failed to update profile"
                    )
                }
            }
        }
    }

    fun deleteAccount(onDeleted: () -> Unit) {
        if (_uiState.value.isDemoUser) {
            _uiState.update { it.copy(errorMessage = "Demo account cannot be deleted.") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val user = FirebaseAuth.getInstance().currentUser
                val uid = user?.uid
                if (uid != null) {
                    val db = FirebaseFirestore.getInstance()
                    try {
                        db.collection("users").document(uid).delete().await()
                    } catch (_: Exception) {}
                }
                user?.delete()?.await()
                onDeleted()
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.localizedMessage ?: "Failed to delete account. Please re-authenticate."
                    )
                }
            }
        }
    }

    fun setDarkMode(enabled: Boolean) {
        _uiState.update { it.copy(darkModeEnabled = enabled) }
    }

    fun setDailyTips(enabled: Boolean) {
        _uiState.update { it.copy(dailyTipsEnabled = enabled) }
    }

    fun setEditDialogVisible(visible: Boolean) {
        if (_uiState.value.isDemoUser) return
        _uiState.update { it.copy(showEditDialog = visible) }
    }

    fun setLogoutDialogVisible(visible: Boolean) {
        _uiState.update { it.copy(showLogoutDialog = visible) }
    }

    fun setDeleteDialogVisible(visible: Boolean) {
        if (_uiState.value.isDemoUser) return
        _uiState.update { it.copy(showDeleteDialog = visible) }
    }

    fun clearMessages() {
        _uiState.update { it.copy(errorMessage = null, successMessage = null) }
    }
}
