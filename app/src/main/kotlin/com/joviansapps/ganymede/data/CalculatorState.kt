package com.joviansapps.ganymede.data

import com.joviansapps.ganymede.core.common.GanymedeError

/**
 * Représente l'état complet de la calculatrice dans un objet immutable.
 * Cette approche rend la gestion d'état plus prévisible et facilite le débogage.
 */
data class CalculatorState(
    val expression: String = "",
    val result: String = "0",
    val history: List<CalculationHistoryItem> = emptyList(),
    val angleMode: AngleMode = AngleMode.DEGREES,
    val memory: CalculatorMemory = CalculatorMemory(),
    val operationStatus: OperationStatus = OperationStatus.Ready
) {

    /**
     * Indique si l'expression est vide
     */
    val isExpressionEmpty: Boolean get() = expression.isBlank()

    /**
     * Indique si il y a un résultat valide
     */
    val hasValidResult: Boolean get() = result != "0" && result.isNotBlank()

    /**
     * Indique si la calculatrice vient d'évaluer une expression
     */
    val justEvaluated: Boolean get() = operationStatus == OperationStatus.JustEvaluated

    /**
     * Indique si une opération est en cours
     */
    val isProcessing: Boolean get() = operationStatus == OperationStatus.Processing

    // === Propriétés de compatibilité avec l'ancien code ===

    /**
     * Compatibilité : indique si le mode degré est activé
     */
    val isDegrees: Boolean get() = angleMode == AngleMode.DEGREES

    /**
     * Compatibilité : indique si la mémoire contient une valeur
     */
    val hasMemory: Boolean get() = memory.hasValue

    /**
     * Valide l'état actuel et retourne les erreurs éventuelles
     */
    fun validate(): List<GanymedeError> {
        val errors = mutableListOf<GanymedeError>()

        // Vérification de la longueur de l'expression
        if (expression.length > MAX_EXPRESSION_LENGTH) {
            errors.add(GanymedeError.ValidationError("Expression trop longue"))
        }

        // Vérification du résultat
        if (result.contains("Error") || result.contains("NaN") || result.contains("Infinity")) {
            errors.add(GanymedeError.CalculationError("Résultat invalide: $result"))
        }

        return errors
    }

    companion object {
        const val MAX_EXPRESSION_LENGTH = 500
        const val MAX_HISTORY_SIZE = 100

        /**
         * Crée un état initial par défaut
         */
        fun initial() = CalculatorState()

        /**
         * Crée un état d'erreur
         */
        fun error(message: String) = CalculatorState(
            result = "Error: $message",
            operationStatus = OperationStatus.Error
        )
    }
}

/**
 * Modes d'angle pour les fonctions trigonométriques
 */
enum class AngleMode(val displayName: String, val symbol: String) {
    DEGREES("Degrés", "°"),
    RADIANS("Radians", "rad"),
    GRADIANS("Gradians", "grad");

    fun next(): AngleMode = when (this) {
        DEGREES -> RADIANS
        RADIANS -> GRADIANS
        GRADIANS -> DEGREES
    }
}

/**
 * État de la mémoire de la calculatrice
 */
data class CalculatorMemory(
    val value: Double = 0.0,
    val hasValue: Boolean = false
) {
    /**
     * Ajoute une valeur à la mémoire
     */
    fun add(amount: Double): CalculatorMemory = copy(
        value = value + amount,
        hasValue = true
    )

    /**
     * Soustrait une valeur de la mémoire
     */
    fun subtract(amount: Double): CalculatorMemory = copy(
        value = value - amount,
        hasValue = true
    )

    /**
     * Définit une nouvelle valeur en mémoire
     */
    fun store(newValue: Double): CalculatorMemory = copy(
        value = newValue,
        hasValue = true
    )

    /**
     * Efface la mémoire
     */
    fun clear(): CalculatorMemory = CalculatorMemory()
}

/**
 * Statut d'opération de la calculatrice
 */
enum class OperationStatus {
    Ready,          // Prêt pour une nouvelle opération
    Processing,     // Traitement en cours
    JustEvaluated,  // Vient d'évaluer une expression
    Error           // Erreur dans l'opération
}

/**
 * Item d'historique des calculs
 */
data class CalculationHistoryItem(
    val expression: String,
    val result: String,
    val timestamp: Long = System.currentTimeMillis(),
    val angleMode: AngleMode = AngleMode.DEGREES
) {
    /**
     * Formate l'item pour l'affichage
     */
    fun formatForDisplay(): String = "$expression = $result"

    /**
     * Indique si le calcul était valide
     */
    val isValid: Boolean get() = !result.contains("Error") && !result.contains("NaN")
}
