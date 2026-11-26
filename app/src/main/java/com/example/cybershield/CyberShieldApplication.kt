package com.example.cybershield

import android.app.Application
import com.google.firebase.FirebaseApp

class CyberShieldApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        try {
            FirebaseApp.initializeApp(this)
        } catch (_: Exception) {}
    }
}
