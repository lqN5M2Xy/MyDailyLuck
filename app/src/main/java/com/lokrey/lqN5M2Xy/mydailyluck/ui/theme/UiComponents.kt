package com.lokrey.lqN5M2Xy.mydailyluck.ui.theme

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color



    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun StandardTopBar(
        title: String,
        onBackClick: () -> Unit
    ) {
        TopAppBar(
            navigationIcon = {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            },
            title = { Text(text = title) },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.Blue,
                navigationIconContentColor = Color.White,
                titleContentColor = Color.White
            )
        )
    }

