package com.joviansapps.ganymede.utils

/**
 * Types réutilisables et helpers liés à la gestion des erreurs.
 * Extrait depuis ErrorManager.kt pour clarifier les responsabilités.
 */

enum class ErrorCategory {
    NETWORK,
    DATABASE,
    VALIDATION,
    CALCULATION,
    SYSTEM,
    SECURITY,
    UNKNOWN
}

enum class ErrorSeverity {
    LOW,
    MEDIUM,
    HIGH,
    CRITICAL
}

data class ErrorContext(
    val screenName: String = "",
    val userAction: String = "",
    val additionalData: Map<String, Any> = emptyMap()
)

data class CategorizedError(
    val originalError: Throwable,
    val category: ErrorCategory,
    val isRecoverable: Boolean,
    val userMessage: String
)

sealed class RecoveryResult {
    object NotApplicable : RecoveryResult()
    data class ActionTaken(val description: String) : RecoveryResult()
    data class RetryRecommended(val suggestion: String) : RecoveryResult()
    data class ManualInterventionRequired(val instruction: String) : RecoveryResult()
    data class Failed(val reason: String) : RecoveryResult()
}

sealed class ErrorHandlingResult {
    data class Handled(val event: ErrorEvent, val recovery: RecoveryResult) : ErrorHandlingResult()
    data class Ignored(val reason: String) : ErrorHandlingResult()
    data class Failed(val reason: String, val error: Throwable) : ErrorHandlingResult()
}

data class ErrorEvent(
    val id: String,
    val category: ErrorCategory,
    val severity: ErrorSeverity,
    val userMessage: String,
    val technicalMessage: String,
    val isRecoverable: Boolean,
    val recoveryResult: RecoveryResult,
    val context: ErrorContext,
    val timestamp: Long
)

sealed class ErrorState {
    data class CalculationError(val message: String) : ErrorState()
}

/** Helper top-level attendu par les tests pour encapsuler une opération sûre */
fun <T> safeCalculation(
    onError: (String) -> Unit = {},
    block: () -> T
): T? = try {
    block()
} catch (e: ArithmeticException) {
    onError("Erreur arithmétique: ${e.message ?: ""}")
    null
} catch (e: Exception) {
    onError("Erreur: ${e.message ?: ""}")
    null
}
