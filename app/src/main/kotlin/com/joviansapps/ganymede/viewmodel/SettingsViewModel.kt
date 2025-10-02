package com.joviansapps.ganymede.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.joviansapps.ganymede.core.common.Result
import com.joviansapps.ganymede.core.domain.repository.PreferencesRepository
import com.joviansapps.ganymede.crash.CrashReporter
import com.joviansapps.ganymede.utils.ErrorManager
import com.joviansapps.ganymede.utils.ErrorContext
import com.joviansapps.ganymede.utils.ErrorSeverity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

// --- Enums et Classes de Données ---
enum class ThemeMode(val displayName: String) {
    LIGHT("Clair"),
    DARK("Sombre"),
    AUTO("Automatique")
}

enum class NumberFormatMode(val displayName: String) {
    PLAIN("Simple"),
    THOUSANDS("Milliers"),
    SCIENTIFIC("Scientifique")
}

data class SettingsUiState(
    val themeMode: ThemeMode = ThemeMode.AUTO,
    val language: String = defaultLanguage(),
    val numberFormatMode: NumberFormatMode = NumberFormatMode.PLAIN,
    val crashReportsEnabled: Boolean = true,
    val keepScreenOnEnabled: Boolean = false,
    val hapticFeedbackEnabled: Boolean = true,
    val isLoading: Boolean = false,
    val error: String? = null,
    val lastSyncTimestamp: Long = 0L
) {
    val isValid: Boolean get() = error == null && language.isNotBlank()
    val hasUnsavedChanges: Boolean get() = lastSyncTimestamp > 0L
}

// --- Constantes et Valeurs par Défaut ---
object SettingsDefaults {
    val SupportedLanguages = mapOf(
        "fr" to "Français",
        "en" to "English",
        "es" to "Español",
        "de" to "Deutsch"
    )

    const val AUTO_SYNC_INTERVAL_MS = 30000L // 30 secondes
    const val SETTINGS_CACHE_TTL_MS = 300000L // 5 minutes
}

private fun defaultLanguage(): String {
    val systemLanguage = Locale.getDefault().language
    return if (systemLanguage in SettingsDefaults.SupportedLanguages.keys) systemLanguage else "en"
}

/**
 * ViewModel refactorisé pour les paramètres avec architecture moderne
 *
 * Fonctionnalités :
 * - Gestion d'état réactive avec Flow
 * - Gestion d'erreurs centralisée
 * - Validation des données
 * - Cache intelligent
 * - Synchronisation automatique
 */
@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val preferencesRepository: PreferencesRepository,
    private val crashReporter: CrashReporter,
    private val errorManager: ErrorManager
) : ViewModel() {

    // État interne pour le chargement et les erreurs
    private val _isLoading = MutableStateFlow(false)
    private val _error = MutableStateFlow<String?>(null)
    private val _lastSyncTimestamp = MutableStateFlow(0L)

    // Snapshot des préférences pour faciliter le combine (évite l'overload vararg)
    private data class PrefsSnapshot(
        val themeMode: String,
        val language: String,
        val hapticFeedback: Boolean,
        val keepScreenOn: Boolean,
        val numberFormat: String
    )

    private val prefsSnapshotFlow: Flow<PrefsSnapshot> = combine(
        preferencesRepository.getThemeMode(),
        preferencesRepository.getLanguage(),
        preferencesRepository.getHapticFeedbackEnabled(),
        preferencesRepository.getKeepScreenOnEnabled(),
        preferencesRepository.getNumberFormatMode()
    ) { themeMode, language, hapticFeedback, keepScreenOn, numberFormat ->
        PrefsSnapshot(
            themeMode = themeMode,
            language = language,
            hapticFeedback = hapticFeedback,
            keepScreenOn = keepScreenOn,
            numberFormat = numberFormat
        )
    }

    // État réactif combiné depuis le repository et états internes
    val uiState: StateFlow<SettingsUiState> = combine(
        prefsSnapshotFlow,
        _isLoading,
        _error,
        _lastSyncTimestamp
    ) { prefs, isLoading, error, lastSync ->
        SettingsUiState(
            themeMode = safeValueOf<ThemeMode>(prefs.themeMode) ?: ThemeMode.AUTO,
            language = prefs.language.takeIf { it.isNotBlank() } ?: defaultLanguage(),
            numberFormatMode = safeValueOf<NumberFormatMode>(prefs.numberFormat) ?: NumberFormatMode.PLAIN,
            hapticFeedbackEnabled = prefs.hapticFeedback,
            keepScreenOnEnabled = prefs.keepScreenOn,
            isLoading = isLoading,
            error = error,
            lastSyncTimestamp = lastSync
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = SettingsUiState(isLoading = true)
    )

    // Expose le thème mode séparément pour la compatibilité
    val themeMode: StateFlow<ThemeMode> = uiState
        .map { it.themeMode }
        .stateIn(viewModelScope, SharingStarted.Lazily, ThemeMode.AUTO)

    init {
        // Initialiser les paramètres par défaut si nécessaire
        initializeDefaultSettings()
    }

    // === Actions pour modifier les préférences ===

    /**
     * Change le mode de thème avec validation
     */
    fun setThemeMode(themeMode: ThemeMode) {
        executeSettingChange("setThemeMode") {
            preferencesRepository.setThemeMode(themeMode.name)
            crashReporter.logUserAction("Theme changed to ${themeMode.name}", "Settings")
        }
    }

    /**
     * Alias pour setThemeMode pour la compatibilité
     */
    fun setTheme(themeMode: ThemeMode) = setThemeMode(themeMode)

    /**
     * Change la langue avec validation
     */
    fun setLanguage(language: String) {
        if (!SettingsDefaults.SupportedLanguages.containsKey(language)) {
            _error.value = "Langue non supportée: $language"
            return
        }

        executeSettingChange("setLanguage") {
            preferencesRepository.setLanguage(language)
            crashReporter.logUserAction("Language changed to $language", "Settings")
        }
    }

    /**
     * Active/désactive les vibrations tactiles
     */
    fun setHapticFeedbackEnabled(enabled: Boolean) {
        executeSettingChange("setHapticFeedback") {
            preferencesRepository.setHapticFeedbackEnabled(enabled)
            crashReporter.logUserAction("Haptic feedback ${if(enabled) "enabled" else "disabled"}", "Settings")
        }
    }

    /**
     * Active/désactive le maintien de l'écran allumé
     */
    fun setKeepScreenOnEnabled(enabled: Boolean) {
        executeSettingChange("setKeepScreenOn") {
            preferencesRepository.setKeepScreenOnEnabled(enabled)
            crashReporter.logUserAction("Keep screen on ${if(enabled) "enabled" else "disabled"}", "Settings")
        }
    }

    /**
     * Change le mode de formatage des nombres
     */
    fun setNumberFormatMode(mode: NumberFormatMode) {
        executeSettingChange("setNumberFormatMode") {
            preferencesRepository.setNumberFormatMode(mode.name)
            crashReporter.logUserAction("Number format changed to ${mode.name}", "Settings")
        }
    }

    /**
     * Force un test de crash (pour les tests uniquement)
     */
    fun forceCrash() {
        crashReporter.logUserAction("Force crash requested", "Settings")
        crashReporter.testCrash()
    }

    /**
     * Réinitialise tous les paramètres aux valeurs par défaut
     */
    fun resetToDefaults() {
        executeSettingChange("resetToDefaults") {
            setThemeMode(ThemeMode.AUTO)
            setLanguage(defaultLanguage())
            setNumberFormatMode(NumberFormatMode.PLAIN)
            setHapticFeedbackEnabled(true)
            setKeepScreenOnEnabled(false)

            crashReporter.logUserAction("Settings reset to defaults", "Settings")
        }
    }

    /**
     * Efface les erreurs
     */
    fun clearError() {
        _error.value = null
    }

    /**
     * Synchronise manuellement les paramètres
     */
    fun syncSettings() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Simuler une synchronisation
                kotlinx.coroutines.delay(1000)
                _lastSyncTimestamp.value = System.currentTimeMillis()
                crashReporter.logUserAction("Manual settings sync", "Settings")
            } catch (e: Exception) {
                handleError(e, "syncSettings")
            } finally {
                _isLoading.value = false
            }
        }
    }

    // === Méthodes privées ===

    /**
     * Exécute un changement de paramètre avec gestion d'erreur
     */
    private fun executeSettingChange(operation: String, block: suspend () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                block()
                _lastSyncTimestamp.value = System.currentTimeMillis()
            } catch (e: Exception) {
                handleError(e, operation)
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Initialise les paramètres par défaut si nécessaire
     */
    private fun initializeDefaultSettings() {
        viewModelScope.launch {
            try {
                // Vérifier si c'est la première exécution
                val currentLanguage = preferencesRepository.getLanguage().first()
                if (currentLanguage.isBlank()) {
                    preferencesRepository.setLanguage(defaultLanguage())
                    preferencesRepository.setThemeMode(ThemeMode.AUTO.name)
                    preferencesRepository.setNumberFormatMode(NumberFormatMode.PLAIN.name)
                    preferencesRepository.setHapticFeedbackEnabled(true)
                    preferencesRepository.setKeepScreenOnEnabled(false)
                }
            } catch (e: Exception) {
                handleError(e, "initializeDefaultSettings")
            }
        }
    }

    /**
     * Gère les erreurs de manière centralisée
     */
    private suspend fun handleError(error: Throwable, context: String) {
        val errorContext = ErrorContext(
            screenName = "Settings",
            userAction = context
        )

        errorManager.handleError(error, errorContext, ErrorSeverity.MEDIUM)
        _error.value = "Erreur lors de $context: ${error.message}"
    }

    /**
     * Obtient une valeur enum de manière sécurisée
     */
    private inline fun <reified T : Enum<T>> safeValueOf(value: String): T? {
        return try {
            enumValueOf<T>(value)
        } catch (e: IllegalArgumentException) {
            null
        }
    }

    // === Méthodes utilitaires publiques ===

    /**
     * Obtient la liste des langues supportées
     */
    fun getSupportedLanguages(): Map<String, String> = SettingsDefaults.SupportedLanguages

    /**
     * Vérifie si une langue est supportée
     */
    fun isLanguageSupported(language: String): Boolean =
        SettingsDefaults.SupportedLanguages.containsKey(language)

    /**
     * Obtient le nom d'affichage d'une langue
     */
    fun getLanguageDisplayName(language: String): String =
        SettingsDefaults.SupportedLanguages[language] ?: language

    /**
     * Export des paramètres (pour sauvegarde/restauration)
     */
    fun exportSettings(): Map<String, Any> {
        val currentState = uiState.value
        return mapOf(
            "themeMode" to currentState.themeMode.name,
            "language" to currentState.language,
            "numberFormatMode" to currentState.numberFormatMode.name,
            "hapticFeedbackEnabled" to currentState.hapticFeedbackEnabled,
            "keepScreenOnEnabled" to currentState.keepScreenOnEnabled,
            "exportTimestamp" to System.currentTimeMillis()
        )
    }
}
