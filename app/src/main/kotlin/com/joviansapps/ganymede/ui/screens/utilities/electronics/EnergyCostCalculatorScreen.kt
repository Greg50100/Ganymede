package com.joviansapps.ganymede.ui.screens.utilities.electronics

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
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

// --- 1. UI State ---
data class EnergyCostUiState(
    val powerConsumption: String = "",
    val usageHoursPerDay: String = "",
    val costPerKwh: String = "",
    val costPerDay: Double? = null,
    val costPerMonth: Double? = null,
    val costPerYear: Double? = null,
    val error: String? = null
)

// --- 2. ViewModel ---
class EnergyCostViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(EnergyCostUiState())
    val uiState = _uiState.asStateFlow()

    fun onPowerChanged(power: String) {
        _uiState.update { it.copy(powerConsumption = power) }
        calculate() // Trigger calculation on each change
    }

    fun onHoursChanged(hours: String) {
        _uiState.update { it.copy(usageHoursPerDay = hours) }
        calculate() // Trigger calculation on each change
    }

    fun onCostChanged(cost: String) {
        _uiState.update { it.copy(costPerKwh = cost) }
        calculate() // Trigger calculation on each change
    }

    private fun calculate() {
        viewModelScope.launch {
            val state = _uiState.value
            val powerW = state.powerConsumption.toDoubleOrNull()
            val hours = state.usageHoursPerDay.toDoubleOrNull()
            val cost = state.costPerKwh.toDoubleOrNull()

            // Don't show an error if fields are simply empty, just clear the results
            if (state.powerConsumption.isBlank() || state.usageHoursPerDay.isBlank() || state.costPerKwh.isBlank()) {
                _uiState.update {
                    it.copy(error = null, costPerDay = null, costPerMonth = null, costPerYear = null)
                }
                return@launch
            }

            if (powerW == null || hours == null || cost == null) {
                _uiState.update {
                    it.copy(
                        error = "Please enter valid numbers.",
                        costPerDay = null, costPerMonth = null, costPerYear = null
                    )
                }
                return@launch
            }

            val kwhPerDay = (powerW * hours) / 1000.0
            val dayCost = kwhPerDay * cost
            val monthCost = dayCost * 30.44 // Average days in a month
            val yearCost = dayCost * 365.25 // Average days in a year

            _uiState.update {
                it.copy(
                    error = null,
                    costPerDay = dayCost,
                    costPerMonth = monthCost,
                    costPerYear = yearCost
                )
            }
        }
    }
}

// --- 3. Composable Screen ---
@Composable
fun EnergyCostCalculatorScreen(viewModel: EnergyCostViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        OutlinedTextField(
            value = uiState.powerConsumption,
            onValueChange = viewModel::onPowerChanged,
            label = { Text(stringResource(R.string.power_consumption_w)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next),
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = uiState.usageHoursPerDay,
            onValueChange = viewModel::onHoursChanged,
            label = { Text(stringResource(R.string.usage_hours_per_day)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next),
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = uiState.costPerKwh,
            onValueChange = viewModel::onCostChanged,
            label = { Text(stringResource(R.string.cost_per_kwh)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        uiState.error?.let {
            Text(it, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(top = 8.dp))
        }

        if (uiState.costPerDay != null) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Results(
                    day = uiState.costPerDay!!,
                    month = uiState.costPerMonth!!,
                    year = uiState.costPerYear!!
                )
            }
        }
    }
}

@Composable
private fun Results(day: Double, month: Double, year: Double) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text(stringResource(R.string.estimated_cost), style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(8.dp))
        Text(stringResource(R.string.cost_per_day, day))
        Text(stringResource(R.string.cost_per_month, month))
        Text(stringResource(R.string.cost_per_year, year))
    }
}

