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
import com.joviansapps.ganymede.ui.components.ResultField
import com.joviansapps.ganymede.ui.components.NumericTextField
import com.joviansapps.ganymede.ui.components.formatDouble
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.math.pow

// --- 1. State and Enums ---
private const val COPPER_RESISTIVITY = 1.68e-8
private const val ALUMINUM_RESISTIVITY = 2.82e-8

enum class WireMaterial(val resistivity: Double) {
    Copper(COPPER_RESISTIVITY),
    Aluminum(ALUMINUM_RESISTIVITY)
}

data class VoltageDropUiState(
    val sourceVoltage: String = "12",
    val current: String = "1",
    val wireLength: String = "10",
    val wireDiameter: String = "2.05", // AWG 12 in mm
    val material: WireMaterial = WireMaterial.Copper,
    val voltageDrop: Double? = null,
    val voltageDropPercentage: Double? = null,
    val endVoltage: Double? = null
)

// --- 2. ViewModel ---
class VoltageDropViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(VoltageDropUiState())
    val uiState = _uiState.asStateFlow()

    init {
        calculate()
    }

    fun onSourceVoltageChange(value: String) { _uiState.update { it.copy(sourceVoltage = value) }; calculate() }
    fun onCurrentChange(value: String) { _uiState.update { it.copy(current = value) }; calculate() }
    fun onWireLengthChange(value: String) { _uiState.update { it.copy(wireLength = value) }; calculate() }
    fun onWireDiameterChange(value: String) { _uiState.update { it.copy(wireDiameter = value) }; calculate() }
    fun onMaterialChange(material: WireMaterial) { _uiState.update { it.copy(material = material) }; calculate() }

    private fun calculate() {
        viewModelScope.launch {
            val state = _uiState.value
            val v = state.sourceVoltage.toDoubleOrNull()
            val i = state.current.toDoubleOrNull()
            val l = state.wireLength.toDoubleOrNull()
            val d = state.wireDiameter.toDoubleOrNull()

            if (v == null || i == null || l == null || d == null || v <= 0 || i <= 0 || l <= 0 || d <= 0) {
                _uiState.update { it.copy(voltageDrop = null, voltageDropPercentage = null, endVoltage = null) }
                return@launch
            }

            val area = Math.PI * (d / 2000).pow(2) // Convert mm diameter to m radius and calculate area
            val resistance = (state.material.resistivity * l) / area
            val drop = i * resistance
            val dropPercentage = (drop / v) * 100
            val endV = v - drop

            _uiState.update {
                it.copy(
                    voltageDrop = drop,
                    voltageDropPercentage = dropPercentage,
                    endVoltage = endV
                )
            }
        }
    }
}

// --- 3. Composable Screen ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VoltageDropCalculatorScreen(viewModel: VoltageDropViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(stringResource(R.string.voltage_drop_calculator_title), style = MaterialTheme.typography.headlineSmall)

        // --- Input Fields ---
        NumericTextField(value = uiState.sourceVoltage, onValueChange = viewModel::onSourceVoltageChange, label = stringResource(R.string.source_voltage_v))
        NumericTextField(value = uiState.current, onValueChange = viewModel::onCurrentChange, label = stringResource(R.string.current_a))
        NumericTextField(value = uiState.wireLength, onValueChange = viewModel::onWireLengthChange, label = stringResource(R.string.wire_length_m))
        NumericTextField(value = uiState.wireDiameter, onValueChange = viewModel::onWireDiameterChange, label = stringResource(R.string.wire_diameter_mm))

        // --- Material Selector ---
        SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
            WireMaterial.values().forEach { material ->
                SegmentedButton(
                    selected = uiState.material == material,
                    onClick = { viewModel.onMaterialChange(material) },
                    // SegmentedButtonDefaults.itemShape expects (index, count)
                    shape = SegmentedButtonDefaults.itemShape(material.ordinal, WireMaterial.values().size)
                ) {
                    Text(material.name)
                }
            }
        }

        // --- Results ---
        if (uiState.voltageDrop != null) {
            ResultField(
                label = stringResource(R.string.voltage_drop),
                value = formatDouble(uiState.voltageDrop, "#.##"),
                unit = "V"
            )
            ResultField(
                label = stringResource(R.string.voltage_drop_percent),
                value = formatDouble(uiState.voltageDropPercentage, "#.##"),
                unit = "%"
            )
            ResultField(
                label = stringResource(R.string.end_voltage),
                value = formatDouble(uiState.endVoltage, "#.##"),
                unit = "V"
            )
        }
    }
}
