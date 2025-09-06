package com.joviansapps.ganymede

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.joviansapps.ganymede.navigation.AppRoot
import com.joviansapps.ganymede.navigation.LanguageProvider
import com.joviansapps.ganymede.ui.theme.AppTheme
import com.joviansapps.ganymede.viewmodel.SettingsViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val settingsVm: SettingsViewModel = viewModel()
            val state by settingsVm.uiState.collectAsState()

            LanguageProvider(code = state.language) {
                AppTheme(themeMode = state.themeMode) {
                    AppRoot(settingsVm)
                }
            }
        }
    }
}