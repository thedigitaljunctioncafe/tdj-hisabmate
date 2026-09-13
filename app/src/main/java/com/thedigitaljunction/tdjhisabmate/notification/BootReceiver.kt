package com.thedigitaljunction.tdjhisabmate.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.thedigitaljunction.tdjhisabmate.data.preferences.UserPreferencesRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            val pendingResult = goAsync()
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val prefsRepo = UserPreferencesRepository(context.applicationContext)
                    val prefs = prefsRepo.userPreferencesFlow.first()

                    if (prefs.isHisabGuardEnabled) {
                        HisabNotificationHelper.createNotificationChannel(context.applicationContext)
                        HisabReminderScheduler.scheduleDailyReminder(
                            context.applicationContext,
                            prefs.dailyReviewHour,
                            prefs.dailyReviewMinute
                        )
                    }
                } finally {
                    pendingResult.finish()
                }
            }
        }
    }
}
