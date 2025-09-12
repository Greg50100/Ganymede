package com.joviansapps.ganymede

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalView
import androidx.lifecycle.viewmodel.compose.viewModel
import com.joviansapps.ganymede.navigation.AppRoot
import com.joviansapps.ganymede.navigation.LanguageProvider
import com.joviansapps.ganymede.ui.theme.AppTheme
import com.joviansapps.ganymede.viewmodel.SettingsViewModel
import com.joviansapps.ganymede.viewmodel.ThemeMode
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Harmonisation status/navigation bar avec le thème (edge-to-edge, contraste icônes)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            val settingsVm: SettingsViewModel = viewModel()
            val state by settingsVm.uiState.collectAsState()

            LanguageProvider(code = state.language) {
                AppTheme(themeMode = state.themeMode) {
                    val view = LocalView.current
                    val isDark = state.themeMode == ThemeMode.DARK
                    val insetsController = WindowInsetsControllerCompat(window, view)
                    SideEffect {
                        // Contrôle moderne de l'apparence des icônes de la barre système
                        insetsController.isAppearanceLightStatusBars = !isDark
                        insetsController.isAppearanceLightNavigationBars = !isDark
                        // Ne pas définir directement window.statusBarColor / navigationBarColor (dépréciés)
                        // Les Composables utilisent systemBarsPadding pour gérer le safe-area et la couleur
                    }
                    AppRoot(settingsVm)
                }
            }
        }
    }
}