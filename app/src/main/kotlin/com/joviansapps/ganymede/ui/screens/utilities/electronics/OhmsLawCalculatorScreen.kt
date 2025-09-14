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

// --- ViewModel and State ---

data class OhmsLawUiState(
    val voltage: String = "",
    val current: String = "",
    val resistance: String = "",
    val lastEdited: OhmsLawField? = null
)

enum class OhmsLawField { VOLTAGE, CURRENT, RESISTANCE }

class OhmsLawViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(OhmsLawUiState())
    val uiState = _uiState.asStateFlow()

    fun onValueChange(field: OhmsLawField, value: String) {
        _uiState.update {
            when (field) {
                OhmsLawField.VOLTAGE -> it.copy(voltage = value, lastEdited = field)
                OhmsLawField.CURRENT -> it.copy(current = value, lastEdited = field)
                OhmsLawField.RESISTANCE -> it.copy(resistance = value, lastEdited = field)
            }
        }
        calculate()
    }

    private fun calculate() {
        viewModelScope.launch {
            val state = _uiState.value
            val v = state.voltage.toDoubleOrNull()
            val i = state.current.toDoubleOrNull()
            val r = state.resistance.toDoubleOrNull()

            when (state.lastEdited) {
                OhmsLawField.VOLTAGE, OhmsLawField.CURRENT -> {
                    if (v != null && i != null && i != 0.0) {
                        _uiState.update { it.copy(resistance = (v / i).toString()) }
                    }
                }
                OhmsLawField.RESISTANCE -> {
                    if (i != null && r != null) {
                        _uiState.update { it.copy(voltage = (i * r).toString()) }
                    } else if (v != null && r != null && r != 0.0) {
                        _uiState.update { it.copy(current = (v / r).toString()) }
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
        Text(stringResource(R.string.ohms_law_calculator_title), style = MaterialTheme.typography.headlineSmall)

        // Visual aid for Ohm's Law
        Image(
            painter = painterResource(id = R.drawable.ohms_law_triangle),
            contentDescription = stringResource(R.string.ohms_law_triangle_description),
            modifier = Modifier.height(120.dp)
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
