package com.joviansapps.ganymede.core.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.joviansapps.ganymede.core.domain.repository.PreferencesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implémentation concrète du repository pour les préférences utilisateur
 */
@Singleton
class PreferencesRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : PreferencesRepository {

    companion object {
        private val THEME_MODE = stringPreferencesKey("theme_mode")
        private val LANGUAGE = stringPreferencesKey("language")
        private val HAPTIC_FEEDBACK = booleanPreferencesKey("haptic_feedback")
        private val KEEP_SCREEN_ON = booleanPreferencesKey("keep_screen_on")
        private val NUMBER_FORMAT_MODE = stringPreferencesKey("number_format_mode")
    }

    override fun getThemeMode(): Flow<String> =
        dataStore.data.map { preferences ->
            preferences[THEME_MODE] ?: "AUTO"
        }

    override fun getLanguage(): Flow<String> =
        dataStore.data.map { preferences ->
            preferences[LANGUAGE] ?: "fr"
        }

    override fun getHapticFeedbackEnabled(): Flow<Boolean> =
        dataStore.data.map { preferences ->
            preferences[HAPTIC_FEEDBACK] ?: true
        }

    override fun getKeepScreenOnEnabled(): Flow<Boolean> =
        dataStore.data.map { preferences ->
            preferences[KEEP_SCREEN_ON] ?: false
        }

    override fun getNumberFormatMode(): Flow<String> =
        dataStore.data.map { preferences ->
            preferences[NUMBER_FORMAT_MODE] ?: "PLAIN"
        }

    override suspend fun setThemeMode(theme: String) {
        dataStore.edit { preferences ->
            preferences[THEME_MODE] = theme
        }
    }

    override suspend fun setLanguage(language: String) {
        dataStore.edit { preferences ->
            preferences[LANGUAGE] = language
        }
    }

    override suspend fun setHapticFeedbackEnabled(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[HAPTIC_FEEDBACK] = enabled
        }
    }

    override suspend fun setKeepScreenOnEnabled(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[KEEP_SCREEN_ON] = enabled
        }
    }

    override suspend fun setNumberFormatMode(mode: String) {
        dataStore.edit { preferences ->
            preferences[NUMBER_FORMAT_MODE] = mode
        }
    }
}
