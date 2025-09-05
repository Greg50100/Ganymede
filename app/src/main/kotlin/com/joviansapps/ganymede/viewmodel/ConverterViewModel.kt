package com.joviansapps.ganymede.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class ConverterUiState(
    val input: String = "",
    val output: String = "",
    val unitFrom: String = "m",
    val unitTo: String = "cm"
)

class ConverterViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(ConverterUiState())
    val uiState: StateFlow<ConverterUiState> = _uiState

    fun onInputChange(value: String) {
        _uiState.value = _uiState.value.copy(input = value)
        convert()
    }

    fun onUnitChange(from: String, to: String) {
        _uiState.value = _uiState.value.copy(unitFrom = from, unitTo = to)
        convert()
    }

    private fun convert() {
        val inputVal = _uiState.value.input.toDoubleOrNull() ?: 0.0
        val result = when (_uiState.value.unitFrom to _uiState.value.unitTo) {
            "m" to "cm" -> inputVal * 100
            "cm" to "m" -> inputVal / 100
            else -> inputVal
        }
        _uiState.value = _uiState.value.copy(output = result.toString())
    }
}