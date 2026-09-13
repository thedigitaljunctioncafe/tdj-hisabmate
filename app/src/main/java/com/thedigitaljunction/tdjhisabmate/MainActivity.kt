package com.thedigitaljunction.tdjhisabmate

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.thedigitaljunction.tdjhisabmate.notification.HisabNotificationHelper
import com.thedigitaljunction.tdjhisabmate.ui.navigation.MainApp
import com.thedigitaljunction.tdjhisabmate.ui.theme.TDJHisabMateTheme
import com.thedigitaljunction.tdjhisabmate.ui.viewmodel.HisabViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: HisabViewModel by viewModels { HisabViewModel.Factory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        HisabNotificationHelper.createNotificationChannel(this)
        enableEdgeToEdge()
        setContent {
            val prefs by viewModel.preferences.collectAsState()
            val isDark = when (prefs.themeMode) {
                "DARK" -> true
                "LIGHT" -> false
                else -> isSystemInDarkTheme()
            }

            TDJHisabMateTheme(darkTheme = isDark) {
                MainApp(viewModel = viewModel)
            }
        }
    }

    override fun onStop() {
        super.onStop()
        if (!viewModel.isExternalPickerActive) {
            viewModel.lockSession()
        }
    }
}
