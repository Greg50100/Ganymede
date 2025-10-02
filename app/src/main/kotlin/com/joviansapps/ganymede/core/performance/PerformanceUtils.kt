package com.joviansapps.ganymede.core.performance

import android.os.Build
import android.os.Debug
import android.os.SystemClock
import android.util.Log
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.ConcurrentHashMap
import kotlin.system.measureNanoTime
import kotlin.system.measureTimeMillis

/**
 * Collecte un Flow seulement quand le lifecycle est au moins STARTED
 * Optimise les performances en évitant les mises à jour inutiles
 */
@Composable
fun <T> Flow<T>.collectAsStateWithLifecycle(
    initial: T,
    minActiveState: Lifecycle.State = Lifecycle.State.STARTED
): State<T> {
    val lifecycleOwner = LocalLifecycleOwner.current
    val state = remember { mutableStateOf(initial) }

    LaunchedEffect(this, lifecycleOwner.lifecycle) {
        var isActive = false

        val observer = LifecycleEventObserver { _, event ->
            isActive = lifecycleOwner.lifecycle.currentState.isAtLeast(minActiveState)
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        try {
            collectLatest { value ->
                if (isActive) {
                    state.value = value
                }
            }
        } finally {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    return state
}

/**
 * Cache pour les résultats de calculs complexes
 */
object PerformanceCache {
    private val mathCache = mutableMapOf<String, Any?>()

    @Suppress("UNCHECKED_CAST")
    fun <T> getOrPut(key: String, factory: () -> T): T {
        return mathCache.getOrPut(key) { factory() } as T
    }

    fun clear() {
        mathCache.clear()
    }

    fun remove(key: String) {
        mathCache.remove(key)
    }
}

/**
 * Extension pour mémoriser les calculs coûteux
 */
inline fun <T> memoized(key: String, noinline computation: () -> T): T {
    return PerformanceCache.getOrPut(key, computation)
}

/**
 * Utilitaires pour le monitoring et l'optimisation des performances
 *
 * Cette classe fournit des outils pour :
 * - Mesurer les temps d'exécution
 * - Monitorer l'utilisation mémoire
 * - Détecter les fuites de mémoire
 * - Optimiser les opérations critiques
 */
object PerformanceUtils {

    private const val TAG = "PerformanceUtils"
    private const val PERFORMANCE_THRESHOLD_MS = 100L
    private const val MEMORY_THRESHOLD_MB = 50L

    // Métriques de performance
    private val _performanceMetrics = MutableStateFlow<PerformanceMetrics>(PerformanceMetrics())
    val performanceMetrics: StateFlow<PerformanceMetrics> = _performanceMetrics.asStateFlow()

    // Cache des mesures de performance
    private val performanceCache = ConcurrentHashMap<String, PerformanceMeasurement>()

    // Scope pour les opérations asynchrones
    private val performanceScope = CoroutineScope(Dispatchers.Default)

    /**
     * Mesure le temps d'exécution d'une opération
     */
    inline fun <T> measureOperation(
        operationName: String,
        logResults: Boolean = true,
        block: () -> T
    ): PerformanceResult<T> {
        val startTime = SystemClock.elapsedRealtime()
        val startMemory = getCurrentMemoryUsage()

        return try {
            val result = block()
            val executionTime = SystemClock.elapsedRealtime() - startTime
            val endMemory = getCurrentMemoryUsage()
            val memoryDelta = endMemory - startMemory

            val measurement = PerformanceMeasurement(
                operationName = operationName,
                executionTimeMs = executionTime,
                memoryUsedMB = memoryDelta,
                timestamp = System.currentTimeMillis(),
                isSuccessful = true
            )

            recordMeasurement(measurement)

            if (logResults) {
                logPerformanceMeasurement(measurement)
            }

            PerformanceResult.Success(result, measurement)

        } catch (e: Exception) {
            val executionTime = SystemClock.elapsedRealtime() - startTime
            val measurement = PerformanceMeasurement(
                operationName = operationName,
                executionTimeMs = executionTime,
                memoryUsedMB = 0L,
                timestamp = System.currentTimeMillis(),
                isSuccessful = false,
                error = e.message
            )

            recordMeasurement(measurement)
            PerformanceResult.Error(e, measurement)
        }
    }

    /**
     * Mesure le temps d'exécution d'une opération suspendante
     */
    suspend inline fun <T> measureSuspendOperation(
        operationName: String,
        logResults: Boolean = true,
        crossinline block: suspend () -> T
    ): PerformanceResult<T> = withContext(Dispatchers.Default) {
        val startTime = SystemClock.elapsedRealtime()
        val startMemory = getCurrentMemoryUsage()

        try {
            val result = block()
            val executionTime = SystemClock.elapsedRealtime() - startTime
            val endMemory = getCurrentMemoryUsage()
            val memoryDelta = endMemory - startMemory

            val measurement = PerformanceMeasurement(
                operationName = operationName,
                executionTimeMs = executionTime,
                memoryUsedMB = memoryDelta,
                timestamp = System.currentTimeMillis(),
                isSuccessful = true
            )

            recordMeasurement(measurement)

            if (logResults) {
                logPerformanceMeasurement(measurement)
            }

            PerformanceResult.Success(result, measurement)

        } catch (e: Exception) {
            val executionTime = SystemClock.elapsedRealtime() - startTime
            val measurement = PerformanceMeasurement(
                operationName = operationName,
                executionTimeMs = executionTime,
                memoryUsedMB = 0L,
                timestamp = System.currentTimeMillis(),
                isSuccessful = false,
                error = e.message
            )

            recordMeasurement(measurement)
            PerformanceResult.Error(e, measurement)
        }
    }

    /**
     * Démarre un profiler pour une opération longue
     */
    fun startProfiler(operationName: String): PerformanceProfiler {
        return PerformanceProfiler(operationName).apply { start() }
    }

    /**
     * Obtient l'utilisation mémoire actuelle en MB
     */
    fun getCurrentMemoryUsage(): Long {
        val runtime = Runtime.getRuntime()
        val usedMemory = runtime.totalMemory() - runtime.freeMemory()
        return usedMemory / (1024 * 1024) // Conversion en MB
    }

    /**
     * Obtient des informations détaillées sur la mémoire
     */
    fun getDetailedMemoryInfo(): DetailedMemoryInfo {
        val runtime = Runtime.getRuntime()
        val totalMemory = runtime.totalMemory()
        val freeMemory = runtime.freeMemory()
        val usedMemory = totalMemory - freeMemory
        val maxMemory = runtime.maxMemory()

        return DetailedMemoryInfo(
            totalMemoryMB = totalMemory / (1024 * 1024),
            usedMemoryMB = usedMemory / (1024 * 1024),
            freeMemoryMB = freeMemory / (1024 * 1024),
            maxMemoryMB = maxMemory / (1024 * 1024),
            memoryUsagePercent = (usedMemory.toFloat() / maxMemory.toFloat()) * 100f
        )
    }

    /**
     * Force le garbage collection et mesure l'impact
     */
    fun forceGarbageCollection(): GarbageCollectionResult {
        val beforeMemory = getCurrentMemoryUsage()
        val startTime = SystemClock.elapsedRealtime()

        System.gc()

        // Attendre un peu pour que le GC se termine
        Thread.sleep(100)

        val afterMemory = getCurrentMemoryUsage()
        val duration = SystemClock.elapsedRealtime() - startTime
        val memoryFreed = beforeMemory - afterMemory

        return GarbageCollectionResult(
            memoryFreedMB = memoryFreed,
            durationMs = duration,
            beforeMemoryMB = beforeMemory,
            afterMemoryMB = afterMemory
        )
    }

    /**
     * Enregistre une mesure de performance
     */
    fun recordMeasurement(measurement: PerformanceMeasurement) {
        performanceCache[measurement.operationName] = measurement

        // Mettre à jour les métriques globales
        performanceScope.launch {
            updateGlobalMetrics(measurement)
        }

        // Nettoyer le cache si trop plein
        if (performanceCache.size > 100) {
            cleanPerformanceCache()
        }
    }

    /**
     * Met à jour les métriques globales
     */
    private suspend fun updateGlobalMetrics(measurement: PerformanceMeasurement) {
        val currentMetrics = _performanceMetrics.value
        val newMetrics = currentMetrics.copy(
            totalOperations = currentMetrics.totalOperations + 1,
            successfulOperations = currentMetrics.successfulOperations + if (measurement.isSuccessful) 1 else 0,
            averageExecutionTimeMs = calculateNewAverage(
                currentMetrics.averageExecutionTimeMs,
                measurement.executionTimeMs,
                currentMetrics.totalOperations + 1
            ),
            totalMemoryUsedMB = currentMetrics.totalMemoryUsedMB + measurement.memoryUsedMB,
            slowestOperationMs = maxOf(currentMetrics.slowestOperationMs, measurement.executionTimeMs),
            lastUpdateTimestamp = System.currentTimeMillis()
        )

        _performanceMetrics.value = newMetrics
    }

    /**
     * Calcule une nouvelle moyenne
     */
    private fun calculateNewAverage(currentAvg: Long, newValue: Long, totalCount: Long): Long {
        return ((currentAvg * (totalCount - 1)) + newValue) / totalCount
    }

    /**
     * Nettoie le cache des anciennes mesures
     */
    private fun cleanPerformanceCache() {
        val currentTime = System.currentTimeMillis()
        val cutoffTime = currentTime - (5 * 60 * 1000) // 5 minutes

        performanceCache.entries.removeAll { entry ->
            entry.value.timestamp < cutoffTime
        }
    }

    /**
     * Log une mesure de performance
     */
    fun logPerformanceMeasurement(measurement: PerformanceMeasurement) {
        val logLevel = when {
            measurement.executionTimeMs > PERFORMANCE_THRESHOLD_MS -> Log.WARN
            !measurement.isSuccessful -> Log.ERROR
            else -> Log.DEBUG
        }

        val message = buildString {
            append("Performance [${measurement.operationName}]: ")
            append("${measurement.executionTimeMs}ms, ")
            append("${measurement.memoryUsedMB}MB")
            if (!measurement.isSuccessful) {
                append(", Erreur: ${measurement.error}")
            }
        }

        Log.println(logLevel, TAG, message)
    }

    /**
     * Obtient les statistiques de performance pour une opération
     */
    fun getOperationStats(operationName: String): PerformanceMeasurement? {
        return performanceCache[operationName]
    }

    /**
     * Obtient toutes les mesures de performance
     */
    fun getAllMeasurements(): Map<String, PerformanceMeasurement> {
        return performanceCache.toMap()
    }

    /**
     * Réinitialise toutes les métriques
     */
    fun resetMetrics() {
        performanceCache.clear()
        _performanceMetrics.value = PerformanceMetrics()
    }
}

/**
 * Classe pour profiler des opérations longues
 */
class PerformanceProfiler(private val operationName: String) {
    private var startTime: Long = 0
    private var startMemory: Long = 0
    private val checkpoints = mutableListOf<ProfilerCheckpoint>()

    fun start() {
        startTime = SystemClock.elapsedRealtime()
        startMemory = PerformanceUtils.getCurrentMemoryUsage()
    }

    fun checkpoint(name: String) {
        val currentTime = SystemClock.elapsedRealtime()
        val currentMemory = PerformanceUtils.getCurrentMemoryUsage()

        checkpoints.add(
            ProfilerCheckpoint(
                name = name,
                timeFromStartMs = currentTime - startTime,
                memoryFromStartMB = currentMemory - startMemory,
                timestamp = System.currentTimeMillis()
            )
        )
    }

    fun finish(): ProfilerResult {
        val endTime = SystemClock.elapsedRealtime()
        val endMemory = PerformanceUtils.getCurrentMemoryUsage()

        return ProfilerResult(
            operationName = operationName,
            totalTimeMs = endTime - startTime,
            totalMemoryMB = endMemory - startMemory,
            checkpoints = checkpoints.toList()
        )
    }
}

/**
 * Résultat d'une mesure de performance
 */
sealed class PerformanceResult<T> {
    data class Success<T>(val result: T, val measurement: PerformanceMeasurement) : PerformanceResult<T>()
    data class Error<T>(val exception: Throwable, val measurement: PerformanceMeasurement) : PerformanceResult<T>()
}

/**
 * Mesure de performance individuelle
 */
data class PerformanceMeasurement(
    val operationName: String,
    val executionTimeMs: Long,
    val memoryUsedMB: Long,
    val timestamp: Long,
    val isSuccessful: Boolean,
    val error: String? = null
)

/**
 * Métriques globales de performance
 */
data class PerformanceMetrics(
    val totalOperations: Long = 0,
    val successfulOperations: Long = 0,
    val averageExecutionTimeMs: Long = 0,
    val totalMemoryUsedMB: Long = 0,
    val slowestOperationMs: Long = 0,
    val lastUpdateTimestamp: Long = 0
) {
    val successRate: Float get() = if (totalOperations > 0) {
        (successfulOperations.toFloat() / totalOperations.toFloat()) * 100f
    } else 0f
}

/**
 * Informations détaillées sur la mémoire
 */
data class DetailedMemoryInfo(
    val totalMemoryMB: Long,
    val usedMemoryMB: Long,
    val freeMemoryMB: Long,
    val maxMemoryMB: Long,
    val memoryUsagePercent: Float
)

/**
 * Résultat du garbage collection
 */
data class GarbageCollectionResult(
    val memoryFreedMB: Long,
    val durationMs: Long,
    val beforeMemoryMB: Long,
    val afterMemoryMB: Long
)

/**
 * Point de contrôle du profiler
 */
data class ProfilerCheckpoint(
    val name: String,
    val timeFromStartMs: Long,
    val memoryFromStartMB: Long,
    val timestamp: Long
)

/**
 * Résultat du profiler
 */
data class ProfilerResult(
    val operationName: String,
    val totalTimeMs: Long,
    val totalMemoryMB: Long,
    val checkpoints: List<ProfilerCheckpoint>
)
