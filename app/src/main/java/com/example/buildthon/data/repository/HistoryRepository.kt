package com.example.buildthon.data.repository

import com.example.buildthon.data.model.QuizResultRecord
import com.example.buildthon.data.model.ScanHistoryRecord
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await

class HistoryRepository {
    private val firestore: FirebaseFirestore?
        get() = try {
            FirebaseFirestore.getInstance()
        } catch (e: Exception) {
            null
        }

    private val auth: FirebaseAuth?
        get() = try {
            FirebaseAuth.getInstance()
        } catch (e: Exception) {
            null
        }

    private val currentUserId: String?
        get() = try {
            auth?.currentUser?.uid
        } catch (e: Exception) {
            null
        }

    suspend fun getScans(): Result<List<ScanHistoryRecord>> {
        val db = firestore
        val uid = currentUserId
        if (db == null || uid == null) {
            return Result.success(getSampleScans())
        }

        return try {
            val snapshot = db.collection("users")
                .document(uid)
                .collection("scans")
                .orderBy("scannedAt", Query.Direction.DESCENDING)
                .get()
                .await()

            val scans = snapshot.documents.mapNotNull { doc ->
                doc.toObject(ScanHistoryRecord::class.java)?.copy(scanId = doc.id)
            }
            Result.success(if (scans.isEmpty()) getSampleScans() else scans)
        } catch (e: Exception) {
            Result.success(getSampleScans())
        }
    }

    suspend fun getQuizResults(): Result<List<QuizResultRecord>> {
        val db = firestore
        val uid = currentUserId
        if (db == null || uid == null) {
            return Result.success(getSampleQuizResults())
        }

        return try {
            val snapshot = db.collection("users")
                .document(uid)
                .collection("quizResults")
                .orderBy("completedAt", Query.Direction.DESCENDING)
                .get()
                .await()

            val results = snapshot.documents.mapNotNull { doc ->
                doc.toObject(QuizResultRecord::class.java)?.copy(resultId = doc.id)
            }
            Result.success(if (results.isEmpty()) getSampleQuizResults() else results)
        } catch (e: Exception) {
            Result.success(getSampleQuizResults())
        }
    }

    suspend fun deleteScan(scanId: String): Result<Boolean> {
        val db = firestore
        val uid = currentUserId
        if (db == null || uid == null) {
            return Result.success(true)
        }

        return try {
            db.collection("users")
                .document(uid)
                .collection("scans")
                .document(scanId)
                .delete()
                .await()
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun getSampleScans(): List<ScanHistoryRecord> {
        return listOf(
            ScanHistoryRecord("1", "URL", "https://github.com", "Safe", System.currentTimeMillis() - 3600000),
            ScanHistoryRecord("2", "URL", "http://fake-bank-login.xyz", "Phishing", System.currentTimeMillis() - 86400000),
            ScanHistoryRecord("3", "URL", "https://developer.android.com", "Safe", System.currentTimeMillis() - 172800000)
        )
    }

    private fun getSampleQuizResults(): List<QuizResultRecord> {
        return listOf(
            QuizResultRecord("q1", 90, 10, System.currentTimeMillis() - 4000000),
            QuizResultRecord("q2", 80, 10, System.currentTimeMillis() - 90000000)
        )
    }
}
