package com.joviansapps.ganymede.ui.screens.utilities.electronics

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
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

data class LedResistorUiState(
    val sourceVoltage: String = "",
    val ledForwardVoltage: String = "",
    val ledForwardCurrent: String = "",
    val resistance: Double? = null
)

class LedResistorViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(LedResistorUiState())
    val uiState = _uiState.asStateFlow()

    fun onValueChange(field: String, value: String) {
        _uiState.update {
            when (field) {
                "source" -> it.copy(sourceVoltage = value)
                "forward_v" -> it.copy(ledForwardVoltage = value)
                "forward_c" -> it.copy(ledForwardCurrent = value)
                else -> it
            }
        }
        calculate()
    }

    private fun calculate() {
        viewModelScope.launch {
            val state = _uiState.value
            val sourceV = state.sourceVoltage.toDoubleOrNull()
            val forwardV = state.ledForwardVoltage.toDoubleOrNull()
            val forwardC_mA = state.ledForwardCurrent.toDoubleOrNull()

            if (sourceV != null && forwardV != null && forwardC_mA != null && forwardC_mA > 0 && sourceV > forwardV) {
                val forwardC_A = forwardC_mA / 1000.0 // Convert mA to A
                val resistance = (sourceV - forwardV) / forwardC_A
                _uiState.update { it.copy(resistance = resistance) }
            } else {
                _uiState.update { it.copy(resistance = null) }
            }
        }
    }
}

@Composable
fun LedResistorCalculatorScreen(viewModel: LedResistorViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val formatter = DecimalFormat("#.##")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(stringResource(R.string.led_resistor_calculator_title), style = MaterialTheme.typography.headlineSmall)

        OutlinedTextField(
            value = uiState.sourceVoltage,
            onValueChange = { viewModel.onValueChange("source", it) },
            label = { Text(stringResource(R.string.source_voltage_v)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = uiState.ledForwardVoltage,
            onValueChange = { viewModel.onValueChange("forward_v", it) },
            label = { Text(stringResource(R.string.led_forward_voltage_v)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = uiState.ledForwardCurrent,
            onValueChange = { viewModel.onValueChange("forward_c", it) },
            label = { Text(stringResource(R.string.led_forward_current_ma)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp)) {
                Text(
                    text = stringResource(R.string.required_resistor_value, uiState.resistance?.let(formatter::format) ?: "N/A"),
                    style = MaterialTheme.typography.titleLarge
                )
            }
        }
    }
}
