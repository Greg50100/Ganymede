package com.joviansapps.ganymede.ui.screens.utilities.electronics

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.joviansapps.ganymede.R
import com.joviansapps.ganymede.ui.components.ResultField
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.DecimalFormat
import kotlin.math.PI
import kotlin.math.sqrt

enum class PassiveFilterType { LOW_PASS, HIGH_PASS }
enum class PassiveFilterComponentType { RC, RL, LC }

data class PassiveFilterUiState(
    val filterType: PassiveFilterType = PassiveFilterType.LOW_PASS,
    val componentType: PassiveFilterComponentType = PassiveFilterComponentType.RC,
    val resistance: String = "1k",
    val capacitance: String = "100n",
    val inductance: String = "10m",
    val cutoffFrequency: Double? = null
)

class PassiveFilterViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(PassiveFilterUiState())
    val uiState = _uiState.asStateFlow()

    init { calculate() }

    fun onValueChange(field: String, value: String) {
        val s = _uiState.value
        val newState = when (field) {
            "r" -> PassiveFilterUiState(s.filterType, s.componentType, value, s.capacitance, s.inductance, s.cutoffFrequency)
            "c" -> PassiveFilterUiState(s.filterType, s.componentType, s.resistance, value, s.inductance, s.cutoffFrequency)
            "l" -> PassiveFilterUiState(s.filterType, s.componentType, s.resistance, s.capacitance, value, s.cutoffFrequency)
            else -> s
        }
        _uiState.value = newState
        calculate()
    }

    fun onFilterTypeChange(type: PassiveFilterType) {
        val s = _uiState.value
        _uiState.value = PassiveFilterUiState(type, s.componentType, s.resistance, s.capacitance, s.inductance, s.cutoffFrequency)
        calculate()
    }

    fun onComponentTypeChange(type: PassiveFilterComponentType) {
        val s = _uiState.value
        _uiState.value = PassiveFilterUiState(s.filterType, type, s.resistance, s.capacitance, s.inductance, s.cutoffFrequency)
        calculate()
    }

    private fun parseValue(str: String): Double? {
        val valueString = str.lowercase().trim()
        if (valueString.isEmpty()) return null
        var multiplier = 1.0
        val lastChar = valueString.last()
        if (!lastChar.isDigit()) {
            when (lastChar) {
                'p' -> multiplier = 1e-12
                'n' -> multiplier = 1e-9
                'u', 'µ' -> multiplier = 1e-6
                'm' -> multiplier = 1e-3
                'k' -> multiplier = 1e3
                'g' -> multiplier = 1e9
            }
            return valueString.dropLast(1).toDoubleOrNull()?.times(multiplier)
        }
        return valueString.toDoubleOrNull()
    }

    private fun calculate() {
        viewModelScope.launch {
            val s = _uiState.value
            val r = parseValue(s.resistance)
            val c = parseValue(s.capacitance)
            val l = parseValue(s.inductance)
            var fc: Double? = null

            when (s.componentType) {
                PassiveFilterComponentType.RC -> {
                    if (r != null && c != null && r > 0 && c > 0) {
                        fc = 1 / (2 * PI * r * c)
                    }
                }
                PassiveFilterComponentType.RL -> {
                    if (r != null && l != null && r > 0 && l > 0) {
                        fc = r / (2 * PI * l)
                    }
                }
                PassiveFilterComponentType.LC -> {
                    if (l != null && c != null && l > 0 && c > 0) {
                        fc = 1 / (2 * PI * sqrt(l * c))
                    }
                }
            }
            _uiState.value = PassiveFilterUiState(s.filterType, s.componentType, s.resistance, s.capacitance, s.inductance, fc)
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PassiveFilterCalculatorScreen(viewModel: PassiveFilterViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val formatter = remember { DecimalFormat("#.###") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(stringResource(id = R.string.passive_filter_calculator_title), style = MaterialTheme.typography.headlineSmall)

        SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
            SegmentedButton(selected = uiState.filterType == PassiveFilterType.LOW_PASS, onClick = { viewModel.onFilterTypeChange(PassiveFilterType.LOW_PASS) }, shape = SegmentedButtonDefaults.itemShape(0, 2)) { Text("Passe-Bas") }
            SegmentedButton(selected = uiState.filterType == PassiveFilterType.HIGH_PASS, onClick = { viewModel.onFilterTypeChange(PassiveFilterType.HIGH_PASS) }, shape = SegmentedButtonDefaults.itemShape(1, 2)) { Text("Passe-Haut") }
        }

        SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
            SegmentedButton(selected = uiState.componentType == PassiveFilterComponentType.RC, onClick = { viewModel.onComponentTypeChange(PassiveFilterComponentType.RC) }, shape = SegmentedButtonDefaults.itemShape(0, 3)) { Text("RC") }
            SegmentedButton(selected = uiState.componentType == PassiveFilterComponentType.RL, onClick = { viewModel.onComponentTypeChange(PassiveFilterComponentType.RL) }, shape = SegmentedButtonDefaults.itemShape(1, 3)) { Text("RL") }
            SegmentedButton(selected = uiState.componentType == PassiveFilterComponentType.LC, onClick = { viewModel.onComponentTypeChange(PassiveFilterComponentType.LC) }, shape = SegmentedButtonDefaults.itemShape(2, 3)) { Text("LC") }
        }

        if (uiState.componentType in listOf(PassiveFilterComponentType.RC, PassiveFilterComponentType.RL)) {
            OutlinedTextField(value = uiState.resistance, onValueChange = { viewModel.onValueChange("r", it) }, label = { Text("Résistance (Ω)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text), modifier = Modifier.fillMaxWidth())
        }
        if (uiState.componentType in listOf(PassiveFilterComponentType.RC, PassiveFilterComponentType.LC)) {
            OutlinedTextField(value = uiState.capacitance, onValueChange = { viewModel.onValueChange("c", it) }, label = { Text("Capacité (F)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text), modifier = Modifier.fillMaxWidth())
        }
        if (uiState.componentType in listOf(PassiveFilterComponentType.RL, PassiveFilterComponentType.LC)) {
            OutlinedTextField(value = uiState.inductance, onValueChange = { viewModel.onValueChange("l", it) }, label = { Text("Inductance (H)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text), modifier = Modifier.fillMaxWidth())
        }

        if (uiState.cutoffFrequency != null) {
            val label = if (uiState.componentType == PassiveFilterComponentType.LC) "Fréquence de Résonance (f₀)" else "Fréquence de Coupure (fc)"
            ResultField(
                label = label,
                value = uiState.cutoffFrequency?.let { formatter.format(it) } ?: "N/A",
                unit = "Hz"
            )
        }
    }
}
