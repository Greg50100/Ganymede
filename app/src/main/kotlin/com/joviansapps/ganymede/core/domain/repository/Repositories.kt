package com.joviansapps.ganymede.core.domain.repository

import com.joviansapps.ganymede.core.domain.model.CalculationResult
import com.joviansapps.ganymede.core.domain.model.CalculationType
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface pour l'historique des calculs
 */
interface CalculationRepository {

    fun getAllCalculations(): Flow<List<CalculationResult>>

    fun getFavoriteCalculations(): Flow<List<CalculationResult>>

    fun getCalculationsByType(type: CalculationType): Flow<List<CalculationResult>>

    fun searchCalculations(query: String): Flow<List<CalculationResult>>

    suspend fun saveCalculation(calculation: CalculationResult)

    suspend fun toggleFavorite(calculationId: String)

    suspend fun deleteCalculation(calculationId: String)

    suspend fun clearHistory()

    suspend fun cleanOldHistory(daysToKeep: Int = 30)
}

/**
 * Repository interface pour les préférences utilisateur
 */
interface PreferencesRepository {

    fun getThemeMode(): Flow<String>

    fun getLanguage(): Flow<String>

    fun getHapticFeedbackEnabled(): Flow<Boolean>

    fun getKeepScreenOnEnabled(): Flow<Boolean>

    fun getNumberFormatMode(): Flow<String>

    suspend fun setThemeMode(theme: String)

    suspend fun setLanguage(language: String)

    suspend fun setHapticFeedbackEnabled(enabled: Boolean)

    suspend fun setKeepScreenOnEnabled(enabled: Boolean)

    suspend fun setNumberFormatMode(mode: String)
}
