package com.joviansapps.ganymede.ui.screens.utilities.electronics

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
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

// --- State and ViewModel ---

enum class UnknownResistor { R1, R2, R3, Rx }

data class WheatstoneBridgeUiState(
    val r1: String = "100",
    val r2: String = "200",
    val r3: String = "150",
    val rx: String = "",
    val unknown: UnknownResistor = UnknownResistor.Rx,
    val calculatedValue: Double? = null
)

class WheatstoneBridgeViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(WheatstoneBridgeUiState())
    val uiState = _uiState.asStateFlow()

    init {
        calculate()
    }

    fun onValueChange(field: String, value: String) {
        _uiState.update {
            when (field) {
                "r1" -> it.copy(r1 = value)
                "r2" -> it.copy(r2 = value)
                "r3" -> it.copy(r3 = value)
                "rx" -> it.copy(rx = value)
                else -> it
            }
        }
        calculate()
    }

    fun setUnknownResistor(resistor: UnknownResistor) {
        _uiState.update { it.copy(unknown = resistor) }
        calculate()
    }

    private fun calculate() {
        viewModelScope.launch {
            val state = _uiState.value
            val r1 = state.r1.toDoubleOrNull()
            val r2 = state.r2.toDoubleOrNull()
            val r3 = state.r3.toDoubleOrNull()
            val rx = state.rx.toDoubleOrNull()

            val result: Double? = when (state.unknown) {
                UnknownResistor.Rx -> if (r1 != null && r2 != null && r3 != null && r1 > 0) (r2 / r1) * r3 else null
                UnknownResistor.R1 -> if (rx != null && r2 != null && r3 != null && r2 > 0) (r3 / rx) * r2 else null
                UnknownResistor.R2 -> if (rx != null && r1 != null && r3 != null && r3 > 0) (rx / r3) * r1 else null
                UnknownResistor.R3 -> if (rx != null && r1 != null && r2 != null && r2 > 0) (rx / r2) * r1 else null
            }
            _uiState.update { it.copy(calculatedValue = result) }
        }
    }
}

// --- UI ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WheatstoneBridgeCalculatorScreen(viewModel: WheatstoneBridgeViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val formatter = DecimalFormat("#.##")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(stringResource(R.string.wheatstone_bridge_calculator_title), style = MaterialTheme.typography.headlineSmall)

        Text(stringResource(id = R.string.calculate_for), style = MaterialTheme.typography.titleMedium)
        SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
            UnknownResistor.values().forEach { resistor ->
                SegmentedButton(
                    selected = uiState.unknown == resistor,
                    onClick = { viewModel.setUnknownResistor(resistor) },
                    shape = SegmentedButtonDefaults.itemShape(resistor.ordinal, UnknownResistor.values().size)
                ) {
                    Text(resistor.name)
                }
            }
        }

        // Drawable placeholder removed to avoid missing-resource build errors.
        Box(modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
            .padding(12.dp), contentAlignment = Alignment.Center) {
            Text(text = stringResource(id = R.string.wheatstone_bridge_description), style = MaterialTheme.typography.bodyMedium)
        }

        // --- Input Fields ---
        val calculatedValueStr = uiState.calculatedValue?.let { formatter.format(it) } ?: ""

        OutlinedTextField(
            value = if (uiState.unknown == UnknownResistor.R1) calculatedValueStr else uiState.r1,
            onValueChange = { viewModel.onValueChange("r1", it) },
            label = { Text(stringResource(R.string.resistance_r1_ohm)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth(),
            enabled = uiState.unknown != UnknownResistor.R1,
            readOnly = uiState.unknown == UnknownResistor.R1
        )
        OutlinedTextField(
            value = if (uiState.unknown == UnknownResistor.R2) calculatedValueStr else uiState.r2,
            onValueChange = { viewModel.onValueChange("r2", it) },
            label = { Text(stringResource(R.string.resistance_r2_ohm)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth(),
            enabled = uiState.unknown != UnknownResistor.R2,
            readOnly = uiState.unknown == UnknownResistor.R2
        )
        OutlinedTextField(
            value = if (uiState.unknown == UnknownResistor.R3) calculatedValueStr else uiState.r3,
            onValueChange = { viewModel.onValueChange("r3", it) },
            label = { Text(stringResource(R.string.resistance_r3_ohm)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth(),
            enabled = uiState.unknown != UnknownResistor.R3,
            readOnly = uiState.unknown == UnknownResistor.R3
        )
        OutlinedTextField(
            value = if (uiState.unknown == UnknownResistor.Rx) calculatedValueStr else uiState.rx,
            onValueChange = { viewModel.onValueChange("rx", it) },
            label = { Text(stringResource(R.string.unknown_resistance_rx)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth(),
            enabled = uiState.unknown != UnknownResistor.Rx,
            readOnly = uiState.unknown == UnknownResistor.Rx
        )
    }
}
