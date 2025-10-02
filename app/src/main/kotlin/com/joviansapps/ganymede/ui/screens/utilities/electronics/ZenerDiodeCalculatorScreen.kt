package com.joviansapps.ganymede.ui.screens.utilities.electronics

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
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
import com.joviansapps.ganymede.ui.components.ElectronicsResultRow
import com.joviansapps.ganymede.ui.components.NumericTextField
import com.joviansapps.ganymede.ui.components.formatDouble
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.math.pow

// --- State and ViewModel ---

// --- Refactoring Step 1: Define a sealed class for input fields ---
sealed class ZenerField {
    data object SourceVoltage : ZenerField()
    data object ZenerVoltage : ZenerField()
    data object LoadResistance : ZenerField()
    data object ZenerPower : ZenerField()
}

data class ZenerDiodeUiState(
    val sourceVoltage: String = "12",
    val zenerVoltage: String = "5.1",
    val loadResistance: String = "1000",
    val zenerPower: String = "0.5",
    val result: ZenerResult? = null,
    val error: String? = null
)

data class ZenerResult(
    val seriesResistor: Double,
    val resistorPower: Double,
    val loadCurrent: Double,
    val zenerCurrent: Double
)

class ZenerDiodeViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(ZenerDiodeUiState())
    val uiState = _uiState.asStateFlow()

    init {
        calculate()
    }

    // --- Refactoring Step 2: Update the event handler to use the sealed class ---
    fun onValueChange(field: ZenerField, value: String) {
        _uiState.update {
            when (field) {
                is ZenerField.SourceVoltage -> it.copy(sourceVoltage = value)
                is ZenerField.ZenerVoltage -> it.copy(zenerVoltage = value)
                is ZenerField.LoadResistance -> it.copy(loadResistance = value)
                is ZenerField.ZenerPower -> it.copy(zenerPower = value)
            }
        }
        calculate()
    }

    private fun calculate() {
        viewModelScope.launch {
            val state = _uiState.value
            val vin = state.sourceVoltage.toDoubleOrNull()
            val vz = state.zenerVoltage.toDoubleOrNull()
            val rl = state.loadResistance.toDoubleOrNull()
            val pzMax = state.zenerPower.toDoubleOrNull()

            if (vin == null || vz == null || rl == null || pzMax == null) {
                _uiState.update { it.copy(result = null, error = null) }
                return@launch
            }

            if (vin <= vz) {
                _uiState.update { it.copy(result = null, error = "Source voltage must be greater than Zener voltage.") }
                return@launch
            }

            val il = vz / rl
            val izMax = pzMax / vz
            val izMin = 0.1 * izMax // Rule of thumb for minimum Zener current

            val rs = (vin - vz) / (il + izMin)
            val pr = (vin - vz).pow(2) / rs
            val iz = (vin - vz) / rs - il


            if (iz > izMax) {
                _uiState.update { it.copy(result = null, error = "Zener current exceeds maximum rating. Increase series resistor.") }
                return@launch
            }
            if (iz < 0){
                _uiState.update { it.copy(result = null, error = "Load resistance is too low, Zener is off.") }
                return@launch
            }


            _uiState.update {
                it.copy(
                    result = ZenerResult(
                        seriesResistor = rs,
                        resistorPower = pr,
                        loadCurrent = il,
                        zenerCurrent = iz
                    ),
                    error = null
                )
            }
        }
    }
}

// --- UI ---

@Composable
fun ZenerDiodeCalculatorScreen(viewModel: ZenerDiodeViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(stringResource(R.string.zener_diode_calculator_title), style = MaterialTheme.typography.headlineSmall)

        // --- Refactoring Step 3: Update UI calls to pass the sealed class instance ---
        NumericTextField(value = uiState.sourceVoltage, onValueChange = { viewModel.onValueChange(ZenerField.SourceVoltage, it) }, label = stringResource(R.string.source_voltage_v))
        NumericTextField(value = uiState.zenerVoltage, onValueChange = { viewModel.onValueChange(ZenerField.ZenerVoltage, it) }, label = stringResource(R.string.zener_voltage_v))
        NumericTextField(value = uiState.loadResistance, onValueChange = { viewModel.onValueChange(ZenerField.LoadResistance, it) }, label = stringResource(R.string.load_resistance_ohm))
        NumericTextField(value = uiState.zenerPower, onValueChange = { viewModel.onValueChange(ZenerField.ZenerPower, it) }, label = stringResource(R.string.zener_power_rating_w))

        // --- Result or Error ---
        uiState.error?.let {
            Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodyLarge)
        }

        uiState.result?.let { res ->
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(stringResource(R.string.results_title), style = MaterialTheme.typography.titleLarge)
                    ElectronicsResultRow(stringResource(R.string.series_resistor_rs), "${formatDouble(res.seriesResistor, "#.###")} Ω")
                    ElectronicsResultRow(stringResource(R.string.resistor_power_pr), "${formatDouble(res.resistorPower, "#.###")} W")
                    ElectronicsResultRow(stringResource(R.string.load_current_il), "${formatDouble(res.loadCurrent * 1000, "#.###")} mA")
                    ElectronicsResultRow(stringResource(R.string.zener_current_iz), "${formatDouble(res.zenerCurrent * 1000, "#.###")} mA")
                }
            }
        }
    }
}
