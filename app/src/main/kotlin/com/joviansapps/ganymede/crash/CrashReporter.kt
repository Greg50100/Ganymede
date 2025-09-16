package com.joviansapps.ganymede.crash

import android.util.Log
import com.google.firebase.crashlytics.FirebaseCrashlytics
import java.util.Collections

// Façade Crashlytics directe (sans KTX). Fallback local en cas d'échec des appels.
object CrashReporter {
    @Volatile private var enabled: Boolean = true
    private const val MAX_BUFFER_SIZE = 50 // Limite pour éviter une consommation mémoire excessive

    // Buffer pour garder une trace des derniers logs en mémoire.
    private val buffer = Collections.synchronizedList(mutableListOf<String>())

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

    /**
     * Associe un identifiant utilisateur aux rapports de plantage.
     */
    fun setUserId(id: String) {
        if (!enabled) return
        crashlytics?.setUserId(id)
    }

    /**
     * Ajoute une clé personnalisée pour enrichir les rapports.
     */
    fun setCustomKey(key: String, value: String) {
        if (!enabled) return
        crashlytics?.setCustomKey(key, value)
    }

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
        addToBuffer(line)
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
        addToBuffer(line)
        val used = crashlytics?.let { c -> runCatching { c.log(line) }.isSuccess } ?: false
        if (!used) Log.i("CrashReporter", line)
    }

    private fun addToBuffer(line: String) {
        synchronized(buffer) {
            buffer.add(line)
            while (buffer.size > MAX_BUFFER_SIZE) {
                buffer.removeAt(0)
            }
        }
    }
}
