package com.joviansapps.ganymede

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.joviansapps.ganymede.navigation.AppRoot
import com.joviansapps.ganymede.navigation.LanguageProvider
import com.joviansapps.ganymede.ui.theme.AppTheme
import com.joviansapps.ganymede.viewmodel.SettingsViewModel
import com.joviansapps.ganymede.viewmodel.ThemeMode

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            val settingsVm: SettingsViewModel = viewModel()
            val settingsState by settingsVm.uiState.collectAsState()
            val view = LocalView.current
            // Récupère la window de l'activité une seule fois et la rend disponible dans le scope
            val activityWindow = (view.context as android.app.Activity).window

            if (!view.isInEditMode) {
                SideEffect {
                    if (settingsState.keepScreenOnEnabled) {
                        activityWindow.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                    } else {
                        activityWindow.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                    }
                }
            }

            LanguageProvider(code = settingsState.language) {
                AppTheme(themeMode = settingsState.themeMode) {
                    val isDark = when (settingsState.themeMode) {
                        ThemeMode.DARK -> true
                        ThemeMode.LIGHT -> false
                        ThemeMode.AUTO -> isSystemInDarkTheme()
                    }
                    val insetsController = WindowInsetsControllerCompat(activityWindow, view)
                    SideEffect {
                        insetsController.isAppearanceLightStatusBars = !isDark
                        insetsController.isAppearanceLightNavigationBars = !isDark
                    }
                    AppRoot(settingsVm)
                }
            }
        }
    }
}
