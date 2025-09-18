package com.joviansapps.ganymede.ui.screens.utilities.electronics

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
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
import kotlin.math.sqrt

// --- ViewModel and State ---

data class OhmsLawUiState(
    val voltage: String = "",
    val current: String = "",
    val resistance: String = "",
    val power: String = "", // ajouté
    val lastEdited: OhmsLawField? = null,
    val error: String? = null // New error field
)

enum class OhmsLawField { VOLTAGE, CURRENT, RESISTANCE, POWER }

class OhmsLawViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(OhmsLawUiState())
    val uiState = _uiState.asStateFlow()

    fun onValueChange(field: OhmsLawField, value: String) {
        _uiState.update {
            when (field) {
                OhmsLawField.VOLTAGE -> it.copy(voltage = value, lastEdited = field)
                OhmsLawField.CURRENT -> it.copy(current = value, lastEdited = field)
                OhmsLawField.RESISTANCE -> it.copy(resistance = value, lastEdited = field)
                OhmsLawField.POWER -> it.copy(power = value, lastEdited = field)
            }
        }
        calculate()
    }

    private fun calculate() {
        viewModelScope.launch {
            val state = _uiState.value
            _uiState.update { it.copy(error = null) } // Reset error at the start of calculation

            val v = state.voltage.toDoubleOrNull()
            val i = state.current.toDoubleOrNull()
            val r = state.resistance.toDoubleOrNull()
            val p = state.power.toDoubleOrNull()

            when (state.lastEdited) {
                OhmsLawField.VOLTAGE, OhmsLawField.CURRENT -> {
                    if (v != null && i != null) {
                        // Resistance
                        if (i == 0.0) {
                            _uiState.update { it.copy(resistance = "", error = "error_current_zero") }
                        } else {
                            _uiState.update { it.copy(resistance = (v / i).toString()) }
                        }
                        // Power
                        _uiState.update { it.copy(power = (v * i).toString()) }
                    }
                }
                OhmsLawField.RESISTANCE -> {
                    if (i != null && r != null) {
                        // V = I * R
                        _uiState.update { it.copy(voltage = (i * r).toString(), power = (i * i * r).toString()) }
                    } else if (v != null && r != null) {
                        if (r == 0.0) {
                            _uiState.update { it.copy(current = "", error = "error_resistance_zero") }
                        } else {
                            val currentCalc = (v / r)
                            _uiState.update { it.copy(current = currentCalc.toString(), power = (v * currentCalc).toString()) }
                        }
                    }
                }
                OhmsLawField.POWER -> {
                    if (p != null) {
                        when {
                            // Given P and V -> I = P / V ; R = V^2 / P (if P != 0)
                            v != null -> {
                                if (v == 0.0) {
                                    _uiState.update { it.copy(current = "", resistance = "", error = "error_voltage_zero") }
                                } else {
                                    val currentCalc = p / v
                                    val resistanceCalc = if (p != 0.0) (v * v / p) else null
                                    _uiState.update { it.copy(current = currentCalc.toString(), resistance = resistanceCalc?.toString() ?: "") }
                                }
                            }
                            // Given P and I -> V = P / I ; R = P / I^2
                            i != null -> {
                                if (i == 0.0) {
                                    _uiState.update { it.copy(voltage = "", resistance = "", error = "error_current_zero") }
                                } else {
                                    val voltageCalc = p / i
                                    val resistanceCalc = if (i != 0.0) (p / (i * i)) else null
                                    _uiState.update { it.copy(voltage = voltageCalc.toString(), resistance = resistanceCalc?.toString() ?: "") }
                                }
                            }
                            // Given P and R -> V = sqrt(P * R) ; I = sqrt(P / R)
                            r != null -> {
                                if (r < 0 || p < 0) {
                                    _uiState.update { it.copy(voltage = "", current = "", error = "error_power_invalid") }
                                } else if (r == 0.0) {
                                    _uiState.update { it.copy(voltage = "", current = "", error = "error_resistance_zero") }
                                } else {
                                    val voltageCalc = sqrt(p * r)
                                    val currentCalc = sqrt(p / r)
                                    _uiState.update { it.copy(voltage = voltageCalc.toString(), current = currentCalc.toString()) }
                                }
                            }
                        }
                    }
                }
                null -> {}
            }
        }
    }
}

// --- UI ---
@Composable
@Preview
fun OhmsLawCalculatorScreen(viewModel: OhmsLawViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.ohms_law_wheel),
            contentDescription = stringResource(R.string.ohms_law_triangle_description),
            contentScale = ContentScale.FillWidth,
            modifier = Modifier
                .fillMaxWidth(),
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary)
        )

        Text(
            text = stringResource(R.string.ohms_law_description),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyMedium
        )

        // Input Fields
        OhmsLawInput(
            value = uiState.voltage,
            onValueChange = { viewModel.onValueChange(OhmsLawField.VOLTAGE, it) },
            label = stringResource(R.string.voltage_v)
        )

        OhmsLawInput(
            value = uiState.current,
            onValueChange = { viewModel.onValueChange(OhmsLawField.CURRENT, it) },
            label = stringResource(R.string.current_a)
        )

        OhmsLawInput(
            value = uiState.resistance,
            onValueChange = { viewModel.onValueChange(OhmsLawField.RESISTANCE, it) },
            label = stringResource(R.string.resistance_ohm)
        )

        // Power input
        OhmsLawInput(
            value = uiState.power,
            onValueChange = { viewModel.onValueChange(OhmsLawField.POWER, it) },
            label = "Power (W)"
        )

        uiState.error?.let { errorKey ->
            // Compose the error text: use existing string resources when available, otherwise fall back to literals
            val errorText: String = when (errorKey) {
                "error_current_zero" -> stringResource(R.string.error_current_zero)
                "error_resistance_zero" -> stringResource(R.string.error_resistance_zero)
                "error_voltage_zero" -> "Voltage cannot be zero to calculate current."
                "error_power_invalid" -> "Power and resistance must be non-negative to calculate voltage/current."
                else -> errorKey
            }

            Text(
                text = errorText,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

@Composable
private fun OhmsLawInput(value: String, onValueChange: (String) -> Unit, label: String) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        modifier = Modifier.fillMaxWidth()
    )
}
