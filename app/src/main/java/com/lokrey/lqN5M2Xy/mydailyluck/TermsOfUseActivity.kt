package com.lokrey.lqN5M2Xy.mydailyluck

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent

class TermsOfUseActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MarkdownScreen(
                resId = R.raw.terms_of_use,
                screenTitle = "Terms of Use",
                onBackClick = { finish() }
            )
        }
    }
}