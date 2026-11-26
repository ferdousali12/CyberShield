package com.example.cybershield.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.tasks.await

class AuthRepository {
    private val auth: FirebaseAuth?
        get() = try {
            FirebaseAuth.getInstance()
        } catch (_: Exception) {
            null
        }

    val currentUser: FirebaseUser?
        get() = try {
            auth?.currentUser
        } catch (_: Exception) {
            null
        }

    suspend fun login(email: String, pass: String): Result<FirebaseUser?> {
        val firebaseAuth = auth ?: return Result.failure(
            Exception("Firebase not initialized. Please add google-services.json to the app/ folder.")
        )
        return try {
            val result = firebaseAuth.signInWithEmailAndPassword(email, pass).await()
            Result.success(result.user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun register(email: String, pass: String): Result<FirebaseUser?> {
        val firebaseAuth = auth ?: return Result.failure(
            Exception("Firebase not initialized. Please add google-services.json to the app/ folder.")
        )
        return try {
            val result = firebaseAuth.createUserWithEmailAndPassword(email, pass).await()
            Result.success(result.user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun logout() {
        try {
            auth?.signOut()
        } catch (_: Exception) {}
    }
}
