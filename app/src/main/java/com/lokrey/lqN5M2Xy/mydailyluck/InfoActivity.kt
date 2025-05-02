package com.lokrey.lqN5M2Xy.mydailyluck

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.lokrey.lqN5M2Xy.mydailyluck.ui.theme.InfoScreen

class InfoActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            InfoScreen(
                onBackClick = { finish() }  // Schließt die Activity
            )
        }
    }
}
