// My Daily Luck - v1.0

package com.lokrey.lqN5M2Xy.mydailyluck

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.ActivityInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.imeNestedScroll
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.lokrey.lqN5M2Xy.mydailyluck.utils.IntegrityValidator
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import java.security.MessageDigest
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit
import kotlin.math.roundToInt


class MainActivity : ComponentActivity() {

    private lateinit var integrityValidator: IntegrityValidator

    // SharedPreferences zum Speichern des Integritätsergebnisses
    private val sharedPreferences by lazy {
        getSharedPreferences("app_prefs", MODE_PRIVATE)
    }

    // Prüfen, ob die Integritätsprüfung bereits durchgeführt wurde
    private fun shouldCheckIntegrity(): Boolean {
        return sharedPreferences.getBoolean("integrity_checked", false).not()
    }

    // Integrität als erfolgreich markieren
    private fun saveIntegrityCheck() {
        sharedPreferences.edit().putBoolean("integrity_checked", true).apply()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)

        // Import: androidx.core.view.WindowCompat
        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)


        if (shouldCheckIntegrity()) {
            integrityValidator =
                IntegrityValidator(this)

            integrityValidator.checkIntegrity(  // Führt den Check durch; wirft eine Exception bei Fehler
                onSuccess = {
                    saveIntegrityCheck()  // Speichern, dass die Prüfung erfolgt ist
                    Log.d("MainActivity", "Integrity check successful")
                },
                onFailure = { exception ->

                    Toast.makeText(this, "Integrity check failed!", Toast.LENGTH_LONG).show()
                    Log.d("MainActivity", "Integrity check failed: ${exception.message}")
                    //finish()  // Schließt die App, wenn der Check fehlschlägt
                }
            )
        } else {
            Log.d("MainActivity", "Integrity check has already been performed")
        }

        if (isDeviceRootedAdvanced(this)) {
            // Reaktion auf Root-Erkennung
            Toast.makeText(this, "Root access detected! App is closing.", Toast.LENGTH_LONG).show()
            //finish()
        }
        if (BuildConfig.DEBUG) {
            Toast.makeText(this, "Debugging detected! App will close.", Toast.LENGTH_SHORT).show()

            //android.os.Process.killProcess(android.os.Process.myPid())
        }

        if (!isSignatureValid()) {
            Toast.makeText(this, "Signature check failed!", Toast.LENGTH_LONG).show()
           //finish()
        }


        val sharedPreferences = getSharedPreferences("MyDailyLuckPrefs", MODE_PRIVATE)

        setContent {
            MyDailyLuckApp(sharedPreferences)
        }
        requestNotificationPermission() //  Berechtigungsabfrage aufrufen

        scheduleDailyCheck(this)
    }


    override fun onDestroy() {
        super.onDestroy()
        // Aufräumen der Integritätsprüfung
        if (::integrityValidator.isInitialized) {
            integrityValidator.destroy()
        }
    }

    @OptIn(ExperimentalLayoutApi::class)
    @Composable
    fun MyDailyLuckApp(sharedPreferences: SharedPreferences) {
        val context = LocalContext.current
        var userInput by remember { mutableStateOf("") }
        var attemptsLeft by remember { mutableStateOf(1) }
        var lastAttemptTime by remember { mutableStateOf(getLastAttemptTime(sharedPreferences)) }
        var nextAttemptTime by remember { mutableStateOf(sharedPreferences.getLong("next_attempt_time", 0L)) }
        val focusManager = LocalFocusManager.current
        var hasClicked by remember { mutableStateOf(false) }

        var resultText by remember {
            mutableStateOf(sharedPreferences.getString("last_result", "") ?: "")
        }
        var randomGenerated by remember {
            mutableStateOf(sharedPreferences.getString("last_random", "") ?: "")
        }
        var emoji by remember {
            mutableStateOf(sharedPreferences.getString("last_emoji", "") ?: "")
        }
        var recommendation by remember {
            mutableStateOf(sharedPreferences.getString("last_recommendation", "") ?: "")
        }
        var submittedInput by remember {
            mutableStateOf(sharedPreferences.getString("last_input", "") ?: "")
        }

        val systemUi = rememberSystemUiController()


        val scrollState = rememberScrollState()
        val coroutineScope = rememberCoroutineScope()
        val imeBottomPx = WindowInsets.ime.getBottom(LocalDensity.current)

        LaunchedEffect(imeBottomPx) {
            if (imeBottomPx > 0 && resultText != "Invalid Input") {
                // animateScrollTo braucht die Höhe in Pixel, scrollState.maxValue ist in Scroll‑Pixels

                scrollState.animateScrollTo(scrollState.maxValue)
            }
        }

        SideEffect {
            systemUi.setStatusBarColor(
                color = Color(0xFF6200EE),  // deine Wunsch‑Farbe
                darkIcons = true             // true = dunkle Icons (bei hellem Hintergrund), false = helle Icons
            )
        }


        LaunchedEffect(Unit) {
            val now = System.currentTimeMillis()
            val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(now))
            val lastAttemptDate =
                SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(lastAttemptTime))
            if (currentDate == lastAttemptDate || now - lastAttemptTime < 12 * 60 * 60 * 1000) {
                attemptsLeft = 0
            }
            if (attemptsLeft > 0) {
                resultText = ""
                emoji = ""
                recommendation = ""
                randomGenerated = ""
                submittedInput = ""
            }
        }
        Box(modifier = Modifier.fillMaxSize()) {
            // Dein bisheriger Root-Container
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxSize()
                    .imePadding()
                    .background(Color(0xFFF5F5DC))

            ) {

            val screenWidth = maxWidth
            val burgundy = Color(0xFF8E0FED)



            Column(
                modifier = Modifier.fillMaxWidth()
                    .fillMaxSize()
                    .fillMaxHeight() // Wichtig!
                    .verticalScroll(scrollState) // Scroll-Option aktivieren
                    .imeNestedScroll()
                    .padding(screenWidth * 0.05f),

                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Spacer(modifier = Modifier.height(48.dp))
                Text("🍀", fontSize = safeResponsiveFontSize(0.3f, screenWidth))
                Spacer(modifier = Modifier.height(8.dp))

                if (attemptsLeft == 1
                ) {
                    if (!hasClicked) {
                        Text(
                            "My Daily Luck",
                            fontSize = safeResponsiveFontSize(0.12f, screenWidth),
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1d6b32)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            "See how much luck the universe gives you today!",
                            fontSize = safeResponsiveFontSize(0.06f, screenWidth),
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(18.dp))

                        Text(
                            "Try to estimate the randomly generated number (1-100) assigned to you each day. " +
                                    "Your luck is determined by how close your chosen number is to this value. " +
                                    "The closer your guess is, the higher your luck level for today.",
                            fontSize = safeResponsiveFontSize(0.05f, screenWidth),
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center,
                            color = Color.Blue
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = userInput,
                        onValueChange = {
                            if (it.all { char -> char.isDigit() } && it.length <= 3) userInput = it
                        },
                        label = {
                            Text(
                                "Enter a number (1-100)",
                                fontSize = safeResponsiveFontSize(0.03f, screenWidth),
                                textAlign = TextAlign.Center
                            )
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number
                        )
                        ,
                        modifier = Modifier
                            .width(screenWidth * 0.5f)
                            .height((screenWidth * 0.4f) * 0.4f)  // Höhe ist 40% der Breite

                            .onFocusChanged { focus ->
                                if (focus.isFocused) {
                                    coroutineScope.launch {
                                        // scrollt ans Ende (Button liegt ja im Layout ganz unten)
                                        scrollState.animateScrollTo(scrollState.maxValue)
                                    }
                                }
                            }


                        ,
                        textStyle = TextStyle(
                            fontSize = safeResponsiveFontSize(0.04f, screenWidth),
                            textAlign = TextAlign.Center
                        )
                    )
                    Spacer(modifier = Modifier.height(22.dp))

                    Button(onClick = {
                        hasClicked = true
                        if (attemptsLeft > 0) {
                            focusManager.clearFocus()
                            val userNumber = userInput.toIntOrNull()

                            if (userNumber in 1..100) {

                                val (score, generated, symbol, advice) = checkLuck(userInput)

                                val displayNumber = userNumber?.toString() ?: userInput

                                resultText = score
                                randomGenerated = generated
                                emoji = symbol
                                recommendation = advice
                                submittedInput = displayNumber

                                attemptsLeft = 0
                                lastAttemptTime = System.currentTimeMillis()
                                saveLastAttemptTime(sharedPreferences, lastAttemptTime)
                                // Berechne den nächsten Versuchzeitpunkt und speichere ihn in SharedPreferences
                                nextAttemptTime = calculateNextAttemptTime(lastAttemptTime)

                                sharedPreferences.edit()
                                    .putLong("next_attempt_time", nextAttemptTime)
                                    .putString("last_input", submittedInput)
                                    .putString("last_random", randomGenerated)
                                    .putString("last_result", resultText)
                                    .putString("last_emoji", emoji)
                                    .putString("last_recommendation", recommendation)
                                    .apply()
                            } else {
                                // Nur Text anzeigen, nichts speichern
                                resultText = "Invalid Input"
                                emoji = "❌"
                                recommendation = " \n Please enter a number between 1 and 100!"
                            }
                        }
                    }, enabled = attemptsLeft > 0,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Blue),
                    modifier = Modifier.height(50.dp)
                        )
                    {
                        Text("Check Luck \uD83C\uDF40")
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    if (!hasClicked) {
                        Text(
                            text = "Your daily luck check is available ✅",
                            fontSize = safeResponsiveFontSize(0.06f, screenWidth),
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            color = Color(0xFF0a8a2c)
                        )
                    }
                }

                if (submittedInput.isNotEmpty()) {
                    Text(
                        text = buildAnnotatedString {
                            append("\uD83D\uDD22 You guessed: ")
                            withStyle(style = SpanStyle(color = burgundy)) {
                                append(submittedInput)
                            }
                        },
                        fontSize = safeResponsiveFontSize(0.06f, screenWidth),
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                if (randomGenerated.isNotEmpty()) {

                    Text(
                        text = buildAnnotatedString {
                            append("\uD83C\uDFB2 Random number was: ")
                            withStyle(style = SpanStyle(color = burgundy)) {
                                append(randomGenerated)
                            }
                        },
                        fontSize = safeResponsiveFontSize(0.06f, screenWidth),
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(32.dp))
                }

                if (resultText.isNotEmpty()) {
                    Text("Your Luck Score today is:", fontSize = safeResponsiveFontSize(0.06f, screenWidth), fontWeight = FontWeight.Bold, color = Color.DarkGray)
                    Text(
                        resultText,
                        fontSize = safeResponsiveFontSize(0.08f, screenWidth),
                        fontWeight = FontWeight.Bold,
                        color = getLuckColor(resultText),
                    )
                    Text(emoji, fontSize = safeResponsiveFontSize(0.3f, screenWidth))
                }

                if (recommendation.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        recommendation,
                        fontSize = safeResponsiveFontSize(0.06f, screenWidth),
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        color = Color.DarkGray
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                }

                CountdownTimer(lastAttemptTime, attemptsLeft, nextAttemptTime)

                Spacer(modifier = Modifier.height(64.dp))

            }



            // --- 3) Overlay-Button oben rechts ---
            IconButton(
                onClick = {
                    context.startActivity(
                        Intent(context, InfoActivity::class.java)
                    )
                },
                modifier = Modifier
                    .align(Alignment.TopEnd)   // oben rechts im Box-Container
                    .padding(top = 40.dp, end = 12.dp)           // etwas Abstand zum Rand
                    // Gesamt‑Größe des Buttons
                    .size(screenWidth * 0.20f),
                colors = IconButtonDefaults.iconButtonColors(
                    contentColor   = Color.Blue   // Standard-Farbe für Icon und Ripple
                )
            ) {
                Icon(Icons.Default.Info, contentDescription = "Info",
                // reine Icon‑Größe
                modifier = Modifier.size(screenWidth * 0.2f)
                )
            }
        }
    }
    }

    // Timer für nächsten Versuch
    @Composable
    fun CountdownTimer(lastAttemptTime: Long, attemptsLeft: Int, nextAttemptTime: Long) {
        var remainingTime by remember(lastAttemptTime) { // Aktualisiert sich sofort, wenn lastAttemptTime sich ändert!
            mutableLongStateOf(calculateRemainingTime(lastAttemptTime))
        }

        LaunchedEffect(lastAttemptTime) { // Immer aktualisieren, wenn lastAttemptTime sich ändert
            while (remainingTime > 0) {
                delay(1000)
                remainingTime = calculateRemainingTime(lastAttemptTime)

            }
        }
        BoxWithConstraints( // Responsive Layout beginnt hier
            modifier = Modifier.fillMaxSize().fillMaxSize()
        ) {
            val screenWidth = maxWidth
            val timeFormat = SimpleDateFormat("hh:mm a", Locale.US)
            val readableTime = timeFormat.format(Date(nextAttemptTime))

            Column(
                modifier = Modifier.fillMaxWidth()
                    .fillMaxSize()
                    .fillMaxHeight()
                    ,

                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (attemptsLeft == 0) {

                    Text(
                        text = buildAnnotatedString {
                            append("Your next daily luck check is available tomorrow at: ")
                            withStyle(style = SpanStyle(color = Color(0xFF800080))) { // Hier wird nur $readableTime grün
                                append(readableTime)
                            }
                        },
                        fontSize = safeResponsiveFontSize(0.06f, screenWidth),
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        color = Color.Red // Dieser Wert gilt für den gesamten Text, außer in Spans, wo du es überschreibst
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = buildAnnotatedString {
                            withStyle(style = SpanStyle(color = Color.Red)) {
                                append("⌛ Time remaining: ")
                            }
                            withStyle(style = SpanStyle(color = Color.Red)) { // Beispiel: Dunkelgrün für die verbleibende Zeit
                                append(formatTimeRemaining(remainingTime))
                            }
                        },
                        fontSize = safeResponsiveFontSize(0.06f, screenWidth),
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )

                }
            }
        }
    }

    @Composable
    fun safeResponsiveFontSize(baseFactor: Float, screenWidth: Dp): TextUnit {
        val density = LocalDensity.current
        return if (screenWidth > 0.dp) {
            with(density) { (screenWidth * baseFactor).toSp() }
        } else {
            16.sp // Default fallback
        }
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // Android 13+
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                == PackageManager.PERMISSION_GRANTED) {
                return // Falls bereits erlaubt, nichts tun
            }
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.POST_NOTIFICATIONS), 1
            )
        }
    }

    private fun checkLuck(input: String): Quadruple<String, String, String, String> {
        if (input.isEmpty()) return Quadruple("", "", "", "")

        val E = input.toIntOrNull()
        if (E == null || E !in 1..100) {
            return Quadruple("Invalid Input", "N/A", "❌", "Check your input!")
        }

        val R = (1..100).random()

        if (E == R) {
            return Quadruple("GREAT LUCK!", R.toString(), "\uD83C\uDF89", "Wow! This is your lucky day, go make the best of it!")
        }

        var U = (E - 100.0 / 6).roundToInt()
        var O = (E + 100.0 / 6).roundToInt()

        if (U < 0) {
            O += -U
            U = 0
        }
        if (O > 100) {
            U -= (O - 100)
            O = 100
        }

        var A = (E - 100.0 / 3).roundToInt()
        var Y = (E + 100.0 / 3).roundToInt()

        if (A < 0) {
            Y += -A
            A = 0
        }
        if (Y > 100) {
            A -= (Y - 100)
            Y = 100
        }

        val score = when {
            R in U..O -> "GOOD LUCK"
            R in A..Y -> "AVERAGE LUCK"
            else -> "BAD LUCK"
        }

        val emoji = when (score) {
            "BAD LUCK" -> "\uD83D\uDE41"
            "AVERAGE LUCK" -> "\uD83D\uDE10"
            "GOOD LUCK" -> "\uD83D\uDE42"
            else -> "❌"
        }

        val advice = when (score) {
            "BAD LUCK" -> "Even bad luck can lead to something good. Stay positive!"
            "AVERAGE LUCK" -> "You're somewhere in the middle. Not bad!"
            "GOOD LUCK" -> "You have above-average luck today!"
            else -> "Invalid input."
        }

        return Quadruple(score, R.toString(), emoji, advice)
    }

    private fun getLuckColor(score: String): Color {
        return when (score) {
            "BAD LUCK" -> Color.Blue
            "AVERAGE LUCK" -> Color.Green
            "GOOD LUCK" -> Color.Red
            "GREAT LUCK!" -> Color.Magenta
            else -> Color.Black
        }
    }

    private fun calculateRemainingTime(lastAttemptTime: Long): Long {
        val currentTime = System.currentTimeMillis()
        // Mindestwartezeit: 12 Stunden nach dem letzten Versuch
        val twelveHoursLater = lastAttemptTime + (12 * 60 * 60 * 1000)

        // Nächstmöglicher Versuch: Frühestens um Mitternacht des nächsten Tages
        val calendar = Calendar.getInstance().apply {
            timeInMillis = lastAttemptTime
            add(Calendar.DAY_OF_YEAR, 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val midnightNextDay = calendar.timeInMillis

        // Der spätere Wert von beiden entscheidet
        val nextAttemptTime = maxOf(twelveHoursLater, midnightNextDay)
        return (nextAttemptTime - currentTime).coerceAtLeast(0)
    }

    private fun calculateNextAttemptTime(lastAttemptTime: Long): Long {
        val twelveHoursLater = lastAttemptTime + (12 * 60 * 60 * 1000)
        val calendar = Calendar.getInstance().apply {
            timeInMillis = lastAttemptTime
            add(Calendar.DAY_OF_YEAR, 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val midnightNextDay = calendar.timeInMillis
        return maxOf(twelveHoursLater, midnightNextDay)
    }

    private fun formatTimeRemaining(timeMillis: Long): String {
        val hours = timeMillis / (1000 * 60 * 60)
        val minutes = (timeMillis / (1000 * 60)) % 60
        val seconds = (timeMillis / 1000) % 60
        return String.format(Locale.US, "%02d:%02d:%02d", hours, minutes, seconds)

    }

    private fun getLastAttemptTime(sharedPreferences: SharedPreferences): Long {
        return sharedPreferences.getLong("last_attempt_time", 0L)
    }

    private fun saveLastAttemptTime(sharedPreferences: SharedPreferences, time: Long) {
        sharedPreferences.edit().putLong("last_attempt_time", time).apply()
    }

    data class Quadruple<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)

    companion object {

        fun scheduleDailyCheck(context: Context) {
            // 1. Zeitverzögerung berechnen (Millisekunden bis z.B. 08:00 Uhr)
            val delayMillis = calculateDelayForTime(8, 0, second = 5)
            // Beachte: Du hast ja schon eine ähnliche Funktion "calculateInitialDelay()".
            // Einfach anpassen, damit sie immer 8:00 (oder deine Zeit) zurückgibt.
            Log.d("DailyNotificationWorker", "Verzögerung für Benachrichtigung: ${delayMillis / 1000} Sekunden")

            Log.d("WorkManager", "WorkManager wurde erfolgreich initialisiert.")
            // 2. OneTimeWorkRequest bauen
            val request = OneTimeWorkRequestBuilder<DailyNotificationWorker>()
                .setInitialDelay(delayMillis, TimeUnit.MILLISECONDS)
                .build()

            // 3. Einmalige UniqueWork starten
            Log.d("MainActivity", "🔄 Vor Worker-Start")
            WorkManager.getInstance(context).enqueueUniqueWork(
                "daily_luck_notification",
                ExistingWorkPolicy.REPLACE,
                request
            )
            Log.d("MainActivity", "🔄 Nach Worker-Start")
        }


        fun calculateDelayForTime(hour: Int, minute: Int, second: Int): Long {
            val now = Calendar.getInstance()
            val target = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, hour)
                set(Calendar.MINUTE, minute)
                set(Calendar.SECOND, second)
                set(Calendar.MILLISECOND, 0)
                if (before(now)) add(
                    Calendar.DAY_OF_YEAR,
                    1
                )  // Falls die Zeit heute schon vorbei ist
            }
            return target.timeInMillis - now.timeInMillis
        }
    }
    fun checkSuExists(): Boolean {
        val paths = arrayOf(
            "/system/app/Superuser.apk",
            "/system/xbin/su",
            "/system/bin/su",
            "/system/sbin/su",
            "/sbin/su",
            "/vendor/bin/su"
        )
        for (path in paths) {
            if (File(path).exists()) return true
        }
        return false
    }

    fun checkTestKeys(): Boolean {
        return Build.TAGS?.contains("test-keys") ?: false
    }

    fun canExecuteSuCommand(): Boolean {
        return try {
            val process = Runtime.getRuntime().exec(arrayOf("which", "su"))
            val output = process.inputStream.bufferedReader().readText()
            output.isNotEmpty()
        } catch (e: Exception) {
            false
        }
    }

    fun checkForRootApps(context: Context): Boolean {
        val knownRootApps = listOf(
            "com.noshufou.android.su",
            "com.thirdparty.superuser",
            "eu.chainfire.supersu",
            "com.koushikdutta.superuser",
            "com.topjohnwu.magisk"  // Magisk Manager, falls vorhanden
        )
        val pm = context.packageManager
        for (app in knownRootApps) {
            try {
                pm.getPackageInfo(app, 0)
                return true
            } catch (e: PackageManager.NameNotFoundException) {
                // App nicht gefunden, weiter machen
            }
        }
        return false
    }

    fun isDeviceRootedAdvanced(context: Context): Boolean {
        return checkSuExists() || checkTestKeys() || canExecuteSuCommand() || checkForRootApps(context)
    }

    fun isSignatureValid(): Boolean {
        return try {
            val packageName = applicationContext.packageName
            val packageInfo: PackageInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                // Ab Android 9 (Pie) verwenden wir GET_SIGNING_CERTIFICATES
                packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNING_CERTIFICATES)
            } else {
                // Vor Android 9 verwenden wir GET_SIGNATURES (deprecated)
                @Suppress("DEPRECATION")
                packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNATURES)
            }

            val validSignature = "Aus Sicherheitsgründen entfernt" // Ersetze durch deinen tatsächlichen SHA256-Hash

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                // Android 9 und höher: Nutzung von signingInfo
                packageInfo.signingInfo?.apkContentsSigners?.any { signature ->
                    val signatureHash = getSignatureHash(signature.toByteArray())
                    Log.d("SignatureCheck", "App Signature (ab Android P): $signatureHash")
                    signatureHash == validSignature
                } ?: false
            } else {
                // Android 8 und älter: Nutzung von signatures
                @Suppress("DEPRECATION")
                packageInfo.signatures?.any { signature ->
                    val signatureHash = getSignatureHash(signature.toByteArray())
                    Log.d("SignatureCheck", "App Signature (vor Android P): $signatureHash")
                    signatureHash == validSignature
                } ?: false
            }
        } catch (e: Exception) {
            Log.e("SignatureCheck", "Error during signature verification: ${e.message}")
            false
        }
    }

    fun getSignatureHash(signature: ByteArray): String {
        return try {
            val md = MessageDigest.getInstance("SHA-256")
            val hash = md.digest(signature)
            hash.joinToString(":") { String.format("%02X", it) }
        } catch (e: Exception) {
            Log.e("SignatureCheck", "Hashing error: ${e.message}")
            ""
        }
    }

}




/*
    Copyright © 2025 LqN5M2Xy™
    Developed by Lokman Kerey.
    All rights reserved.
*/