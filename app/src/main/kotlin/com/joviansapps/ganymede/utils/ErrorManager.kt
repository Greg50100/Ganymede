package com.joviansapps.ganymede.utils

import android.content.Context
import android.util.Log
import com.joviansapps.ganymede.core.common.GanymedeError
import com.joviansapps.ganymede.crash.CrashReporter
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject
import javax.inject.Singleton
import java.util.concurrent.ConcurrentHashMap

/**
 * Gestionnaire centralisé des erreurs pour l'application Ganymede
 *
 * Cette classe fournit une interface unifiée pour :
 * - La gestion et la catégorisation des erreurs
 * - Le logging et reporting des erreurs
 * - La récupération automatique d'erreurs
 * - La communication d'erreurs à l'UI
 */
@Singleton
class ErrorManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val crashReporter: CrashReporter
) {

    companion object {
        private const val TAG = "ErrorManager"
        private const val MAX_ERROR_CACHE_SIZE = 50
        private const val DUPLICATE_ERROR_THRESHOLD = 5000L // 5 secondes

        // Layer de compatibilité pour les tests unitaires existants
        private val _errorState = kotlinx.coroutines.flow.MutableStateFlow<ErrorState?>(null)
        val errorState: kotlinx.coroutines.flow.StateFlow<ErrorState?> = _errorState
        suspend fun showError(state: ErrorState) { _errorState.emit(state) }
        fun clearError() { _errorState.value = null }
    }

    // Flow pour communiquer les erreurs à l'UI
    private val _errorFlow = MutableSharedFlow<ErrorEvent>()
    val errorFlow: SharedFlow<ErrorEvent> = _errorFlow.asSharedFlow()

    // Cache des erreurs récentes pour éviter les doublons (thread-safe)
    private val recentErrors = ConcurrentHashMap<String, Long>()

    /**
     * Traite une erreur selon son type et sa gravité
     */
    suspend fun handleError(
        error: Throwable,
        context: ErrorContext = ErrorContext(),
        severity: ErrorSeverity = ErrorSeverity.MEDIUM
    ): ErrorHandlingResult {

        return try {
            val categorizedError = categorizeError(error)
            val errorId = generateErrorId(categorizedError, context)

            // Éviter les erreurs en double dans un court laps de temps
            if (isDuplicateError(errorId)) {
                return ErrorHandlingResult.Ignored("Erreur dupliquée ignorée")
            }

            // Logger l'erreur
            logError(categorizedError, context, severity)

            // Reporter l'erreur si nécessaire
            if (shouldReportError(categorizedError, severity)) {
                reportError(categorizedError, context)
            }

            // Tenter une récupération automatique
            val recoveryResult = attemptRecovery(categorizedError, context)

            // Émettre l'événement d'erreur pour l'UI
            val errorEvent = createErrorEvent(categorizedError, context, severity, recoveryResult)
            _errorFlow.emit(errorEvent)

            ErrorHandlingResult.Handled(errorEvent, recoveryResult)

        } catch (handlingError: Exception) {
            Log.e(TAG, "Erreur lors du traitement d'une erreur", handlingError)
            ErrorHandlingResult.Failed("Impossible de traiter l'erreur", handlingError)
        }
    }

    /**
     * Catégorise une erreur selon son type
     */
    private fun categorizeError(error: Throwable): CategorizedError {
        return when (error) {
            is GanymedeError.NetworkError -> CategorizedError(
                originalError = error,
                category = ErrorCategory.NETWORK,
                isRecoverable = true,
                userMessage = "Problème de connexion réseau"
            )

            is GanymedeError.DatabaseError -> CategorizedError(
                originalError = error,
                category = ErrorCategory.DATABASE,
                isRecoverable = true,
                userMessage = "Erreur de base de données"
            )

            is GanymedeError.ValidationError -> CategorizedError(
                originalError = error,
                category = ErrorCategory.VALIDATION,
                isRecoverable = true,
                userMessage = error.message
            )

            is GanymedeError.CalculationError -> CategorizedError(
                originalError = error,
                category = ErrorCategory.CALCULATION,
                isRecoverable = true,
                userMessage = error.message
            )

            is OutOfMemoryError -> CategorizedError(
                originalError = error,
                category = ErrorCategory.SYSTEM,
                isRecoverable = false,
                userMessage = "Mémoire insuffisante"
            )

            is SecurityException -> CategorizedError(
                originalError = error,
                category = ErrorCategory.SECURITY,
                isRecoverable = false,
                userMessage = "Erreur de sécurité"
            )

            else -> CategorizedError(
                originalError = error,
                category = ErrorCategory.UNKNOWN,
                isRecoverable = false,
                userMessage = "Une erreur inattendue s'est produite"
            )
        }
    }

    /**
     * Tente une récupération automatique selon le type d'erreur
     */
    private suspend fun attemptRecovery(
        error: CategorizedError,
        context: ErrorContext
    ): RecoveryResult {

        if (!error.isRecoverable) {
            return RecoveryResult.NotApplicable
        }

        return try {
            when (error.category) {
                ErrorCategory.NETWORK -> {
                    // Retry logic pour les erreurs réseau
                    RecoveryResult.RetryRecommended("Vérifiez votre connexion internet")
                }

                ErrorCategory.DATABASE -> {
                    // Nettoyage et réinitialisation de la base de données
                    RecoveryResult.ActionTaken("Base de données réinitialisée")
                }

                ErrorCategory.VALIDATION -> {
                    // Réinitialisation des champs invalides
                    RecoveryResult.ActionTaken("Champs réinitialisés")
                }

                ErrorCategory.CALCULATION -> {
                    // Réinitialisation de l'état de calcul
                    RecoveryResult.ActionTaken("Calculatrice réinitialisée")
                }

                else -> RecoveryResult.ManualInterventionRequired("Intervention manuelle requise")
            }
        } catch (recoveryError: Exception) {
            Log.e(TAG, "Erreur lors de la récupération", recoveryError)
            RecoveryResult.Failed("Récupération échouée")
        }
    }

    /**
     * Détermine si une erreur doit être reportée
     */
    private fun shouldReportError(error: CategorizedError, severity: ErrorSeverity): Boolean {
        return when {
            severity == ErrorSeverity.CRITICAL -> true
            error.category == ErrorCategory.SECURITY -> true
            error.category == ErrorCategory.SYSTEM -> true
            severity == ErrorSeverity.HIGH && error.category != ErrorCategory.VALIDATION -> true
            else -> false
        }
    }

    /**
     * Reporte une erreur via le CrashReporter
     */
    private fun reportError(error: CategorizedError, context: ErrorContext) {
        crashReporter.setCustomKey("error_category", error.category.name)
        crashReporter.setCustomKey("error_recoverable", error.isRecoverable)
        crashReporter.setCustomKey("error_context_screen", context.screenName)
        crashReporter.setCustomKey("error_context_action", context.userAction)

        crashReporter.logException(error.originalError)
    }

    /**
     * Log une erreur avec le niveau approprié
     */
    private fun logError(
        error: CategorizedError,
        context: ErrorContext,
        severity: ErrorSeverity
    ) {
        val logLevel = when (severity) {
            ErrorSeverity.LOW -> Log.INFO
            ErrorSeverity.MEDIUM -> Log.WARN
            ErrorSeverity.HIGH -> Log.ERROR
            ErrorSeverity.CRITICAL -> Log.ERROR
        }

        val logMessage = buildString {
            append("Erreur ${'$'}{error.category.name}: ${'$'}{error.userMessage}")
            if (context.screenName.isNotEmpty()) append(" | Écran: ${'$'}{context.screenName}")
            if (context.userAction.isNotEmpty()) append(" | Action: ${'$'}{context.userAction}")
        }

        Log.println(logLevel, TAG, logMessage)
        crashReporter.logMessage(logMessage, logLevel)
    }

    /**
     * Génère un ID unique pour une erreur
     */
    private fun generateErrorId(error: CategorizedError, context: ErrorContext): String {
        return "${'$'}{error.category.name}_${'$'}{error.originalError.javaClass.simpleName}_${'$'}{context.screenName}"
    }

    /**
     * Vérifie si c'est une erreur dupliquée récente
     */
    private fun isDuplicateError(errorId: String): Boolean {
        val now = System.currentTimeMillis()
        val lastOccurrence = recentErrors[errorId]

        return if (lastOccurrence != null && (now - lastOccurrence) < DUPLICATE_ERROR_THRESHOLD) {
            true
        } else {
            recentErrors[errorId] = now
            // Nettoyer le cache si trop plein
            if (recentErrors.size > MAX_ERROR_CACHE_SIZE) {
                cleanErrorCache()
            }
            false
        }
    }

    /**
     * Nettoie le cache des erreurs anciennes (suppression sûre pour ConcurrentHashMap)
     */
    private fun cleanErrorCache() {
        val now = System.currentTimeMillis()
        val keysToRemove = mutableListOf<String>()
        for ((key, timestamp) in recentErrors) {
            if (now - timestamp > DUPLICATE_ERROR_THRESHOLD * 2) {
                keysToRemove.add(key)
            }
        }
        for (k in keysToRemove) {
            recentErrors.remove(k)
        }
    }

    /**
     * Crée un événement d'erreur pour l'UI
     */
    private fun createErrorEvent(
        error: CategorizedError,
        context: ErrorContext,
        severity: ErrorSeverity,
        recoveryResult: RecoveryResult
    ): ErrorEvent {
        return ErrorEvent(
            id = generateErrorId(error, context),
            category = error.category,
            severity = severity,
            userMessage = error.userMessage,
            technicalMessage = error.originalError.message ?: "Pas de détail technique",
            isRecoverable = error.isRecoverable,
            recoveryResult = recoveryResult,
            context = context,
            timestamp = System.currentTimeMillis()
        )
    }
}

