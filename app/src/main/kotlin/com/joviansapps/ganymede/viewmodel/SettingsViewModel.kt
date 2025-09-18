package com.joviansapps.ganymede.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.joviansapps.ganymede.crash.CrashReporter
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.IOException
import java.util.Locale

// --- DataStore Delegate ---
private val Context.dataStore by preferencesDataStore(name = "settings")

// --- Enums and Data Classes ---
enum class ThemeMode { LIGHT, DARK, AUTO }
enum class NumberFormatMode { PLAIN, THOUSANDS, SCIENTIFIC }

data class SettingsUiState(
    val themeMode: ThemeMode = ThemeMode.AUTO,
    val language: String = defaultLanguage(),
    val numberFormatMode: NumberFormatMode = NumberFormatMode.PLAIN,
    val crashReportsEnabled: Boolean = true,
    val keepScreenOnEnabled: Boolean = false,
    val hapticFeedbackEnabled: Boolean = true
)

// --- Constants and Defaults ---
object SettingsDefaults {
    val SupportedLanguages = listOf("fr", "en", "es", "de")
}

private fun defaultLanguage(): String {
    val systemLanguage = Locale.getDefault().language
    return if (systemLanguage in SettingsDefaults.SupportedLanguages) systemLanguage else "en"
}


class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val dataStore = application.dataStore

    // --- Centralized Preference Keys ---
    private object PreferencesKeys {
        val THEME = stringPreferencesKey("theme_mode")
        val LANGUAGE = stringPreferencesKey("language")
        val NUMBER_FORMAT = stringPreferencesKey("number_format_mode")
        val CRASH_REPORTS = booleanPreferencesKey("crash_reports_enabled")
        val KEEP_SCREEN_ON = booleanPreferencesKey("keep_screen_on_enabled")
        val HAPTIC_FEEDBACK = booleanPreferencesKey("haptic_feedback_enabled")
    }

    // --- StateFlow Initialization ---
    private val settingsFlow = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            val theme = preferences[PreferencesKeys.THEME]
                ?.let { runCatching { ThemeMode.valueOf(it) }.getOrNull() } ?: ThemeMode.AUTO

            val language = preferences[PreferencesKeys.LANGUAGE] ?: defaultLanguage()

            val numberFormat = preferences[PreferencesKeys.NUMBER_FORMAT]
                ?.let { runCatching { NumberFormatMode.valueOf(it) }.getOrNull() } ?: NumberFormatMode.PLAIN

            val crashReports = preferences[PreferencesKeys.CRASH_REPORTS] ?: true
            val keepScreenOn = preferences[PreferencesKeys.KEEP_SCREEN_ON] ?: false
            val hapticFeedback = preferences[PreferencesKeys.HAPTIC_FEEDBACK] ?: true

            SettingsUiState(theme, language, numberFormat, crashReports, keepScreenOn, hapticFeedback)
        }

    val uiState: StateFlow<SettingsUiState> = settingsFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = SettingsUiState()
        )

    init {
        viewModelScope.launch {
            settingsFlow.collect { settings ->
                CrashReporter.updateEnabled(settings.crashReportsEnabled)
            }
        }
    }


    // --- Public Functions to Modify Settings ---
    fun setTheme(mode: ThemeMode) {
        viewModelScope.launch {
            dataStore.edit { it[PreferencesKeys.THEME] = mode.name }
        }
    }

    fun setLanguage(lang: String) {
        viewModelScope.launch {
            val validLang = if (lang in SettingsDefaults.SupportedLanguages) lang else "en"
            dataStore.edit { it[PreferencesKeys.LANGUAGE] = validLang }
        }
    }

    fun setNumberFormatMode(mode: NumberFormatMode) {
        viewModelScope.launch {
            dataStore.edit { it[PreferencesKeys.NUMBER_FORMAT] = mode.name }
        }
    }

    fun setCrashReportsEnabled(enabled: Boolean) {
        CrashReporter.updateEnabled(enabled)
        viewModelScope.launch {
            dataStore.edit { it[PreferencesKeys.CRASH_REPORTS] = enabled }
        }
    }

    fun setKeepScreenOnEnabled(enabled: Boolean) {
        viewModelScope.launch {
            dataStore.edit { it[PreferencesKeys.KEEP_SCREEN_ON] = enabled }
        }
    }

    fun setHapticFeedbackEnabled(enabled: Boolean) {
        viewModelScope.launch {
            dataStore.edit { it[PreferencesKeys.HAPTIC_FEEDBACK] = enabled }
        }
    }

    // --- Debug Functions ---
    fun logTestException() {
        val ex = IllegalStateException("Test crash (simulation)")
        CrashReporter.log(ex, message = "Manual test triggered from settings")
        CrashReporter.logMessage(
            "Additional test message",
            mapOf("userToggle" to (uiState.value.crashReportsEnabled))
        )
    }

    fun forceCrash(): Nothing = throw RuntimeException("Forced crash to test Crashlytics")
}

