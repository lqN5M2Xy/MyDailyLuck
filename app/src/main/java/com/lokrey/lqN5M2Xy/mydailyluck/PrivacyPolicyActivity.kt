package com.lokrey.lqN5M2Xy.mydailyluck

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent

class PrivacyPolicyActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MarkdownScreen(
                resId = R.raw.privacy_policy,
                screenTitle = "Privacy Policy",
                onBackClick = { finish() }
            )
        }
    }
}