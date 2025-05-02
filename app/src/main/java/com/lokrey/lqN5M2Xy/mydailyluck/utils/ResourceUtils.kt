package com.lokrey.lqN5M2Xy.mydailyluck.utils

import android.content.Context
import androidx.annotation.RawRes

/**
 * Liest den Inhalt einer Raw‑Resource als String.
 */
fun Context.readRawText(@RawRes resId: Int): String =
    resources.openRawResource(resId)
        .bufferedReader()
        .use { it.readText() }
