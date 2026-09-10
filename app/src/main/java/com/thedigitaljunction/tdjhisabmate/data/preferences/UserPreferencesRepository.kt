package com.thedigitaljunction.tdjhisabmate.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

data class UserPreferences(
    val currency: String = "₹",
    val isOnboarded: Boolean = false,
    val hasCompletedSetup: Boolean = false,
    val dailyReviewHour: Int = 21,
    val dailyReviewMinute: Int = 0,
    val isHisabGuardEnabled: Boolean = true,
    val isPatternAwarenessEnabled: Boolean = true,
    val isMissedExpensePromptEnabled: Boolean = true,
    val pinCode: String = "",
    val isPinEnabled: Boolean = false,
    val themeMode: String = "SYSTEM" // SYSTEM, LIGHT, DARK
)

class UserPreferencesRepository(private val context: Context) {

    private object Keys {
        val CURRENCY = stringPreferencesKey("currency")
        val IS_ONBOARDED = booleanPreferencesKey("is_onboarded")
        val HAS_COMPLETED_SETUP = booleanPreferencesKey("has_completed_setup")
        val DAILY_REVIEW_HOUR = intPreferencesKey("daily_review_hour")
        val DAILY_REVIEW_MINUTE = intPreferencesKey("daily_review_minute")
        val IS_HISAB_GUARD_ENABLED = booleanPreferencesKey("is_hisab_guard_enabled")
        val IS_PATTERN_AWARENESS_ENABLED = booleanPreferencesKey("is_pattern_awareness_enabled")
        val IS_MISSED_EXPENSE_PROMPT_ENABLED = booleanPreferencesKey("is_missed_expense_prompt_enabled")
        val PIN_CODE = stringPreferencesKey("pin_code")
        val IS_PIN_ENABLED = booleanPreferencesKey("is_pin_enabled")
        val THEME_MODE = stringPreferencesKey("theme_mode")
    }

    val userPreferencesFlow: Flow<UserPreferences> = context.dataStore.data.map { preferences ->
        UserPreferences(
            currency = preferences[Keys.CURRENCY] ?: "₹",
            isOnboarded = preferences[Keys.IS_ONBOARDED] ?: false,
            hasCompletedSetup = preferences[Keys.HAS_COMPLETED_SETUP] ?: false,
            dailyReviewHour = preferences[Keys.DAILY_REVIEW_HOUR] ?: 21,
            dailyReviewMinute = preferences[Keys.DAILY_REVIEW_MINUTE] ?: 0,
            isHisabGuardEnabled = preferences[Keys.IS_HISAB_GUARD_ENABLED] ?: true,
            isPatternAwarenessEnabled = preferences[Keys.IS_PATTERN_AWARENESS_ENABLED] ?: true,
            isMissedExpensePromptEnabled = preferences[Keys.IS_MISSED_EXPENSE_PROMPT_ENABLED] ?: true,
            pinCode = preferences[Keys.PIN_CODE] ?: "",
            isPinEnabled = preferences[Keys.IS_PIN_ENABLED] ?: false,
            themeMode = preferences[Keys.THEME_MODE] ?: "SYSTEM"
        )
    }

    suspend fun setCurrency(currency: String) {
        context.dataStore.edit { it[Keys.CURRENCY] = currency }
    }

    suspend fun setOnboarded(isOnboarded: Boolean) {
        context.dataStore.edit { it[Keys.IS_ONBOARDED] = isOnboarded }
    }

    suspend fun setCompletedSetup(completed: Boolean) {
        context.dataStore.edit { it[Keys.HAS_COMPLETED_SETUP] = completed }
    }

    suspend fun setDailyReviewTime(hour: Int, minute: Int) {
        context.dataStore.edit {
            it[Keys.DAILY_REVIEW_HOUR] = hour
            it[Keys.DAILY_REVIEW_MINUTE] = minute
        }
    }

    suspend fun setHisabGuardEnabled(enabled: Boolean) {
        context.dataStore.edit { it[Keys.IS_HISAB_GUARD_ENABLED] = enabled }
    }

    suspend fun setPatternAwarenessEnabled(enabled: Boolean) {
        context.dataStore.edit { it[Keys.IS_PATTERN_AWARENESS_ENABLED] = enabled }
    }

    suspend fun setMissedExpensePromptEnabled(enabled: Boolean) {
        context.dataStore.edit { it[Keys.IS_MISSED_EXPENSE_PROMPT_ENABLED] = enabled }
    }

    suspend fun setPin(pin: String, enabled: Boolean) {
        context.dataStore.edit {
            it[Keys.PIN_CODE] = pin
            it[Keys.IS_PIN_ENABLED] = enabled
        }
    }

    suspend fun setThemeMode(themeMode: String) {
        context.dataStore.edit { it[Keys.THEME_MODE] = themeMode }
    }
}
