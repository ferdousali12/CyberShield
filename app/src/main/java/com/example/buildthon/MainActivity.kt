package com.example.buildthon

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.buildthon.core.theme.CyberShieldTheme
import com.example.buildthon.navigation.CyberShieldNavGraph

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
