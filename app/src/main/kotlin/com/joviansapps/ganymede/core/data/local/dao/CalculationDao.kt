package com.joviansapps.ganymede.core.data.local.dao

import androidx.room.*
import com.joviansapps.ganymede.core.data.local.entity.CalculationEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO pour l'accès aux données des calculs
 */
@Dao
interface CalculationDao {

    @Query("SELECT * FROM calculation_history ORDER BY timestamp DESC")
    fun getAllCalculations(): Flow<List<CalculationEntity>>

    @Query("SELECT * FROM calculation_history WHERE isFavorite = 1 ORDER BY timestamp DESC")
    fun getFavoriteCalculations(): Flow<List<CalculationEntity>>

    @Query("SELECT * FROM calculation_history WHERE type = :type ORDER BY timestamp DESC LIMIT :limit")
    fun getCalculationsByType(type: String, limit: Int = 50): Flow<List<CalculationEntity>>

    @Query("SELECT * FROM calculation_history WHERE input LIKE '%' || :query || '%' OR result LIKE '%' || :query || '%'")
    fun searchCalculations(query: String): Flow<List<CalculationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCalculation(calculation: CalculationEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCalculations(calculations: List<CalculationEntity>)

    @Update
    suspend fun updateCalculation(calculation: CalculationEntity)

    @Delete
    suspend fun deleteCalculation(calculation: CalculationEntity)

    @Query("DELETE FROM calculation_history WHERE timestamp < :cutoffTime AND isFavorite = 0")
    suspend fun deleteOldCalculations(cutoffTime: Long)

    @Query("DELETE FROM calculation_history")
    suspend fun clearAllHistory()

    @Query("UPDATE calculation_history SET isFavorite = :isFavorite WHERE id = :id")
    suspend fun updateFavoriteStatus(id: String, isFavorite: Boolean)

    // Nouvelles méthodes dédiées pour opérations ciblées et performantes
    @Query("SELECT * FROM calculation_history WHERE id = :id LIMIT 1")
    suspend fun getCalculationById(id: String): CalculationEntity?

    @Query("DELETE FROM calculation_history WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("UPDATE calculation_history SET isFavorite = CASE WHEN isFavorite = 1 THEN 0 ELSE 1 END WHERE id = :id")
    suspend fun toggleFavorite(id: String)
}
