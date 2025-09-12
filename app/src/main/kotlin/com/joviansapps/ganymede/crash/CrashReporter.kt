package com.joviansapps.ganymede.crash

import android.util.Log
import java.util.Collections
import com.google.firebase.crashlytics.FirebaseCrashlytics

// Façade Crashlytics directe (sans KTX). Fallback local en cas d'échec des appels.
object CrashReporter {
    @Volatile private var enabled: Boolean = true
    // TODO: Purger/limiter le buffer si nécessaire (taille max) et envisager une persistance si offline.
    private val buffer = Collections.synchronizedList(mutableListOf<String>())

    // TODO: S'assurer que updateEnabled() est appelée tôt (Application.onCreate) pour refléter le choix utilisateur.
    // TODO: Ajouter éventuellement des setUserId/setCustomKey pour enrichir les rapports (si besoin produit).
    private val crashlytics: FirebaseCrashlytics? by lazy {
        runCatching { FirebaseCrashlytics.getInstance() }.getOrNull()
    }

    fun updateEnabled(value: Boolean) {
        enabled = value
        crashlytics?.let { c ->
            runCatching { c.setCrashlyticsCollectionEnabled(value) }
                .onFailure { Log.w("CrashReporter", "Activation Crashlytics échouée: ${it.message}") }
        }
    }

    fun clearBufferedReports() = buffer.clear()
    fun getBufferedReports(): List<String> = buffer.toList()

    fun log(throwable: Throwable, message: String? = null, extra: Map<String, Any?> = emptyMap()) {
        if (!enabled) return
        val line = buildString {
            append("CRASH:")
            append(message ?: throwable.message)
            if (extra.isNotEmpty()) {
                append(" | ")
                append(extra.entries.joinToString { "${it.key}=${it.value}" })
            }
        }
        buffer.add(line)
        val used = crashlytics?.let { c ->
            runCatching {
                c.log(line)
                c.recordException(throwable)
            }.isSuccess
        } ?: false
        if (!used) Log.e("CrashReporter", line, throwable)
    }

    fun logMessage(message: String, extra: Map<String, Any?> = emptyMap()) {
        if (!enabled) return
        val line = buildString {
            append("MSG:")
            append(message)
            if (extra.isNotEmpty()) {
                append(" | ")
                append(extra.entries.joinToString { "${it.key}=${it.value}" })
            }
        }
        buffer.add(line)
        val used = crashlytics?.let { c -> runCatching { c.log(line) }.isSuccess } ?: false
        if (!used) Log.i("CrashReporter", line)
    }
}
