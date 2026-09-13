package com.thedigitaljunction.tdjhisabmate.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.thedigitaljunction.tdjhisabmate.MainActivity
import com.thedigitaljunction.tdjhisabmate.R

object HisabNotificationHelper {

    const val CHANNEL_ID = "tdj_hisabmate_reminders"
    const val CHANNEL_NAME = "HisabMate Reminders"
    const val NOTIFICATION_ID = 1001

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Daily reminders to review and record your hisab"
                enableVibration(true)
            }
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun showDailyReminder(context: Context) {
        createNotificationChannel(context)

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("TDJ HisabMate Reminder")
            .setContentText("You have a scheduled reminder to review your hisab.")
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("Take a moment to review today's expenses, verify cash payments, and close today's hisab.")
            )
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        try {
            val manager = NotificationManagerCompat.from(context)
            if (manager.areNotificationsEnabled()) {
                manager.notify(NOTIFICATION_ID, notification)
            }
        } catch (_: SecurityException) {
            // Permission not granted or revoked
        }
    }
}
