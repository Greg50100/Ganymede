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
import kotlin.math.acos
import kotlin.math.sin
import kotlin.math.sqrt

enum class PhaseType { Single, Three }

data class PowerAcUiState(
    val phaseType: PhaseType = PhaseType.Single,
    val voltage: String = "230",
    val current: String = "10",
    val powerFactor: String = "0.85",
    val apparentPower: Double? = null,
    val realPower: Double? = null,
    val reactivePower: Double? = null,
    val error: String? = null
)

class PowerAcViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(PowerAcUiState())
    val uiState = _uiState.asStateFlow()

    init {
        calculate()
    }

    fun onValueChange(field: String, value: String) {
        _uiState.update {
            when (field) {
                "voltage" -> it.copy(voltage = value)
                "current" -> it.copy(current = value)
                "pf" -> it.copy(powerFactor = value)
                else -> it
            }
        }
        calculate()
    }

    fun onPhaseChange(phaseType: PhaseType) {
        _uiState.update { it.copy(phaseType = phaseType) }
        calculate()
    }

    private fun calculate() {
        viewModelScope.launch {
            val s = _uiState.value
            val v = s.voltage.toDoubleOrNull()
            val i = s.current.toDoubleOrNull()
            val pf = s.powerFactor.toDoubleOrNull()

            if (v == null || i == null || pf == null) {
                _uiState.update { it.copy(apparentPower = null, realPower = null, reactivePower = null, error = null) }
                return@launch
            }
            if (pf < -1 || pf > 1) {
                _uiState.update { it.copy(error = "Le facteur de puissance doit être entre -1 et 1.") }
                return@launch
            }

            val phaseMultiplier = if (s.phaseType == PhaseType.Three) sqrt(3.0) else 1.0

            val apparent = phaseMultiplier * v * i
            val real = apparent * pf
            val phi = acos(pf)
            val reactive = apparent * sin(phi)

            _uiState.update {
                it.copy(
                    apparentPower = apparent,
                    realPower = real,
                    reactivePower = reactive,
                    error = null
                )
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PowerAcCalculatorScreen(viewModel: PowerAcViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val formatter = DecimalFormat("#.##")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(stringResource(id = R.string.ac_power_calculator_title), style = MaterialTheme.typography.headlineSmall)

        SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
            SegmentedButton(
                selected = uiState.phaseType == PhaseType.Single,
                onClick = { viewModel.onPhaseChange(PhaseType.Single) },
                shape = SegmentedButtonDefaults.itemShape(0, 2)
            ) { Text("Monophasé") }
            SegmentedButton(
                selected = uiState.phaseType == PhaseType.Three,
                onClick = { viewModel.onPhaseChange(PhaseType.Three) },
                shape = SegmentedButtonDefaults.itemShape(1, 2)
            ) { Text("Triphasé") }
        }

        OutlinedTextField(
            value = uiState.voltage,
            onValueChange = { viewModel.onValueChange("voltage", it) },
            label = { Text(if (uiState.phaseType == PhaseType.Single) "Tension (V)" else "Tension Ligne-Ligne (V)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = uiState.current,
            onValueChange = { viewModel.onValueChange("current", it) },
            label = { Text("Courant (A)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = uiState.powerFactor,
            onValueChange = { viewModel.onValueChange("pf", it) },
            label = { Text("Facteur de Puissance (cos φ)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth(),
            isError = uiState.error != null
        )
        uiState.error?.let {
            Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
        }


        if (uiState.realPower != null) {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    PowerResultRow("Puissance Réelle (P)", uiState.realPower, "W")
                    PowerResultRow("Puissance Apparente (S)", uiState.apparentPower, "VA")
                    PowerResultRow("Puissance Réactive (Q)", uiState.reactivePower, "VAR")
                }
            }
        }
    }
}

@Composable
private fun PowerResultRow(label: String, value: Double?, unit: String) {
    val formatter = DecimalFormat("#.##")
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodyLarge)
        Text(
            text = "${formatter.format(value)} $unit",
            style = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.primary)
        )
    }
}
