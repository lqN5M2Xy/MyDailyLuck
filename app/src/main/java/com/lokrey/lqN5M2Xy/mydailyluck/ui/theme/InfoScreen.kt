package com.lokrey.lqN5M2Xy.mydailyluck.ui.theme

import android.content.Intent
import android.text.util.Linkify
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lokrey.lqN5M2Xy.mydailyluck.ImprintActivity
import com.lokrey.lqN5M2Xy.mydailyluck.PrivacyPolicyActivity
import com.lokrey.lqN5M2Xy.mydailyluck.R
import com.lokrey.lqN5M2Xy.mydailyluck.TermsOfUseActivity
import com.lokrey.lqN5M2Xy.mydailyluck.utils.readRawText
import dev.jeziellago.compose.markdowntext.MarkdownText


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InfoScreen(onBackClick: () -> Unit) {
    val context = LocalContext.current
    val markdown = remember { context.readRawText(R.raw.app_info) }




        Scaffold(
            containerColor = Color(0xFFF5F5DC),
            topBar = {
                TopAppBar(
                    navigationIcon = {
                        IconButton(
                            onClick = onBackClick
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                            )
                        }
                    },
                    title = {
                        Text(
                            text = "Back",
                        )
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor             = Color.Blue,
                        navigationIconContentColor = Color.White,
                        titleContentColor          = Color.White
                    )
                )
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
                    .padding(innerPadding)
            ) {
                MarkdownText(
                    markdown = markdown,
                    style = MaterialTheme.typography.bodyLarge
                    .copy(textAlign = TextAlign.Center),
                    modifier = Modifier.fillMaxWidth(),
                    linkifyMask        = Linkify.WEB_URLS or Linkify.EMAIL_ADDRESSES

                )


                Button(
                    onClick = {
                        context.startActivity(Intent(context, PrivacyPolicyActivity::class.java))
                    },
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Blue)
                ) { Text("Privacy Policy",fontSize =24.sp) }

                Spacer(modifier = Modifier.height(28.dp))

                Button(
                    onClick = {
                        context.startActivity(Intent(context, ImprintActivity::class.java))
                    },
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Blue)
                ) { Text("Imprint",fontSize =24.sp) }

                Spacer(modifier = Modifier.height(28.dp))

                Button(
                    onClick = {
                        context.startActivity(Intent(context, TermsOfUseActivity::class.java))
                    },
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Blue)
                ) { Text("Terms of Use",fontSize =24.sp) }
            }
        }

}