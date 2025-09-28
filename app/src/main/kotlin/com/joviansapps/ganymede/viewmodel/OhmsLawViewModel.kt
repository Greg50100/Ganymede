package com.joviansapps.ganymede.viewmodel

import androidx.lifecycle.ViewModel
import com.joviansapps.ganymede.ui.screens.utilities.common.FormFieldState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class OhmsLawState(
    val voltage: FormFieldState = FormFieldState(unit = "V"),
    val current: FormFieldState = FormFieldState(unit = "A"),
    val resistance: FormFieldState = FormFieldState(unit = "Ω"),
    val results: List<Pair<String, String>> = emptyList()
)

class OhmsLawViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(OhmsLawState())
    val uiState = _uiState.asStateFlow()

    fun onVoltageChange(newValue: String) {
        val validatedState = validateField(newValue)
        _uiState.update { it.copy(voltage = it.voltage.copy(value = newValue, isError = validatedState.isError, errorMessage = validatedState.errorMessage), results = emptyList()) }
    }

    fun onCurrentChange(newValue: String) {
        val validatedState = validateField(newValue)
        _uiState.update { it.copy(current = it.current.copy(value = newValue, isError = validatedState.isError, errorMessage = validatedState.errorMessage), results = emptyList()) }
    }

    fun onResistanceChange(newValue: String) {
        val validatedState = validateField(newValue)
        _uiState.update { it.copy(resistance = it.resistance.copy(value = newValue, isError = validatedState.isError, errorMessage = validatedState.errorMessage), results = emptyList()) }
    }

    private fun validateField(value: String): FormFieldState {
        if (value.isEmpty() || value.toDoubleOrNull() != null || value == "." || value == "-") {
            return FormFieldState(value = value, isError = false)
        }
        return FormFieldState(value = value, isError = true, errorMessage = "Invalid number")
    }

    fun calculate() {
        val state = _uiState.value
        val v = state.voltage.value.toDoubleOrNull()
        val i = state.current.value.toDoubleOrNull()
        val r = state.resistance.value.toDoubleOrNull()

        val filledCount = listOfNotNull(v, i, r).size
        if (filledCount != 2) {
            // Not exactly two fields are filled, do nothing.
            return
        }

        val newResults = mutableListOf<Pair<String, String>>()

        try {
            when {
                // Calculate Voltage
                v == null -> {
                    val result = i!! * r!!
                    newResults.add("Voltage (V)" to "%.4f".format(result))
                }
                // Calculate Current
                i == null -> {
                    if (r == 0.0) throw ArithmeticException("Resistance cannot be zero.")
                    // This was the line with the bug, it is now corrected.
                    val result = v!! / r!!
                    newResults.add("Current (A)" to "%.4f".format(result))
                }
                // Calculate Resistance
                r == null -> {
                    if (i == 0.0) throw ArithmeticException("Current cannot be zero.")
                    val result = v!! / i!!
                    newResults.add("Resistance (Ω)" to "%.4f".format(result))
                }
            }
            _uiState.update { it.copy(results = newResults) }
        } catch (e: Exception) {
            // Handle division by zero or other calculation errors
            _uiState.update { it.copy(results = emptyList()) }
        }
    }

    fun reset() {
        _uiState.value = OhmsLawState()
    }
}

