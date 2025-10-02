package com.joviansapps.ganymede.utils

import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.junit.Assert.*

class ErrorManagerTest {

    @Test
    fun `should show calculation error`() = runTest {
        // Given
        val errorMessage = "Test error"

        // When
        ErrorManager.showError(ErrorState.CalculationError(errorMessage))

        // Then
        val currentError = ErrorManager.errorState.value
        assertTrue(currentError is ErrorState.CalculationError)
        // Utilise un safe-cast pour accéder à la propriété spécifique du sous-type
        assertEquals(errorMessage, (currentError as? ErrorState.CalculationError)?.message)
    }

    @Test
    fun `should clear error`() = runTest {
        // Given
        ErrorManager.showError(ErrorState.CalculationError("Test"))

        // When
        ErrorManager.clearError()

        // Then
        assertNull(ErrorManager.errorState.value)
    }

    @Test
    fun `safeCalculation should handle division by zero`() {
        // Given
        var errorCaptured: String? = null

        // When
        val result = safeCalculation(
            onError = { errorCaptured = it }
        ) {
            10 / 0
        }

        // Then
        assertNull(result)
        assertNotNull(errorCaptured)
        assertTrue(errorCaptured!!.contains("arithmétique"))
    }

    @Test
    fun `safeCalculation should return result on success`() {
        // When
        val result = safeCalculation {
            2 + 3
        }

        // Then
        assertEquals(5, result)
    }
}
