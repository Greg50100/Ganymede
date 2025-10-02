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
import com.joviansapps.ganymede.ui.components.ESeriesDropdown
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.math.*

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
            val nearest = computeNearestStandardValue(desired, _uiState.value.selectedSeries)
            _uiState.update { it.copy(nearestValue = nearest) }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StandardValueCalculatorScreen(viewModel: StandardValueViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(stringResource(id = R.string.standard_value_calculator_title), style = MaterialTheme.typography.headlineSmall)
        Text(stringResource(id = R.string.standard_value_description_fr), style = MaterialTheme.typography.bodyMedium)

        OutlinedTextField(
            value = uiState.desiredValue,
            onValueChange = viewModel::onValueChange,
            label = { Text(stringResource(R.string.standard_value_desired_label)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        // Utilise le composable réutilisable pour la sélection de la série E
        ESeriesDropdown(
            selectedSeries = uiState.selectedSeries,
            onSeriesSelected = viewModel::onSeriesChange,
            modifier = Modifier.fillMaxWidth()
        )

        uiState.nearestValue?.let {
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    Text(text = stringResource(R.string.nearest_value_label), style = MaterialTheme.typography.titleLarge)
                    Text(
                        text = formatValue(it),
                        style = MaterialTheme.typography.headlineMedium.copy(color = MaterialTheme.colorScheme.primary)
                    )
                }
            }
        }
    }
}
