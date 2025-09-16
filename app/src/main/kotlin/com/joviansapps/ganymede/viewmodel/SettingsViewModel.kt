// app/src/main/kotlin/com/joviansapps/ganymede/viewmodel/SettingsViewModel.kt
package com.joviansapps.ganymede.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.util.Locale
import com.joviansapps.ganymede.crash.CrashReporter

// Delegate DataStore (extension sur Context)
private val Context.dataStore by preferencesDataStore(name = "settings")

// Liste des langues supportées par l'application
val SupportedLanguages = listOf("fr", "en", "es", "de")

private fun defaultLanguage(): String {
    val sys = Locale.getDefault().language
    return if (sys in SupportedLanguages) sys else "en"
}

enum class ThemeMode { LIGHT, DARK, AUTO }

data class SettingsUiState(
    val themeMode: ThemeMode = ThemeMode.AUTO,
    val language: String = defaultLanguage(),
    val crashReportsEnabled: Boolean = true,
    val keepScreenOnEnabled: Boolean = false
)

class SettingsViewModel(application: Application) : AndroidViewModel(application) {
    private val dataStore = application.dataStore

    private val KEY_THEME = stringPreferencesKey("theme_mode")
    private val KEY_LANG = stringPreferencesKey("language")
    private val KEY_CRASH = booleanPreferencesKey("crash_reports_enabled")
    private val KEY_SCREEN_ON = booleanPreferencesKey("keep_screen_on_enabled")

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState

    init {
        // Hydrate state from DataStore
        viewModelScope.launch {
            dataStore.data
                .catch { emit(emptyPreferences()) }
                .map { prefs ->
                    val theme = prefs[KEY_THEME]?.let { runCatching { ThemeMode.valueOf(it) }.getOrNull() } ?: ThemeMode.AUTO
                    val lang = prefs[KEY_LANG] ?: defaultLanguage()
                    val crash = prefs[KEY_CRASH] ?: true
                    val screenOn = prefs[KEY_SCREEN_ON] ?: false
                    SettingsUiState(
                        themeMode = theme,
                        language = lang,
                        crashReportsEnabled = crash,
                        keepScreenOnEnabled = screenOn
                    )
                }
                .collect { ui ->
                    _uiState.value = ui
                    CrashReporter.updateEnabled(ui.crashReportsEnabled)
                }
        }
    }

    fun setTheme(mode: ThemeMode) {
        _uiState.value = _uiState.value.copy(themeMode = mode)
        viewModelScope.launch { dataStore.edit { it[KEY_THEME] = mode.name } }
    }

    fun setLanguage(lang: String) {
        val l = if (lang in SupportedLanguages) lang else "en"
        _uiState.value = _uiState.value.copy(language = l)
        viewModelScope.launch { dataStore.edit { it[KEY_LANG] = l } }
    }

    fun setCrashReportsEnabled(enabled: Boolean) {
        _uiState.value = _uiState.value.copy(crashReportsEnabled = enabled)
        CrashReporter.updateEnabled(enabled)
        viewModelScope.launch { dataStore.edit { it[KEY_CRASH] = enabled } }
    }

    fun setKeepScreenOnEnabled(enabled: Boolean) {
        _uiState.value = _uiState.value.copy(keepScreenOnEnabled = enabled)
        viewModelScope.launch { dataStore.edit { it[KEY_SCREEN_ON] = enabled } }
    }

    fun testCrashReport() {
        // Simule un envoi sans crasher l'appli.
        val ex = IllegalStateException("Test crash (simulation)")
        CrashReporter.log(ex, message = "Test manuel déclenché")
        CrashReporter.logMessage("Test message additionnel", mapOf("userToggle" to _uiState.value.crashReportsEnabled))
    }

    // Force un crash réel (utilisé uniquement pour tests). L'appel doit être intentionnel.
    fun forceCrash(): Nothing = throw RuntimeException("Crash forcé pour test Crashlytics")
}