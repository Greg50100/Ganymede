package com.joviansapps.ganymede.crash

import android.content.Context
import android.util.Log
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.joviansapps.ganymede.BuildConfig
import com.joviansapps.ganymede.core.common.GanymedeError
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Interface pour le reporting de crashes et d'erreurs
 */
interface CrashReporter {
    fun logException(throwable: Throwable)
    fun logMessage(message: String, priority: Int = Log.INFO)
    fun setUserId(userId: String)
    fun setCustomKey(key: String, value: Any)
    fun recordException(throwable: Throwable)
    fun testCrash()
    fun logUserAction(action: String, screenName: String? = null)
}

/**
 * Implémentation du CrashReporter utilisant Firebase Crashlytics
 *
 * Cette classe centralise la gestion des crashes et du logging pour l'application.
 * Elle fournit une abstraction au-dessus de Firebase Crashlytics avec des fonctionnalités
 * supplémentaires pour le debugging et la personnalisation.
 */
@Singleton
class FirebaseCrashReporter @Inject constructor(
    @ApplicationContext private val context: Context
) : CrashReporter {

    private val crashlytics: FirebaseCrashlytics by lazy {
        FirebaseCrashlytics.getInstance()
    }

    companion object {
        private const val TAG = "CrashReporter"

        // Clés personnalisées pour Crashlytics
        private const val KEY_APP_VERSION = "app_version"
        private const val KEY_BUILD_TYPE = "build_type"
        private const val KEY_USER_ACTION = "user_action"
        private const val KEY_SCREEN_NAME = "screen_name"
    }

    init {
        initializeCrashlytics()
    }

    /**
     * Initialise Crashlytics avec les configurations de base
     */
    private fun initializeCrashlytics() {
        try {
            // Désactiver en mode debug pour éviter les rapports de développement
            crashlytics.setCrashlyticsCollectionEnabled(!BuildConfig.DEBUG)

            // Ajouter des métadonnées de base
            crashlytics.setCustomKey(KEY_APP_VERSION, BuildConfig.VERSION_NAME)
            crashlytics.setCustomKey(KEY_BUILD_TYPE, BuildConfig.BUILD_TYPE)

            Log.d(TAG, "Crashlytics initialisé - Collection: ${!BuildConfig.DEBUG}")
        } catch (e: Exception) {
            Log.e(TAG, "Erreur lors de l'initialisation de Crashlytics", e)
        }
    }

    /**
     * Enregistre une exception dans Crashlytics
     */
    override fun logException(throwable: Throwable) {
        try {
            // Ajouter des informations contextuelles selon le type d'erreur
            when (throwable) {
                is GanymedeError.NetworkError -> {
                    crashlytics.setCustomKey("error_type", "network")
                }
                is GanymedeError.DatabaseError -> {
                    crashlytics.setCustomKey("error_type", "database")
                }
                is GanymedeError.ValidationError -> {
                    crashlytics.setCustomKey("error_type", "validation")
                    crashlytics.setCustomKey("validation_message", throwable.message)
                }
                is GanymedeError.CalculationError -> {
                    crashlytics.setCustomKey("error_type", "calculation")
                    crashlytics.setCustomKey("calculation_error", throwable.message)
                }
                else -> {
                    crashlytics.setCustomKey("error_type", "unknown")
                }
            }

            crashlytics.recordException(throwable)

            if (BuildConfig.DEBUG) {
                Log.e(TAG, "Exception enregistrée", throwable)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Erreur lors de l'enregistrement de l'exception", e)
        }
    }

    /**
     * Enregistre un message personnalisé
     */
    override fun logMessage(message: String, priority: Int) {
        try {
            crashlytics.log("${getPriorityString(priority)}: $message")

            if (BuildConfig.DEBUG) {
                Log.println(priority, TAG, message)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Erreur lors de l'enregistrement du message", e)
        }
    }

    /**
     * Définit un identifiant utilisateur
     */
    override fun setUserId(userId: String) {
        try {
            crashlytics.setUserId(userId)
            Log.d(TAG, "ID utilisateur défini: $userId")
        } catch (e: Exception) {
            Log.e(TAG, "Erreur lors de la définition de l'ID utilisateur", e)
        }
    }

    /**
     * Définit une clé personnalisée
     */
    override fun setCustomKey(key: String, value: Any) {
        try {
            when (value) {
                is String -> crashlytics.setCustomKey(key, value)
                is Boolean -> crashlytics.setCustomKey(key, value)
                is Int -> crashlytics.setCustomKey(key, value)
                is Long -> crashlytics.setCustomKey(key, value)
                is Float -> crashlytics.setCustomKey(key, value)
                is Double -> crashlytics.setCustomKey(key, value)
                else -> crashlytics.setCustomKey(key, value.toString())
            }

            if (BuildConfig.DEBUG) {
                Log.d(TAG, "Clé personnalisée définie: $key = $value")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Erreur lors de la définition de la clé personnalisée", e)
        }
    }

    /**
     * Enregistre une exception (alias pour logException)
     */
    override fun recordException(throwable: Throwable) {
        logException(throwable)
    }

    /**
     * Force un crash pour tester Crashlytics
     * ATTENTION: À utiliser uniquement pour les tests
     */
    override fun testCrash() {
        if (BuildConfig.DEBUG) {
            Log.w(TAG, "Test de crash demandé - Mode DEBUG")
        }
        throw RuntimeException("Test crash déclenché manuellement")
    }

    /**
     * Méthodes utilitaires pour des scénarios spécifiques
     */

    /**
     * Enregistre une action utilisateur pour le contexte
     */
    override fun logUserAction(action: String, screenName: String? ) {
        setCustomKey(KEY_USER_ACTION, action)
        screenName?.let { setCustomKey(KEY_SCREEN_NAME, it) }
        logMessage("Action utilisateur: $action${screenName?.let { " sur $it" } ?: ""}")
    }

    /**
     * Enregistre une erreur de calcul avec contexte
     */
    fun logCalculationError(operation: String, input: String, error: String) {
        setCustomKey("calculation_operation", operation)
        setCustomKey("calculation_input", input)
        setCustomKey("calculation_error_detail", error)
        logException(GanymedeError.CalculationError("$operation: $error"))
    }

    /**
     * Enregistre une erreur de validation avec détails
     */
    fun logValidationError(field: String, value: String, reason: String) {
        setCustomKey("validation_field", field)
        setCustomKey("validation_value", value)
        setCustomKey("validation_reason", reason)
        logException(GanymedeError.ValidationError("Validation échouée pour $field: $reason"))
    }

    /**
     * Convertit la priorité en chaîne lisible
     */
    private fun getPriorityString(priority: Int): String = when (priority) {
        Log.VERBOSE -> "VERBOSE"
        Log.DEBUG -> "DEBUG"
        Log.INFO -> "INFO"
        Log.WARN -> "WARN"
        Log.ERROR -> "ERROR"
        Log.ASSERT -> "ASSERT"
        else -> "UNKNOWN"
    }
}
