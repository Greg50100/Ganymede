package com.joviansapps.ganymede.data.cache

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * Cache LRU (Least Recently Used) thread-safe pour optimiser les calculs répétitifs
 */
class CalculationCache<K, V>(private val maxSize: Int = 100) {
    private val cache = LinkedHashMap<K, V>(maxSize, 0.75f, true)
    private val mutex = Mutex()

    suspend fun get(key: K): V? = mutex.withLock {
        cache[key]
    }

    suspend fun put(key: K, value: V): V? = mutex.withLock {
        val previous = cache.put(key, value)
        if (cache.size > maxSize) {
            val eldest = cache.entries.first()
            cache.remove(eldest.key)
        }
        previous
    }

    suspend fun getOrPut(key: K, defaultValue: suspend () -> V): V = mutex.withLock {
        cache[key] ?: run {
            val value = defaultValue()
            put(key, value)
            value
        }
    }

    suspend fun clear() = mutex.withLock {
        cache.clear()
    }

    suspend fun size(): Int = mutex.withLock {
        cache.size
    }
}

/**
 * Cache global pour les calculs mathématiques
 */
object MathCache {
    private val functionCache = CalculationCache<String, Double>(200)
    private val graphCache = CalculationCache<String, List<Pair<Double, Double>>>(50)

    suspend fun cacheFunction(expression: String, x: Double, result: Double) {
        functionCache.put("$expression@$x", result)
    }

    suspend fun getCachedFunction(expression: String, x: Double): Double? {
        return functionCache.get("$expression@$x")
    }

    suspend fun cacheGraph(expression: String, points: List<Pair<Double, Double>>) {
        graphCache.put(expression, points)
    }

    suspend fun getCachedGraph(expression: String): List<Pair<Double, Double>>? {
        return graphCache.get(expression)
    }

    suspend fun clearAll() {
        functionCache.clear()
        graphCache.clear()
    }
}
