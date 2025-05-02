package com.lokrey.lqN5M2Xy.mydailyluck

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Calendar
import java.util.Locale


class DailyNotificationWorker(appContext: Context, workerParams: WorkerParameters)
    : Worker(appContext, workerParams) {

    override fun doWork(): Result {
        Log.d("DailyNotificationWorker", "📅 Worker gestartet: ${System.currentTimeMillis()}")

        val prefs = applicationContext.getSharedPreferences("MyDailyLuckPrefs", Context.MODE_PRIVATE)
        val nextAttemptTime = prefs.getLong("next_attempt_time", 0L)
        val now = System.currentTimeMillis()

        // 🕰️ Erste Initialisierung, wenn noch nie gesetzt
        if (nextAttemptTime == 0L) {
            val calendar = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 8)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 5)
                set(Calendar.MILLISECOND, 0)
            }
            // Wenn die Uhrzeit schon vorbei ist, auf den nächsten Tag setzen
            if (calendar.timeInMillis <= now) {
                calendar.add(Calendar.DAY_OF_YEAR, 1)
            }
        }

        // 🔍 Überprüfung, ob die Benachrichtigung heute schon gesendet wurde
        val lastNotificationDate = prefs.getString("last_notification_date", "")
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(now))

        if (lastNotificationDate == today) {
            Log.d("Worker", "🔕 Benachrichtigung wurde heute bereits gesendet.")
            return Result.success()
        }

        if (now >= nextAttemptTime) {
            // NEUE ABFRAGE:
            if (isBetween8and10()) {
                showNotification()
                prefs.edit().putString("last_notification_date", today).apply()
                Log.d("Worker", "🔔 Benachrichtigung gesendet.")
            } else {
                Log.d("Worker", "🔕 Aktuelle Zeit ist NICHT zwischen 8:00 und 10:00 Uhr – keine Benachrichtigung.")
            }

        } else {
            Log.d("Worker", "🔕 Kein neuer Versuch – nächster erst nach ${Date(nextAttemptTime)}")
        }
        MainActivity.scheduleDailyCheck(applicationContext)
        return Result.success()
    }


    private fun showNotification() {
        val context = applicationContext
        val channelId = "my_daily_luck_channel"
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Daily Luck",
                NotificationManager.IMPORTANCE_DEFAULT // Kein Ton
            )
            manager.createNotificationChannel(channel)
        }

        val intent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("🎲 Today's Luck Awaits!")
            .setContentText("Your daily luck check is ready. Tap to see your luck!")
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        manager.notify(1002, notification)
    }

    private fun isBetween8and10(): Boolean {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        // Optional: Falls du auch die Minuten prüfen willst, könntest du hier noch "minute" abfragen.

        return hour in 8 until 10

    }

}
