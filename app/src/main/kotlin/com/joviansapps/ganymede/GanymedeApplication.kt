package com.joviansapps.ganymede

import android.app.Application
import android.content.Context
import android.util.Log
import com.google.firebase.FirebaseApp
import com.google.firebase.crashlytics.FirebaseCrashlytics
import dagger.hilt.android.HiltAndroidApp

/**
 * Classe Application principale pour Ganymede
 *
 * Cette classe est responsable de :
 * - L'initialisation de Hilt pour l'injection de dépendances
 * - La configuration de Firebase et Crashlytics
 * - L'initialisation des composants globaux
 * - La gestion de la mémoire de l'application
 */
@HiltAndroidApp
class GanymedeApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        // Initialisation de l'instance globale
        instance = this

        // Configuration des outils de développement
        configureLogging()

        // Initialisation de Firebase
        initializeFirebase()

        // Configuration des outils de crash reporting
        configureCrashlytics()

        Log.d(TAG, "Application Ganymede initialisée avec succès")
    }

    /**
     * Configuration du logging selon l'environnement
     */
    private fun configureLogging() {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "Mode DEBUG activé - Logging détaillé disponible")
        }
    }

    /**
     * Initialisation de Firebase
     */
    private fun initializeFirebase() {
        try {
            FirebaseApp.initializeApp(this)
            Log.d(TAG, "Firebase initialisé avec succès")
        } catch (e: Exception) {
            Log.e(TAG, "Erreur lors de l'initialisation de Firebase", e)
        }
    }

    /**
     * Configuration de Firebase Crashlytics
     */
    private fun configureCrashlytics() {
        try {
            val crashlytics = FirebaseCrashlytics.getInstance()

            // Désactiver Crashlytics en mode debug pour éviter les rapports de test
            crashlytics.setCrashlyticsCollectionEnabled(!BuildConfig.DEBUG)

            // Configuration des métadonnées personnalisées
            crashlytics.setCustomKey("app_version", BuildConfig.VERSION_NAME)
            crashlytics.setCustomKey("build_type", BuildConfig.BUILD_TYPE)

            Log.d(TAG, "Crashlytics configuré avec succès")
        } catch (e: Exception) {
            Log.e(TAG, "Erreur lors de la configuration de Crashlytics", e)
        }
    }

    /**
     * Gestion de la mémoire faible
     */
    override fun onLowMemory() {
        super.onLowMemory()
        Log.w(TAG, "Mémoire faible détectée - nettoyage des caches recommandé")
        // Ici on pourrait ajouter la logique de nettoyage des caches
    }

    /**
     * Gestion du trimming de mémoire
     */
    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)
        when (level) {
            TRIM_MEMORY_UI_HIDDEN -> {
                Log.d(TAG, "Interface utilisateur cachée - nettoyage léger")
            }
            TRIM_MEMORY_RUNNING_MODERATE,
            TRIM_MEMORY_RUNNING_LOW,
            TRIM_MEMORY_RUNNING_CRITICAL -> {
                Log.w(TAG, "Pression mémoire détectée (niveau: $level)")
            }
        }
    }

    companion object {
        private const val TAG = "GanymedeApplication"

        @Volatile
        private var instance: GanymedeApplication? = null

        /**
         * Retourne l'instance globale de l'application
         */
        fun getInstance(): GanymedeApplication {
            return instance ?: throw IllegalStateException(
                "L'application n'a pas été initialisée correctement"
            )
        }

        /**
         * Retourne le contexte de l'application
         */
        fun getAppContext(): Context = getInstance().applicationContext
    }
}
