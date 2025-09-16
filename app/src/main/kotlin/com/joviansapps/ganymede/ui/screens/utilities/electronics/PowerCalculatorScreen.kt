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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.DecimalFormat
import kotlin.math.acos
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sqrt

// --- State and ViewModel ---

enum class PowerCalcType { DC, AC_Single_Phase }

// --- Refactoring Step 1: Define a sealed class for input fields ---
// This replaces "magic strings" like "voltage", "current", etc.
sealed class PowerField {
    data object Voltage : PowerField()
    data object Current : PowerField()
    data object Resistance : PowerField()
    data object PowerFactor : PowerField()
}

data class PowerUiState(
    val calcType: PowerCalcType = PowerCalcType.DC,
    val voltage: String = "12",
    val current: String = "1.5",
    val resistance: String = "",
    val powerFactor: String = "0.85",
    val result: PowerResult? = null
)

data class PowerResult(
    val power: Double? = null, // Watts (Real Power)
    val resistance: Double? = null,
    val apparentPower: Double? = null, // VA
    val reactivePower: Double? = null, // VAR
    val phaseAngle: Double? = null // Degrees
)

class PowerViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(PowerUiState())
    val uiState = _uiState.asStateFlow()

    init {
        calculate()
    }

    // --- Refactoring Step 2: Update the event handler to use the sealed class ---
    // This provides type safety and prevents typos.
    fun onValueChange(field: PowerField, value: String) {
        _uiState.update {
            when (field) {
                is PowerField.Voltage -> it.copy(voltage = value)
                is PowerField.Current -> it.copy(current = value)
                is PowerField.Resistance -> it.copy(resistance = value)
                is PowerField.PowerFactor -> it.copy(powerFactor = value)
            }
        }
        calculate()
    }

    fun onTypeChange(type: PowerCalcType) {
        _uiState.update { it.copy(calcType = type) }
        calculate()
    }

    private fun calculate() {
        viewModelScope.launch {
            val state = _uiState.value
            val v = state.voltage.toDoubleOrNull()
            val i = state.current.toDoubleOrNull()
            val pf = state.powerFactor.toDoubleOrNull()

            if (v == null || i == null) {
                _uiState.update { it.copy(result = null) }
                return@launch
            }

            when (state.calcType) {
                PowerCalcType.DC -> {
                    val power = v * i
                    val resistance = v / i
                    _uiState.update { it.copy(result = PowerResult(power = power, resistance = resistance)) }
                }
                PowerCalcType.AC_Single_Phase -> {
                    if (pf == null || pf < 0 || pf > 1) {
                        _uiState.update { it.copy(result = null) }
                        return@launch
                    }
                    val apparentPower = v * i
                    val realPower = apparentPower * pf
                    val reactivePower = sqrt(apparentPower.pow(2) - realPower.pow(2))
                    val phaseAngle = Math.toDegrees(acos(pf))
                    _uiState.update {
                        it.copy(result = PowerResult(
                            power = realPower,
                            apparentPower = apparentPower,
                            reactivePower = reactivePower,
                            phaseAngle = phaseAngle
                        ))
                    }
                }
            }
        }
    }
}

// --- UI ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PowerCalculatorScreen(viewModel: PowerViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val formatter = DecimalFormat("#.###")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(stringResource(R.string.power_calculator_title), style = MaterialTheme.typography.headlineSmall)

        SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
            PowerCalcType.values().forEach { type ->
                SegmentedButton(
                    selected = uiState.calcType == type,
                    onClick = { viewModel.onTypeChange(type) },
                    shape = SegmentedButtonDefaults.itemShape(type.ordinal, PowerCalcType.values().size)
                ) {
                    Text(if (type == PowerCalcType.DC) "DC" else stringResource(id = R.string.ac_power_single_phase))
                }
            }
        }

        // --- Refactoring Step 3: Update UI calls to pass the sealed class instance ---
        OutlinedTextField(value = uiState.voltage, onValueChange = { viewModel.onValueChange(PowerField.Voltage, it) }, label = { Text(stringResource(R.string.voltage_v)) }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = uiState.current, onValueChange = { viewModel.onValueChange(PowerField.Current, it) }, label = { Text(stringResource(R.string.current_a)) }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth())

        if (uiState.calcType == PowerCalcType.AC_Single_Phase) {
            OutlinedTextField(value = uiState.powerFactor, onValueChange = { viewModel.onValueChange(PowerField.PowerFactor, it) }, label = { Text(stringResource(R.string.power_factor)) }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth())
        }

        // --- Result ---
        uiState.result?.let { res ->
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(stringResource(R.string.results_title), style = MaterialTheme.typography.titleLarge)
                    if (res.power != null) {
                        val label = if(uiState.calcType == PowerCalcType.DC) stringResource(R.string.dc_power) else stringResource(R.string.real_power_w)
                        ElectronicsResultRow(label, "${formatter.format(res.power)} W")
                    }
                    if (res.resistance != null) {
                        ElectronicsResultRow(stringResource(R.string.resistance_ohm), "${formatter.format(res.resistance)} Ω")
                    }
                    if (res.apparentPower != null) {
                        ElectronicsResultRow(stringResource(R.string.apparent_power_va), "${formatter.format(res.apparentPower)} VA")
                    }
                    if (res.reactivePower != null) {
                        ElectronicsResultRow(stringResource(R.string.reactive_power_var), "${formatter.format(res.reactivePower)} VAR")
                    }
                    if (res.phaseAngle != null) {
                        ElectronicsResultRow(stringResource(R.string.phase_angle_deg), "${formatter.format(res.phaseAngle)}°")
                    }
                }
            }
        }
    }
}
