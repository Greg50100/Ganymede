package com.joviansapps.ganymede

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.joviansapps.ganymede.navigation.AppRoot
import com.joviansapps.ganymede.navigation.LanguageProvider
import com.joviansapps.ganymede.ui.theme.AppTheme
import com.joviansapps.ganymede.viewmodel.SettingsViewModel
import com.joviansapps.ganymede.viewmodel.ThemeMode
import dagger.hilt.android.AndroidEntryPoint

/**
 * Activité principale de l'application Ganymede
 *
 * Cette activité est responsable de :
 * - L'initialisation de l'interface utilisateur Compose
 * - La gestion des thèmes (clair/sombre/auto)
 * - La configuration des barres système
 * - La navigation globale de l'application
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Activation du edge-to-edge pour une expérience immersive
        enableEdgeToEdge()

        // Configuration de l'interface utilisateur
        setupUserInterface()

        // Optimisations de performance
        configurePerformanceOptimizations()
    }

    /**
     * Configure l'interface utilisateur principale avec Compose
     */
    private fun setupUserInterface() {
        setContent {
            val settingsViewModel: SettingsViewModel = hiltViewModel()
            val uiState by settingsViewModel.uiState.collectAsState()

            val themeMode = when (uiState.themeMode) {
                ThemeMode.LIGHT -> ThemeMode.LIGHT
                ThemeMode.DARK -> ThemeMode.DARK
                ThemeMode.AUTO -> if (isSystemInDarkTheme()) ThemeMode.DARK else ThemeMode.LIGHT
            }

            AppTheme(themeMode = themeMode) {
                ConfigureSystemBars(isDarkTheme = themeMode == ThemeMode.DARK)

                LanguageProvider(code = uiState.language) {
                    AppRoot(settingsVm = settingsViewModel)
                }
            }
        }
    }

    /**
     * Configure les optimisations de performance
     */
    private fun configurePerformanceOptimizations() {
        // Configuration des barres système pour une meilleure expérience
        WindowCompat.setDecorFitsSystemWindows(window, false)

        // Optionnel : garde l'écran allumé pendant les calculs intensifs
        // window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }
}

/**
 * Composable pour configurer les barres système selon le thème
 */
@Composable
private fun ConfigureSystemBars(isDarkTheme: Boolean) {
    val view = LocalView.current

    SideEffect {
        val window = (view.context as ComponentActivity).window
        val insetsController = WindowCompat.getInsetsController(window, view)

        // Configuration de la couleur des icônes de la barre de statut
        insetsController.isAppearanceLightStatusBars = !isDarkTheme
        insetsController.isAppearanceLightNavigationBars = !isDarkTheme

        // Configuration du comportement des barres système
        insetsController.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
    }
}
