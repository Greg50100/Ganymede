package com.joviansapps.ganymede.ui.screens.utilities.electronics

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
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
import kotlin.math.*

enum class ESeries(val values: List<Double>) {
    E6(listOf(1.0, 1.5, 2.2, 3.3, 4.7, 6.8)),
    E12(listOf(1.0, 1.2, 1.5, 1.8, 2.2, 2.7, 3.3, 3.9, 4.7, 5.6, 6.8, 8.2)),
    E24(listOf(1.0, 1.1, 1.2, 1.3, 1.5, 1.6, 1.8, 2.0, 2.2, 2.4, 2.7, 3.0, 3.3, 3.6, 3.9, 4.3, 4.7, 5.1, 5.6, 6.2, 6.8, 7.5, 8.2, 9.1)),
    E48(listOf(1.00, 1.05, 1.10, 1.15, 1.21, 1.27, 1.33, 1.40, 1.47, 1.54, 1.62, 1.69, 1.78, 1.87, 1.96, 2.05, 2.15, 2.26, 2.37, 2.49, 2.61, 2.74, 2.87, 3.01, 3.16, 3.32, 3.48, 3.65, 3.83, 4.02, 4.22, 4.42, 4.64, 4.87, 5.11, 5.36, 5.62, 5.90, 6.19, 6.49, 6.81, 7.15, 7.50, 7.87, 8.25, 8.66, 9.09, 9.53)),
    E96(listOf(1.00, 1.02, 1.05, 1.07, 1.10, 1.13, 1.15, 1.18, 1.21, 1.24, 1.27, 1.30, 1.33, 1.37, 1.40, 1.43, 1.47, 1.50, 1.54, 1.58, 1.62, 1.65, 1.69, 1.74, 1.78, 1.82, 1.87, 1.91, 1.96, 2.00, 2.05, 2.10, 2.15, 2.21, 2.26, 2.32, 2.37, 2.43, 2.49, 2.55, 2.61, 2.67, 2.74, 2.80, 2.87, 2.94, 3.01, 3.09, 3.16, 3.24, 3.32, 3.40, 3.48, 3.57, 3.65, 3.74, 3.83, 3.92, 4.02, 4.12, 4.22, 4.32, 4.42, 4.53, 4.64, 4.75, 4.87, 4.99, 5.11, 5.23, 5.36, 5.49, 5.62, 5.76, 5.90, 6.04, 6.19, 6.34, 6.49, 6.65, 6.81, 6.98, 7.15, 7.32, 7.50, 7.68, 7.87, 8.06, 8.25, 8.45, 8.66, 8.87, 9.09, 9.31, 9.53, 9.76))
}

data class StandardValueUiState(
    val desiredValue: String = "4000",
    val selectedSeries: ESeries = ESeries.E24,
    val nearestValue: Double? = null
)

class StandardValueViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(StandardValueUiState())
    val uiState = _uiState.asStateFlow()

    init {
        calculate()
    }

    fun onValueChange(value: String) {
        _uiState.update { it.copy(desiredValue = value) }
        calculate()
    }

    fun onSeriesChange(series: ESeries) {
        _uiState.update { it.copy(selectedSeries = series) }
        calculate()
    }

    private fun calculate() {
        viewModelScope.launch {
            val desired = _uiState.value.desiredValue.toDoubleOrNull() ?: return@launch
            if (desired <= 0) return@launch

            val seriesValues = _uiState.value.selectedSeries.values
            val magnitude = 10.0.pow(floor(log10(desired)))
            val normalizedDesired = desired / magnitude

            val nearestNormalized = seriesValues.minByOrNull { abs(it - normalizedDesired) } ?: return@launch
            val nearest = nearestNormalized * magnitude

            _uiState.update { it.copy(nearestValue = nearest) }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StandardValueCalculatorScreen(viewModel: StandardValueViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var isDropdownExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(stringResource(id = R.string.standard_value_calculator_title), style = MaterialTheme.typography.headlineSmall)
        Text("Trouve la valeur standard la plus proche dans une série E.", style = MaterialTheme.typography.bodyMedium)

        OutlinedTextField(
            value = uiState.desiredValue,
            onValueChange = viewModel::onValueChange,
            label = { Text("Valeur Désirée") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        ExposedDropdownMenuBox(
            expanded = isDropdownExpanded,
            onExpandedChange = { isDropdownExpanded = !isDropdownExpanded }
        ) {
            OutlinedTextField(
                value = uiState.selectedSeries.name,
                onValueChange = {},
                readOnly = true,
                label = { Text("Série E") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isDropdownExpanded) },
                modifier = Modifier.menuAnchor().fillMaxWidth()
            )
            ExposedDropdownMenu(
                expanded = isDropdownExpanded,
                onDismissRequest = { isDropdownExpanded = false }
            ) {
                ESeries.values().forEach { series ->
                    DropdownMenuItem(
                        text = { Text(series.name) },
                        onClick = {
                            viewModel.onSeriesChange(series)
                            isDropdownExpanded = false
                        }
                    )
                }
            }
        }

        uiState.nearestValue?.let {
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    Text(text = "Valeur la plus proche", style = MaterialTheme.typography.titleLarge)
                    Text(
                        text = formatValue(it),
                        style = MaterialTheme.typography.headlineMedium.copy(color = MaterialTheme.colorScheme.primary)
                    )
                }
            }
        }
    }
}

private fun formatValue(value: Double): String {
    val formatter = DecimalFormat("#.###")
    return when {
        value >= 1_000_000_000 -> "${formatter.format(value / 1_000_000_000)} G"
        value >= 1_000_000 -> "${formatter.format(value / 1_000_000)} M"
        value >= 1_000 -> "${formatter.format(value / 1_000)} k"
        value < 1 -> "${formatter.format(value * 1_000)} m"
        else -> formatter.format(value)
    }
}
