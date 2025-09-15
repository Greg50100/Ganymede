package com.joviansapps.ganymede.ui.screens.utilities.electronics

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
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
import kotlin.math.sqrt

// --- 1. State and Data ---
// American Wire Gauge data: Map<AWG, Diameter in mm>
val awgData = mapOf(
    0 to 8.25, 1 to 7.35, 2 to 6.54, 3 to 5.83, 4 to 5.19, 5 to 4.62, 6 to 4.11, 7 to 3.66, 8 to 3.26, 9 to 2.91,
    10 to 2.59, 11 to 2.30, 12 to 2.05, 13 to 1.83, 14 to 1.63, 15 to 1.45, 16 to 1.29, 17 to 1.15, 18 to 1.02,
    19 to 0.912, 20 to 0.812, 21 to 0.723, 22 to 0.644, 23 to 0.573, 24 to 0.511
)
// Standard metric wire sizes in mm²
val metricData = listOf(0.5, 0.75, 1.0, 1.5, 2.5, 4.0, 6.0, 10.0, 16.0, 25.0)

enum class WireStandard { Metric, AWG }
private const val COPPER_RESISTIVITY_WG = 1.68e-8

data class WireGaugeUiState(
    val sourceVoltage: String = "12",
    val current: String = "5",
    val wireLength: String = "10",
    val maxDropPercentage: String = "3",
    val standard: WireStandard = WireStandard.Metric,
    val resultText: String? = null
)

// --- 2. ViewModel ---
class WireGaugeViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(WireGaugeUiState())
    val uiState = _uiState.asStateFlow()

    init {
        calculate()
    }

    fun onSourceVoltageChange(value: String) { _uiState.update { it.copy(sourceVoltage = value) }; calculate() }
    fun onCurrentChange(value: String) { _uiState.update { it.copy(current = value) }; calculate() }
    fun onWireLengthChange(value: String) { _uiState.update { it.copy(wireLength = value) }; calculate() }
    fun onMaxDropPercentageChange(value: String) { _uiState.update { it.copy(maxDropPercentage = value) }; calculate() }
    fun onStandardChange(standard: WireStandard) { _uiState.update { it.copy(standard = standard) }; calculate() }

    private fun calculate() {
        viewModelScope.launch {
            val state = _uiState.value
            val v = state.sourceVoltage.toDoubleOrNull()
            val i = state.current.toDoubleOrNull()
            val l = state.wireLength.toDoubleOrNull()
            val dropPercent = state.maxDropPercentage.toDoubleOrNull()

            if (v == null || i == null || l == null || dropPercent == null || v <= 0 || i <= 0 || l <= 0 || dropPercent <= 0) {
                _uiState.update { it.copy(resultText = null) }
                return@launch
            }

            val maxVoltageDrop = v * (dropPercent / 100.0)
            val requiredResistance = maxVoltageDrop / i
            val requiredAreaM2 = (COPPER_RESISTIVITY_WG * l) / requiredResistance

            val result: String? = when (state.standard) {
                WireStandard.AWG -> {
                    val requiredDiameterMm = 2 * sqrt(requiredAreaM2 / PI) * 1000
                    val suitableGauge = awgData.entries
                        .filter { it.value >= requiredDiameterMm }
                        .minByOrNull { it.value }
                    suitableGauge?.let { "AWG ${it.key} (${it.value} mm)" }
                }
                WireStandard.Metric -> {
                    val requiredAreaMm2 = requiredAreaM2 * 1_000_000
                    val suitableMetricSize = metricData.firstOrNull { it >= requiredAreaMm2 }
                    suitableMetricSize?.let { "$it mm²" }
                }
            }
            _uiState.update { it.copy(resultText = result) }
        }
    }
}

// --- 3. Composable Screen ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WireGaugeCalculatorScreen(viewModel: WireGaugeViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(stringResource(R.string.wire_gauge_calculator_title), style = MaterialTheme.typography.headlineSmall)

        // --- Standard Selector ---
        SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
            WireStandard.values().forEach { standard ->
                SegmentedButton(
                    selected = uiState.standard == standard,
                    onClick = { viewModel.onStandardChange(standard) },
                    // SegmentedButtonDefaults.itemShape expects (index, count)
                    shape = SegmentedButtonDefaults.itemShape(standard.ordinal, WireStandard.values().size)
                ) {
                    Text(standard.name)
                }
            }
        }

        // --- Input Fields ---
        OutlinedTextField(value = uiState.sourceVoltage, onValueChange = viewModel::onSourceVoltageChange, label = { Text(stringResource(R.string.source_voltage_v)) }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = uiState.current, onValueChange = viewModel::onCurrentChange, label = { Text(stringResource(R.string.current_a)) }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = uiState.wireLength, onValueChange = viewModel::onWireLengthChange, label = { Text(stringResource(R.string.wire_length_m)) }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = uiState.maxDropPercentage, onValueChange = viewModel::onMaxDropPercentageChange, label = { Text(stringResource(R.string.max_voltage_drop_percent)) }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth())

        // --- Result ---
        uiState.resultText?.let { result ->
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(stringResource(R.string.recommended_wire_gauge), style = MaterialTheme.typography.titleLarge)
                    Text(
                        text = result,
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}
