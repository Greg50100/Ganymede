// app/src/main/kotlin/com/joviansapps/ganymede/viewmodel/SettingsViewModel.kt
package com.joviansapps.ganymede.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

enum class ThemeMode { LIGHT, DARK, AUTO }

data class SettingsUiState(
    val themeMode: ThemeMode = ThemeMode.AUTO,
    val primaryColor: Long = 0xFF6750A4,
    val language: String = "en"           // ← default language set to English
)

class SettingsViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState

    fun setTheme(mode: ThemeMode) {
        _uiState.value = _uiState.value.copy(themeMode = mode)
    }

    fun setPrimaryColor(colorLong: Long) {
        _uiState.value = _uiState.value.copy(primaryColor = colorLong)
    }

    /** Ajout de la fonction de changement de langue */
    fun setLanguage(lang: String) {
        _uiState.value = _uiState.value.copy(language = lang)
    }
}