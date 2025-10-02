package com.joviansapps.ganymede.core.data.repository

import com.joviansapps.ganymede.core.data.local.dao.CalculationDao
import com.joviansapps.ganymede.core.data.local.entity.toEntity
import com.joviansapps.ganymede.core.data.local.entity.toDomain
import com.joviansapps.ganymede.core.domain.model.CalculationResult
import com.joviansapps.ganymede.core.domain.model.CalculationType
import com.joviansapps.ganymede.core.domain.repository.CalculationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implémentation concrète du repository pour les calculs
 */
@Singleton
class CalculationRepositoryImpl @Inject constructor(
    private val calculationDao: CalculationDao
) : CalculationRepository {

    override fun getAllCalculations(): Flow<List<CalculationResult>> =
        calculationDao.getAllCalculations().map { entities ->
            entities.map { it.toDomain() }
        }

    override fun getFavoriteCalculations(): Flow<List<CalculationResult>> =
        calculationDao.getFavoriteCalculations().map { entities ->
            entities.map { it.toDomain() }
        }

    override fun getCalculationsByType(type: CalculationType): Flow<List<CalculationResult>> =
        calculationDao.getCalculationsByType(type.name).map { entities ->
            entities.map { it.toDomain() }
        }

    override fun searchCalculations(query: String): Flow<List<CalculationResult>> =
        calculationDao.searchCalculations(query).map { entities ->
            entities.map { it.toDomain() }
        }

    override suspend fun saveCalculation(calculation: CalculationResult) {
        val calculationWithId = if (calculation.id.isEmpty()) {
            calculation.copy(id = UUID.randomUUID().toString())
        } else {
            calculation
        }
        calculationDao.insertCalculation(calculationWithId.toEntity())
    }

    override suspend fun toggleFavorite(calculationId: String) {
        // Utilise la requête SQL dédiée pour éviter la collecte de Flow
        calculationDao.toggleFavorite(calculationId)
    }

    override suspend fun deleteCalculation(calculationId: String) {
        // Suppression directe par identifiant
        calculationDao.deleteById(calculationId)
    }

    override suspend fun clearHistory() {
        calculationDao.clearAllHistory()
    }

    override suspend fun cleanOldHistory(daysToKeep: Int) {
        val cutoffTime = System.currentTimeMillis() - (daysToKeep * 24 * 60 * 60 * 1000L)
        calculationDao.deleteOldCalculations(cutoffTime)
    }
}
