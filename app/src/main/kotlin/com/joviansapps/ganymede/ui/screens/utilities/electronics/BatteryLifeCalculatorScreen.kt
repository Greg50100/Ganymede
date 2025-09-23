package com.joviansapps.ganymede.ui.screens.utilities.electronics

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import com.joviansapps.ganymede.ui.components.ResultField
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.math.floor

// --- 1. UI State ---
data class BatteryLifeUiState(
    val capacity: String = "2000",
    val consumption: String = "150",
    val result: Double? = null
)

// --- 2. ViewModel ---
class BatteryLifeViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(BatteryLifeUiState())
    val uiState = _uiState.asStateFlow()

    init {
        calculate()
    }

    fun onCapacityChange(value: String) {
        _uiState.update { it.copy(capacity = value) }
        calculate()
    }

    fun onConsumptionChange(value: String) {
        _uiState.update { it.copy(consumption = value) }
        calculate()
    }

    private fun calculate() {
        viewModelScope.launch {
            val capacityMah = _uiState.value.capacity.toDoubleOrNull()
            val consumptionMa = _uiState.value.consumption.toDoubleOrNull()

            if (capacityMah == null || consumptionMa == null || consumptionMa <= 0) {
                _uiState.update { it.copy(result = null) }
                return@launch
            }

            // Simple formula: Life (hours) = Capacity (mAh) / Consumption (mA)
            val life = capacityMah / consumptionMa
            _uiState.update { it.copy(result = life) }
        }
    }
}

// --- 3. Composable Screen ---
@Composable
fun BatteryLifeCalculatorScreen(viewModel: BatteryLifeViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(stringResource(R.string.battery_life_calculator_title), style = MaterialTheme.typography.headlineSmall)

        OutlinedTextField(
            value = uiState.capacity,
            onValueChange = viewModel::onCapacityChange,
            label = { Text(stringResource(R.string.battery_capacity_mah)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = uiState.consumption,
            onValueChange = viewModel::onConsumptionChange,
            label = { Text(stringResource(R.string.circuit_consumption_ma)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

//        uiState.result?.let { hours ->
//            Card(modifier = Modifier.fillMaxWidth()) {
//                Column(Modifier.padding(16.dp)) {
//                    Text(
//                        text = stringResource(R.string.estimated_battery_life),
//                        style = MaterialTheme.typography.titleLarge
//                    )
//                    Text(
//                        text = formatDuration(hours),
//                        style = MaterialTheme.typography.headlineMedium.copy(color = MaterialTheme.colorScheme.primary)
//                    )
//                }
//            }
//        }
        uiState.result?.let {
            val resultsText =
                "${stringResource(R.string.estimated_battery_life)}: ${formatDuration(it)}"

            ResultField(
                label = stringResource(R.string.results_title),
                value = resultsText,
                modifier = Modifier.fillMaxWidth()
            )

        }
    }
}

private fun formatDuration(totalHours: Double): String {
    if (totalHours < 0) return "N/A"
    val days = floor(totalHours / 24).toInt()
    val remainingHours = floor(totalHours % 24).toInt()
    val remainingMinutes = floor((totalHours * 60) % 60).toInt()

    val parts = mutableListOf<String>()
    if (days > 0) parts.add("$days d")
    if (remainingHours > 0) parts.add("$remainingHours h")
    if (remainingMinutes > 0) parts.add("$remainingMinutes m")

    return if (parts.isEmpty()) "Less than a minute" else parts.joinToString(" ")
}
