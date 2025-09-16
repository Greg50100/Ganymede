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

enum class OpAmpConfig { Inverting, NonInverting }

data class OpAmpUiState(
    val config: OpAmpConfig = OpAmpConfig.Inverting,
    val r1: String = "1",
    val rf: String = "10",
    val vin: String = "1",
    val result: OpAmpResult? = null
)

data class OpAmpResult(
    val gain: Double,
    val vout: Double
)

class OpAmpViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(OpAmpUiState())
    val uiState = _uiState.asStateFlow()

    init {
        calculate()
    }

    fun onValueChange(field: String, value: String) {
        _uiState.update {
            when (field) {
                "r1" -> it.copy(r1 = value)
                "rf" -> it.copy(rf = value)
                "vin" -> it.copy(vin = value)
                else -> it
            }
        }
        calculate()
    }

    fun onConfigChange(config: OpAmpConfig) {
        _uiState.update { it.copy(config = config) }
        calculate()
    }

    private fun calculate() {
        viewModelScope.launch {
            val state = _uiState.value
            val r1 = state.r1.toDoubleOrNull()
            val rf = state.rf.toDoubleOrNull()
            val vin = state.vin.toDoubleOrNull()

            if (r1 == null || rf == null || vin == null || r1 <= 0) {
                _uiState.update { it.copy(result = null) }
                return@launch
            }

            val result = when (state.config) {
                OpAmpConfig.Inverting -> {
                    val gain = -rf / r1
                    val vout = gain * vin
                    OpAmpResult(gain, vout)
                }
                OpAmpConfig.NonInverting -> {
                    val gain = 1 + (rf / r1)
                    val vout = gain * vin
                    OpAmpResult(gain, vout)
                }
            }
            _uiState.update { it.copy(result = result) }
        }
    }
}

// --- UI ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OpAmpCalculatorScreen(viewModel: OpAmpViewModel = viewModel()) {
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
        Text(stringResource(R.string.op_amp_calculator_title), style = MaterialTheme.typography.headlineSmall)

        SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
            OpAmpConfig.values().forEach { config ->
                SegmentedButton(
                    selected = uiState.config == config,
                    onClick = { viewModel.onConfigChange(config) },
                    shape = SegmentedButtonDefaults.itemShape(config.ordinal, OpAmpConfig.values().size)
                ) {
                    Text(config.name)
                }
            }
        }

        // Using the new placeholder diagram
        // Drawable placeholder removed to avoid missing-resource build errors.
        Box(modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .padding(12.dp), contentAlignment = Alignment.Center) {
            Text(text = stringResource(id = R.string.op_amp_circuit_diagram), style = MaterialTheme.typography.bodyMedium)
        }


        OutlinedTextField(value = uiState.vin, onValueChange = { viewModel.onValueChange("vin", it) }, label = { Text(stringResource(R.string.input_voltage_vin)) }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = uiState.r1, onValueChange = { viewModel.onValueChange("r1", it) }, label = { Text(stringResource(R.string.resistance_r1_kohm)) }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = uiState.rf, onValueChange = { viewModel.onValueChange("rf", it) }, label = { Text(stringResource(R.string.feedback_resistance_rf_kohm)) }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth())


        uiState.result?.let {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(stringResource(R.string.results_title), style = MaterialTheme.typography.titleLarge)
                    ElectronicsResultRow(stringResource(R.string.voltage_gain), formatter.format(it.gain))
                    ElectronicsResultRow(stringResource(R.string.output_voltage_vout_short), "${formatter.format(it.vout)} V")
                }
            }
        }
    }
}
