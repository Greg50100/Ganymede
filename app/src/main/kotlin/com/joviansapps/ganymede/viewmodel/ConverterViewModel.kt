package com.joviansapps.ganymede.viewmodel

import androidx.lifecycle.ViewModel
import com.joviansapps.ganymede.data.conversion.ConversionRepository
import com.joviansapps.ganymede.data.conversion.UnitCategory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class ConverterUiState(
    val input: String = "",
    val output: String = "",
    val category: UnitCategory = UnitCategory.LENGTH,
    val unitFrom: String = ConversionRepository.units(UnitCategory.LENGTH).first().id,
    val unitTo: String = ConversionRepository.units(UnitCategory.LENGTH).getOrElse(1) { ConversionRepository.units(UnitCategory.LENGTH).first() }.id
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

    fun onCategoryChange(category: UnitCategory) {
        val units = ConversionRepository.units(category)
        val defaultFrom = units.first().id
        val defaultTo = units.getOrElse(1) { units.first() }.id
        _uiState.value = _uiState.value.copy(category = category, unitFrom = defaultFrom, unitTo = defaultTo)
        convert()
    }

    private fun convert() {
        val state = _uiState.value
        val inputVal = state.input.toDoubleOrNull() ?: 0.0
        val units = ConversionRepository.units(state.category)
        val fromUnit = units.find { it.id == state.unitFrom } ?: units.first()
        val toUnit = units.find { it.id == state.unitTo } ?: units.first()
        val base = fromUnit.convertFrom(inputVal)
        val resultVal = toUnit.convertTo(base)
        _uiState.value = state.copy(output = formatNumber(resultVal))
    }

    private fun formatNumber(v: Double): String {
        return if (v == v.toLong().toDouble()) v.toLong().toString() else v.toString()
    }
}