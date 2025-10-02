package com.joviansapps.ganymede.core.domain.usecase

import com.joviansapps.ganymede.core.domain.model.CalculationResult
import com.joviansapps.ganymede.core.domain.model.CalculationType
import com.joviansapps.ganymede.core.domain.repository.CalculationRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Use case pour sauvegarder un calcul
 */
@Singleton
class SaveCalculationUseCase @Inject constructor(
    private val calculationRepository: CalculationRepository
) {
    suspend operator fun invoke(
        type: CalculationType,
        input: String,
        result: String
    ) = calculationRepository.saveCalculation(
        CalculationResult(
            id = "",
            type = type,
            input = input,
            result = result
        )
    )
}

/**
 * Use case pour obtenir l'historique des calculs
 */
@Singleton
class GetCalculationHistoryUseCase @Inject constructor(
    private val calculationRepository: CalculationRepository
) {
    operator fun invoke(): Flow<List<CalculationResult>> =
        calculationRepository.getAllCalculations()
}

/**
 * Use case pour gérer les favoris
 */
@Singleton
class ToggleFavoriteUseCase @Inject constructor(
    private val calculationRepository: CalculationRepository
) {
    suspend operator fun invoke(calculationId: String) =
        calculationRepository.toggleFavorite(calculationId)
}

/**
 * Use case pour la recherche dans l'historique
 */
@Singleton
class SearchCalculationsUseCase @Inject constructor(
    private val calculationRepository: CalculationRepository
) {
    operator fun invoke(query: String): Flow<List<CalculationResult>> =
        calculationRepository.searchCalculations(query)
}

/**
 * Use case pour nettoyer l'historique ancien
 */
@Singleton
class CleanOldHistoryUseCase @Inject constructor(
    private val calculationRepository: CalculationRepository
) {
    suspend operator fun invoke(daysToKeep: Int = 30) =
        calculationRepository.cleanOldHistory(daysToKeep)
}
