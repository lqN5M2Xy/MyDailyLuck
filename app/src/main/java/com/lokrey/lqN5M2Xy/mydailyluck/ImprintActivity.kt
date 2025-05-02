package com.lokrey.lqN5M2Xy.mydailyluck

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent

class ImprintActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MarkdownScreen(
                resId = R.raw.imprint,
                screenTitle = "Imprint",
                onBackClick = { finish() }
            )
        }
    }
}