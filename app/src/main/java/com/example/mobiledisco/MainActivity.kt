package com.example.mobiledisco

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.example.mobiledisco.ui.theme.MobileDiscoTheme
import com.example.mobiledisco.ui.screens.MobileDiscoScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MobileDiscoTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MobileDiscoScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}
