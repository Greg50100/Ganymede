package com.joviansapps.ganymede.ui.screens.utilities.electronics

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.joviansapps.ganymede.R
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.math.PI

// --- 1. State and Enums ---
enum class FilterCircuitType { RC, RL }
enum class FilterPassType { LowPass, HighPass }

data class FilterUiState(
    val resistance: String = "1000",
    val capacitance: String = "0.1", // in µF for RC
    val inductance: String = "10", // in mH for RL
    val frequency: String = "",
    val circuitType: FilterCircuitType = FilterCircuitType.RC,
    val passType: FilterPassType = FilterPassType.LowPass
)

// --- 2. ViewModel ---
class FilterViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(FilterUiState())
    val uiState = _uiState.asStateFlow()

    init {
        calculate()
    }

    fun onResistanceChange(value: String) { _uiState.update { it.copy(resistance = value) }; calculate() }
    fun onCapacitanceChange(value: String) { _uiState.update { it.copy(capacitance = value) }; calculate() }
    fun onInductanceChange(value: String) { _uiState.update { it.copy(inductance = value) }; calculate() }
    fun onFrequencyChange(value: String) { _uiState.update { it.copy(frequency = value) }; calculate() }

    fun onCircuitTypeChange(type: FilterCircuitType) {
        _uiState.update { it.copy(circuitType = type, frequency = "") } // Reset frequency on type change
        calculate()
    }

    fun onPassTypeChange(type: FilterPassType) {
        _uiState.update { it.copy(passType = type) }
    }

    private fun calculate() {
        viewModelScope.launch {
            val state = _uiState.value
            val r = state.resistance.toDoubleOrNull()

            when (state.circuitType) {
                FilterCircuitType.RC -> {
                    val c = state.capacitance.toDoubleOrNull()?.times(1e-6) // Convert µF to F
                    val fRes = if (r != null && c != null && r > 0 && c > 0) 1 / (2 * PI * r * c) else null
                    _uiState.update { it.copy(frequency = fRes?.let { "%.2f".format(it) } ?: "") }
                }
                FilterCircuitType.RL -> {
                    val l = state.inductance.toDoubleOrNull()?.times(1e-3) // Convert mH to H
                    val fRes = if (r != null && l != null && r > 0 && l > 0) r / (2 * PI * l) else null
                    _uiState.update { it.copy(frequency = fRes?.let { "%.2f".format(it) } ?: "") }
                }
            }
        }
    }
}

// --- 3. Composable Screen ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterCalculatorScreen(viewModel: FilterViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(stringResource(R.string.filter_calculator_title), style = MaterialTheme.typography.headlineSmall)

        // --- Type Selectors ---
        SegmentedButtonRow(
            label = stringResource(R.string.circuit_type),
            options = FilterCircuitType.values().map { it.name },
            selectedIndex = uiState.circuitType.ordinal,
            onSelected = { viewModel.onCircuitTypeChange(FilterCircuitType.values()[it]) }
        )

        SegmentedButtonRow(
            label = stringResource(R.string.filter_type),
            options = listOf(stringResource(R.string.low_pass), stringResource(R.string.high_pass)),
            selectedIndex = uiState.passType.ordinal,
            onSelected = { viewModel.onPassTypeChange(FilterPassType.values()[it]) }
        )

        // --- Circuit Diagram ---
        val circuitDrawable = when (uiState.circuitType) {
            FilterCircuitType.RC -> if (uiState.passType == FilterPassType.LowPass) R.drawable.ic_rc_low_pass_filter else R.drawable.ic_rc_high_pass_filter
            FilterCircuitType.RL -> if (uiState.passType == FilterPassType.LowPass) R.drawable.ic_rl_low_pass_filter else R.drawable.ic_rl_high_pass_filter
        }
        Icon(
            painter = painterResource(id = circuitDrawable),
            contentDescription = "Filter Circuit Diagram",
            modifier = Modifier.fillMaxWidth().height(100.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )

        // --- Input Fields ---
        OutlinedTextField(value = uiState.resistance, onValueChange = viewModel::onResistanceChange, label = { Text(stringResource(R.string.resistance_ohm)) }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth())

        if (uiState.circuitType == FilterCircuitType.RC) {
            OutlinedTextField(value = uiState.capacitance, onValueChange = viewModel::onCapacitanceChange, label = { Text(stringResource(R.string.capacitance_uf)) }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth())
        } else {
            OutlinedTextField(value = uiState.inductance, onValueChange = viewModel::onInductanceChange, label = { Text(stringResource(R.string.inductance_mh)) }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth())
        }

        // --- Result Field ---
        OutlinedTextField(
            value = uiState.frequency,
            onValueChange = { /* Read-only or handled by ViewModel if needed */ },
            label = { Text(stringResource(R.string.cutoff_frequency_hz)) },
            readOnly = true,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun SegmentedButtonRow(
    label: String,
    options: List<String>,
    selectedIndex: Int,
    onSelected: (Int) -> Unit
) {
    Column {
        Text(label, style = MaterialTheme.typography.labelMedium)
        Spacer(Modifier.height(4.dp))
        SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
            options.forEachIndexed { index, optionLabel ->
                SegmentedButton(
                    selected = index == selectedIndex,
                    onClick = { onSelected(index) },
                    shape = MaterialTheme.shapes.medium
                ) {
                    Text(optionLabel)
                }
            }
        }
    }
}
