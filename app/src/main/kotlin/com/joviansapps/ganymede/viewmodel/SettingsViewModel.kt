// app/src/main/kotlin/com/joviansapps/ganymede/viewmodel/SettingsViewModel.kt
package com.joviansapps.ganymede.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.util.Locale
import androidx.datastore.preferences.core.booleanPreferencesKey
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
    val primaryColor: Long = 0xFFFB8C00,
    val language: String = defaultLanguage(),
    val crashReportsEnabled: Boolean = true
)

class SettingsViewModel(application: Application) : AndroidViewModel(application) {
    private val dataStore = application.dataStore

    private val KEY_THEME = stringPreferencesKey("theme_mode")
    private val KEY_PRIMARY = longPreferencesKey("primary_color")
    private val KEY_LANG = stringPreferencesKey("language")
    private val KEY_CRASH = booleanPreferencesKey("crash_reports_enabled")

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState

    init {
        // Hydrate state from DataStore
        viewModelScope.launch {
            dataStore.data
                .catch { emit(emptyPreferences()) }
                .map { prefs ->
                    val theme = prefs[KEY_THEME]?.let {
                        try { ThemeMode.valueOf(it) } catch (_: Exception) { ThemeMode.AUTO }
                    } ?: ThemeMode.AUTO
                    val color = prefs[KEY_PRIMARY] ?: 0xFFFB8C00
                    val lang = prefs[KEY_LANG] ?: defaultLanguage()
                    val crash = prefs[KEY_CRASH] ?: true
                    SettingsUiState(
                        themeMode = theme,
                        primaryColor = color,
                        language = lang,
                        crashReportsEnabled = crash
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
        viewModelScope.launch {
            dataStore.edit { prefs -> prefs[KEY_THEME] = mode.name }
        }
    }

    fun setPrimaryColor(colorLong: Long) {
        _uiState.value = _uiState.value.copy(primaryColor = colorLong)
        viewModelScope.launch {
            dataStore.edit { prefs -> prefs[KEY_PRIMARY] = colorLong }
        }
    }

    fun setLanguage(lang: String) {
        val l = if (lang in SupportedLanguages) lang else "en"
        _uiState.value = _uiState.value.copy(language = l)
        viewModelScope.launch {
            dataStore.edit { prefs -> prefs[KEY_LANG] = l }
        }
    }


    fun setCrashReportsEnabled(enabled: Boolean) {
        _uiState.value = _uiState.value.copy(crashReportsEnabled = enabled)
        CrashReporter.updateEnabled(enabled)
        viewModelScope.launch { dataStore.edit { it[KEY_CRASH] = enabled } }
    }

    fun testCrashReport() {
        // Simule un envoi sans crasher l'appli.
        val ex = IllegalStateException("Test crash (simulation)")
        CrashReporter.log(ex, message = "Test manuel déclenché")
        CrashReporter.logMessage("Test message additionnel", mapOf("userToggle" to _uiState.value.crashReportsEnabled))
    }

    // Force un crash réel (utilisé uniquement pour tests). L'appel doit être intentionnel.
    fun forceCrash(): Nothing {
        throw RuntimeException("Crash forcé pour test Crashlytics")
    }
}