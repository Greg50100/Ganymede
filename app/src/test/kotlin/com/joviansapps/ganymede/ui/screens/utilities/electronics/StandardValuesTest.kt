package com.joviansapps.ganymede.ui.screens.utilities.electronics

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class StandardValuesTest {
    @Test
    fun `compute nearest standard value for 4000 in E24 returns 3900`() {
        val result = computeNearestStandardValue(4000.0, ESeries.E24)
        assertEquals(3900.0, result ?: Double.NaN, 0.0)
    }

    @Test
    fun `compute nearest standard value for small value`() {
        val result = computeNearestStandardValue(0.002, ESeries.E12)
        // Expect 0.0018 (1.8 * 0.001) because tie-breaker chooses the earlier value 1.8 in the series
        assertEquals(0.0018, result ?: Double.NaN, 1e-12)
    }

    @Test
    fun `compute nearest returns null for non positive`() {
        assertNull(computeNearestStandardValue(0.0, ESeries.E24))
        assertNull(computeNearestStandardValue(-10.0, ESeries.E48))
    }
}

