package com.joviansapps.ganymede.core.domain.model

/**
 * Modèle de domaine pour les résultats de calculs
 */
data class CalculationResult(
    val id: String,
    val type: CalculationType,
    val input: String,
    val result: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isFavorite: Boolean = false
)

enum class CalculationType {
    BASIC_MATH,
    MATRIX,
    ELECTRONICS,
    PHYSICS,
    CHEMISTRY,
    UNIT_CONVERSION
}

/**
 * Modèle pour l'historique des calculs
 */
data class CalculationHistory(
    val calculations: List<CalculationResult> = emptyList(),
    val favorites: List<CalculationResult> = emptyList()
)
