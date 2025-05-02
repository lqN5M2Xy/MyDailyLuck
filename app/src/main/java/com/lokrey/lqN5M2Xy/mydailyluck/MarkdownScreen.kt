package com.lokrey.lqN5M2Xy.mydailyluck

import android.text.util.Linkify
import androidx.annotation.RawRes
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.lokrey.lqN5M2Xy.mydailyluck.ui.theme.StandardTopBar
import com.lokrey.lqN5M2Xy.mydailyluck.utils.readRawText
import dev.jeziellago.compose.markdowntext.MarkdownText


// MarkdownScreen.kt
    @Composable
    fun MarkdownScreen(
        @RawRes resId: Int,
        screenTitle: String,
        onBackClick: () -> Unit
    ) {
        val context = LocalContext.current
        val markdown = remember { context.readRawText(resId) }

        Scaffold(
            containerColor = Color(0xFFF5F5DC),
            topBar = { StandardTopBar(title = screenTitle, onBackClick = onBackClick) }
        ) { innerPadding ->
            Column(
                Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
                    .padding(innerPadding)
            ) {
                MarkdownText(
                    markdown = markdown,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.fillMaxWidth(),
                    linkifyMask        = Linkify.WEB_URLS or Linkify.EMAIL_ADDRESSES,




                )
            }
        }
    }


