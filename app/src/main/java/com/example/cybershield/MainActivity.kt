package com.example.cybershield

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.cybershield.core.theme.CyberShieldTheme
import com.example.cybershield.navigation.CyberShieldNavGraph

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CyberShieldTheme {
                CyberShieldNavGraph()
            }
        }
    }
}
