package com.joviansapps.ganymede.ui.screens.utilities.electronics

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
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
import java.text.DecimalFormat
import kotlin.math.PI
import kotlin.math.sqrt

data class ResonanceUiState(
    val resistance: String = "10",
    val capacitance: String = "100n",
    val inductance: String = "10m",
    val resonantFrequency: Double? = null,
    val qFactor: Double? = null,
    val bandwidth: Double? = null
)

class RlcResonantCircuitViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(ResonanceUiState())
    val uiState = _uiState.asStateFlow()

    init {
        calculate()
    }

    fun onValueChange(field: String, value: String) {
        _uiState.update {
            when (field) {
                "r" -> it.copy(resistance = value)
                "c" -> it.copy(capacitance = value)
                "l" -> it.copy(inductance = value)
                else -> it
            }
        }
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
                'm' -> multiplier = 1e6
            }
            return valueString.dropLast(1).toDoubleOrNull()?.let { it * multiplier }
        }
        return valueString.toDoubleOrNull()
    }

    private fun calculate() {
        viewModelScope.launch {
            val r = parseValue(_uiState.value.resistance)
            val c = parseValue(_uiState.value.capacitance)
            val l = parseValue(_uiState.value.inductance)

            if (r == null || c == null || l == null || r <= 0 || c <= 0 || l <= 0) {
                _uiState.update { it.copy(resonantFrequency = null, qFactor = null, bandwidth = null) }
                return@launch
            }

            val f0 = 1 / (2 * PI * sqrt(l * c))
            val q = (1 / r) * sqrt(l / c)
            val bw = f0 / q

            _uiState.update {
                it.copy(resonantFrequency = f0, qFactor = q, bandwidth = bw)
            }
        }
    }
}

@Composable
fun RlcResonantCircuitCalculatorScreen(viewModel: RlcResonantCircuitViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(stringResource(id = R.string.rlc_resonant_circuit_calculator_title), style = MaterialTheme.typography.headlineSmall)

        OutlinedTextField(value = uiState.resistance, onValueChange = { viewModel.onValueChange("r", it) }, label = { Text("Résistance (Ω)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text), modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = uiState.capacitance, onValueChange = { viewModel.onValueChange("c", it) }, label = { Text("Capacité (F)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text), modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = uiState.inductance, onValueChange = { viewModel.onValueChange("l", it) }, label = { Text("Inductance (H)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text), modifier = Modifier.fillMaxWidth())

        if (uiState.resonantFrequency != null) {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    ResonanceResultRow("Fréquence de Résonance (f₀)", uiState.resonantFrequency, "Hz")
                    ResonanceResultRow("Facteur de Qualité (Q)", uiState.qFactor, "")
                    ResonanceResultRow("Bande Passante (BW)", uiState.bandwidth, "Hz")
                }
            }
        }
    }
}

@Composable
private fun ResonanceResultRow(label: String, value: Double?, unit: String) {
    val formatter = DecimalFormat("#.###")
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodyLarge)
        Text(
            text = "${formatter.format(value)} $unit",
            style = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.primary)
        )
    }
}
