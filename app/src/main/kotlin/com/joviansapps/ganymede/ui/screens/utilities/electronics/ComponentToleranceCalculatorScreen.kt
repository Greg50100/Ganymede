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

data class ToleranceUiState(
    val nominalValue: String = "100",
    val tolerance: String = "5",
    val minValue: Double? = null,
    val maxValue: Double? = null
)

class ComponentToleranceViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(ToleranceUiState())
    val uiState = _uiState.asStateFlow()

    init {
        calculate()
    }

    fun onValueChange(field: String, value: String) {
        _uiState.update {
            when (field) {
                "nominal" -> it.copy(nominalValue = value)
                "tolerance" -> it.copy(tolerance = value)
                else -> it
            }
        }
        calculate()
    }

    private fun calculate() {
        viewModelScope.launch {
            val nominal = _uiState.value.nominalValue.toDoubleOrNull()
            val tolerance = _uiState.value.tolerance.toDoubleOrNull()

            if (nominal == null || tolerance == null) {
                _uiState.update { it.copy(minValue = null, maxValue = null) }
                return@launch
            }

            val toleranceValue = nominal * (tolerance / 100.0)
            val min = nominal - toleranceValue
            val max = nominal + toleranceValue

            _uiState.update { it.copy(minValue = min, maxValue = max) }
        }
    }
}

@Composable
fun ComponentToleranceCalculatorScreen(viewModel: ComponentToleranceViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(stringResource(id = R.string.component_tolerance_calculator_title), style = MaterialTheme.typography.headlineSmall)
        Text("Calcule la plage de valeurs min/max pour un composant.", style = MaterialTheme.typography.bodyMedium)

        OutlinedTextField(
            value = uiState.nominalValue,
            onValueChange = { viewModel.onValueChange("nominal", it) },
            label = { Text("Valeur Nominale") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = uiState.tolerance,
            onValueChange = { viewModel.onValueChange("tolerance", it) },
            label = { Text("Tolérance (%)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        if (uiState.minValue != null) {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(text = stringResource(id = R.string.result_title), style = MaterialTheme.typography.titleLarge)
                    ToleranceResultRow("Valeur Minimale", uiState.minValue)
                    ToleranceResultRow("Valeur Maximale", uiState.maxValue)
                }
            }
        }
    }
}

@Composable
private fun ToleranceResultRow(label: String, value: Double?) {
    val formatter = DecimalFormat("#.####")
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodyLarge)
        Text(
            text = value?.let { formatter.format(it) } ?: "N/A",
            style = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.primary)
        )
    }
}
