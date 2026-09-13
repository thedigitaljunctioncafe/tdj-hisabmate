package com.thedigitaljunction.tdjhisabmate.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.thedigitaljunction.tdjhisabmate.data.preferences.UserPreferencesRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class HisabReminderReceiver : BroadcastReceiver() {

    companion object {
        const val ACTION_DAILY_REMINDER = "com.thedigitaljunction.tdjhisabmate.ACTION_DAILY_REMINDER"
    }

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == ACTION_DAILY_REMINDER || intent.action.isNullOrEmpty()) {
            val pendingResult = goAsync()
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val prefsRepo = UserPreferencesRepository(context.applicationContext)
                    val prefs = prefsRepo.userPreferencesFlow.first()

                    if (prefs.isHisabGuardEnabled) {
                        HisabNotificationHelper.showDailyReminder(context.applicationContext)
                        // Reschedule for next day at the user's preferred time
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
