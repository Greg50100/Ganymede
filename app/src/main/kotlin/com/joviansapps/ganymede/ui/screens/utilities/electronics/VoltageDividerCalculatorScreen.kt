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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
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

// --- ViewModel and State ---

data class VoltageDividerUiState(
    val vin: String = "",
    val r1: String = "",
    val r2: String = "",
    val vout: Double? = null
)

class VoltageDividerViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(VoltageDividerUiState())
    val uiState = _uiState.asStateFlow()

    fun onValueChange(field: String, value: String) {
        _uiState.update {
            when (field) {
                "vin" -> it.copy(vin = value)
                "r1" -> it.copy(r1 = value)
                "r2" -> it.copy(r2 = value)
                else -> it
            }
        }
        calculate()
    }

    private fun calculate() {
        viewModelScope.launch {
            val state = _uiState.value
            val vin = state.vin.toDoubleOrNull()
            val r1 = state.r1.toDoubleOrNull()
            val r2 = state.r2.toDoubleOrNull()

            if (vin != null && r1 != null && r2 != null && (r1 + r2) != 0.0) {
                val vout = vin * (r2 / (r1 + r2))
                _uiState.update { it.copy(vout = vout) }
            } else {
                _uiState.update { it.copy(vout = null) }
            }
        }
    }
}


// --- UI ---
@Composable
@Preview
fun VoltageDividerCalculatorScreen(viewModel: VoltageDividerViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val formatter = DecimalFormat("#.####")


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(stringResource(R.string.voltage_divider_calculator_title), style = MaterialTheme.typography.headlineSmall)

        // removed Image(resource) that referenced a missing drawable; show descriptive text instead
        Text(
            text = stringResource(R.string.voltage_divider_circuit_description),
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center
        )

        Text(
            text = "Vout = Vin * (R2 / (R1 + R2))",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )

        OutlinedTextField(
            value = uiState.vin,
            onValueChange = { viewModel.onValueChange("vin", it) },
            label = { Text(stringResource(R.string.input_voltage_vin)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = uiState.r1,
            onValueChange = { viewModel.onValueChange("r1", it) },
            label = { Text(stringResource(R.string.resistance_r1_ohm)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = uiState.r2,
            onValueChange = { viewModel.onValueChange("r2", it) },
            label = { Text(stringResource(R.string.resistance_r2_ohm)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp)) {
                Text(
                    text = stringResource(R.string.output_voltage_vout, uiState.vout?.let(formatter::format) ?: "N/A"),
                    style = MaterialTheme.typography.titleLarge
                )
            }
        }
    }
}
