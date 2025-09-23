package com.joviansapps.ganymede.ui.screens.utilities.physics

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

private const val GAS_CONSTANT = 8.31446261815324 // J/(mol·K)

// --- 1. State and Enums ---
enum class UnknownVariable { Pressure, Volume, Moles, Temperature }

data class IdealGasLawUiState(
    val pressure: String = "101325", // Pa
    val volume: String = "0.0224", // m³
    val moles: String = "1", // mol
    val temperature: String = "273.15", // K
    val unknown: UnknownVariable = UnknownVariable.Pressure,
    val result: String? = null
)

// --- 2. ViewModel ---
class IdealGasLawViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(IdealGasLawUiState())
    val uiState = _uiState.asStateFlow()

    fun onValueChange(field: UnknownVariable, value: String) {
        _uiState.update {
            when (field) {
                UnknownVariable.Pressure -> it.copy(pressure = value)
                UnknownVariable.Volume -> it.copy(volume = value)
                UnknownVariable.Moles -> it.copy(moles = value)
                UnknownVariable.Temperature -> it.copy(temperature = value)
            }
        }
        calculate()
    }

    fun onUnknownChange(unknown: UnknownVariable) {
        _uiState.update { it.copy(unknown = unknown) }
        calculate()
    }

    private fun calculate() {
        viewModelScope.launch {
            val state = _uiState.value
            val p = state.pressure.toDoubleOrNull()
            val v = state.volume.toDoubleOrNull()
            val n = state.moles.toDoubleOrNull()
            val t = state.temperature.toDoubleOrNull()

            val formatter = DecimalFormat("#.#####")
            val res: Double? = when (state.unknown) {
                UnknownVariable.Pressure -> if (n != null && t != null && v != null && v != 0.0) (n * GAS_CONSTANT * t) / v else null
                UnknownVariable.Volume -> if (n != null && t != null && p != null && p != 0.0) (n * GAS_CONSTANT * t) / p else null
                UnknownVariable.Moles -> if (p != null && v != null && t != null && t != 0.0) (p * v) / (GAS_CONSTANT * t) else null
                UnknownVariable.Temperature -> if (p != null && v != null && n != null && n != 0.0) (p * v) / (n * GAS_CONSTANT) else null
            }

            val resultString = res?.let {
                when (state.unknown) {
                    UnknownVariable.Pressure -> "${formatter.format(it)} Pa"
                    UnknownVariable.Volume -> "${formatter.format(it)} m³"
                    UnknownVariable.Moles -> "${formatter.format(it)} mol"
                    UnknownVariable.Temperature -> "${formatter.format(it)} K"
                }
            }
            _uiState.update { it.copy(result = resultString) }
        }
    }

    init {
        calculate()
    }
}

// --- 3. Composable Screen ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IdealGasLawCalculatorScreen(viewModel: IdealGasLawViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Loi des Gaz Parfaits (PV=nRT)", style = MaterialTheme.typography.headlineSmall)

        Text(stringResource(id = R.string.calculate_for), style = MaterialTheme.typography.titleMedium)
        SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
            UnknownVariable.values().forEach { variable ->
                SegmentedButton(
                    selected = uiState.unknown == variable,
                    onClick = { viewModel.onUnknownChange(variable) },
                    shape = SegmentedButtonDefaults.itemShape(variable.ordinal, UnknownVariable.values().size)
                ) {
                    Text(variable.name)
                }
            }
        }

        GasLawInput(
            label = "Pression (Pa)",
            value = uiState.pressure,
            onValueChange = { viewModel.onValueChange(UnknownVariable.Pressure, it) },
            isEnabled = uiState.unknown != UnknownVariable.Pressure
        )
        GasLawInput(
            label = "Volume (m³)",
            value = uiState.volume,
            onValueChange = { viewModel.onValueChange(UnknownVariable.Volume, it) },
            isEnabled = uiState.unknown != UnknownVariable.Volume
        )
        GasLawInput(
            label = "Moles (mol)",
            value = uiState.moles,
            onValueChange = { viewModel.onValueChange(UnknownVariable.Moles, it) },
            isEnabled = uiState.unknown != UnknownVariable.Moles
        )
        GasLawInput(
            label = "Température (K)",
            value = uiState.temperature,
            onValueChange = { viewModel.onValueChange(UnknownVariable.Temperature, it) },
            isEnabled = uiState.unknown != UnknownVariable.Temperature
        )

        uiState.result?.let {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    Text(text = stringResource(id = R.string.result_title), style = MaterialTheme.typography.titleLarge)
                    Text(
                        text = it,
                        style = MaterialTheme.typography.headlineMedium.copy(color = MaterialTheme.colorScheme.primary)
                    )
                }
            }
        }
    }
}

@Composable
private fun GasLawInput(label: String, value: String, onValueChange: (String) -> Unit, isEnabled: Boolean) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        modifier = Modifier.fillMaxWidth(),
        enabled = isEnabled,
        readOnly = !isEnabled
    )
}
