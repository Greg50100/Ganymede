package com.joviansapps.ganymede.viewmodel

import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.joviansapps.ganymede.data.conversion.ConversionRepository
import com.joviansapps.ganymede.data.conversion.UnitCategory
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.*

data class ConverterUiState(
    val category: UnitCategory = UnitCategory.LENGTH,
    val unitFrom: String = ConversionRepository.units(UnitCategory.LENGTH).first().id,
    val unitTo: String = ConversionRepository.units(UnitCategory.LENGTH).getOrElse(1) { ConversionRepository.units(UnitCategory.LENGTH).first() }.id
)

class ConverterViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(ConverterUiState())
    val uiState: StateFlow<ConverterUiState> = _uiState.asStateFlow()

    // Map unitId -> TextFieldValue maintained in ViewModel to preserve cursor when UI recomposes
    private val _values = MutableStateFlow<Map<String, TextFieldValue>>(emptyMap())
    val values: StateFlow<Map<String, TextFieldValue>> = _values.asStateFlow()

    // last edited unit id
    private val _lastEdited = MutableStateFlow<String?>(null)
    val lastEdited: StateFlow<String?> = _lastEdited.asStateFlow()

    private var convertJob: Job? = null

    // Currency rates stub (base: EUR)
    private var currencyRates: Map<String, Double> = mapOf("EUR" to 1.0, "USD" to 1.08, "GBP" to 0.85)

    init {
        // initialize values for default category
        resetValuesForCategory(_uiState.value.category)
    }

    private fun resetValuesForCategory(category: UnitCategory) {
        val units = ConversionRepository.units(category)
        val map = units.associate { it.id to TextFieldValue("") }
        _values.value = map
    }

    fun onCategoryChange(category: UnitCategory) {
        val units = ConversionRepository.units(category)
        val defaultFrom = units.firstOrNull()?.id ?: ""
        val defaultTo = units.getOrNull(1)?.id ?: defaultFrom
        _uiState.value = _uiState.value.copy(category = category, unitFrom = defaultFrom, unitTo = defaultTo)
        resetValuesForCategory(category)
    }

    fun onUnitTextChanged(unitId: String, newValue: TextFieldValue) {
        // update immediate value and schedule conversion (debounce)
        _values.value = _values.value.toMutableMap().apply { put(unitId, newValue) }
        _lastEdited.value = unitId

        // cancel previous scheduled conversion
        convertJob?.cancel()
        convertJob = viewModelScope.launch {
            // small debounce to wait for typing to settle
            delay(200)
            performConversionFrom(unitId, newValue.text)
        }
    }

    private fun performConversionFrom(unitId: String, text: String) {
        val category = _uiState.value.category
        val units = ConversionRepository.units(category)
        val source = units.find { it.id == unitId } ?: return

        // Special case: numeric base category is handled as textual digits; conversion utilities expect a Double representing the digit sequence
        if (category == UnitCategory.NUMERIC_BASE) {
            val asNum = text.toDoubleOrNull()
            if (asNum == null) {
                // invalid digits yet -> keep only this field updated
                return
            }
            val num = asNum
            val newMap = units.associate { target ->
                val conv = try { source.convert(target, num) } catch (e: Exception) { Double.NaN }
                val shown = if (target.id == unitId) text else formatNumber(conv)
                target.id to TextFieldValue(shown)
            }
            _values.value = newMap
            return
        }

        // For currency use currencyRates to adjust base conversion if FactorUnit used
        // We'll attempt to parse as double
        val parsed = text.toDoubleOrNull()
        if (parsed == null) {
            // partial input (e.g. "1.") - do not convert other fields, only update this one
            return
        }
        val num = parsed

        // compute conversions in background (already in coroutine)
        val updated = units.associate { target ->
            val conv = try {
                // If currency, apply rates using currencyRates map
                if (category == UnitCategory.CURRENCY) {
                    val fromRate = currencyRates[source.id] ?: 1.0
                    val toRate = currencyRates[target.id] ?: 1.0
                    // convert num (in source currency) to base(EUR) then to target
                    val inBase = num / fromRate
                    inBase * toRate
                } else {
                    source.convert(target, num)
                }
            } catch (e: Exception) {
                Double.NaN
            }
            val shown = if (target.id == unitId) text else formatNumber(conv)
            target.id to TextFieldValue(shown)
        }

        _values.value = updated
    }

    fun refreshCurrencyRates() {
        // placeholder: in real app fetch from network; here we simulate an async update
        viewModelScope.launch {
            // simulate network delay
            delay(500)
            // replace with fetched rates
            currencyRates = mapOf("EUR" to 1.0, "USD" to 1.10, "GBP" to 0.88)
            // when rates update, re-run conversion from last edited to reflect new rates
            _lastEdited.value?.let { id ->
                val text = _values.value[id]?.text ?: ""
                performConversionFrom(id, text)
            }
        }
    }

    private fun formatNumber(v: Double): String {
        if (v.isNaN() || v.isInfinite()) return ""
        val symbols = DecimalFormatSymbols.getInstance(Locale.getDefault())
        val df = DecimalFormat("#,##0.######", symbols)
        return df.format(v)
    }
}