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
import com.joviansapps.ganymede.ui.screens.utilities.electronics.Complex
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.DecimalFormat
import kotlin.math.*

enum class ImpedanceCircuitType { SERIES, PARALLEL }
enum class ImpedanceComponentType { RC, RL, LC, RLC }

data class RlcImpedanceUiState(
    val circuitType: ImpedanceCircuitType = ImpedanceCircuitType.SERIES,
    val componentType: ImpedanceComponentType = ImpedanceComponentType.RLC,
    val resistance: String = "1000",
    val capacitance: String = "100n",
    val inductance: String = "10m",
    val frequency: String = "1000",
    val impedance: Complex? = null
)

class RlcImpedanceViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(RlcImpedanceUiState())
    val uiState = _uiState.asStateFlow()

    init {
        calculate()
    }

    fun onValueChange(field: String, value: String) {
        _uiState.update {
            when (field) {
                "r" -> it.copy(resistance = value)
                "c" -> it.copy(capacitance = value)
                "l" -> it.copy(inductance = value)
                "f" -> it.copy(frequency = value)
                else -> it
            }
        }
        calculate()
    }

    fun onCircuitTypeChange(type: ImpedanceCircuitType) {
        _uiState.update { it.copy(circuitType = type) }
        calculate()
    }

    fun onComponentTypeChange(type: ImpedanceComponentType) {
        _uiState.update { it.copy(componentType = type) }
        calculate()
    }

    // Helper to parse values with metric prefixes
    private fun parseValue(str: String): Double? {
        val valueString = str.lowercase().trim()
        if (valueString.isEmpty()) return null

        var multiplier = 1.0
        val lastChar = valueString.last()

        if (!lastChar.isDigit()) {
            when (lastChar) {
                'p' -> multiplier = 1e-12
                'n' -> multiplier = 1e-9
                'u', 'µ' -> multiplier = 1e-6
                'm' -> multiplier = 1e-3
                'k' -> multiplier = 1e3
                'g' -> multiplier = 1e9
                'm' -> multiplier = 1e6 // Note: 'M' for Mega, 'm' for milli
            }
            return valueString.dropLast(1).toDoubleOrNull()?.let { it * multiplier }
        }
        return valueString.toDoubleOrNull()
    }


    private fun calculate() {
        viewModelScope.launch {
            val s = _uiState.value
            val r = parseValue(s.resistance)
            val c = parseValue(s.capacitance)
            val l = parseValue(s.inductance)
            val f = parseValue(s.frequency)

            if (f == null || f == 0.0) {
                _uiState.update { it.copy(impedance = null) }
                return@launch
            }

            val omega = 2 * PI * f

            val zR = if (s.componentType == ImpedanceComponentType.LC || r == null) Complex(0.0, 0.0) else Complex(r, 0.0)
            val zC = if (c == null || c == 0.0 || s.componentType == ImpedanceComponentType.RL) Complex() else Complex(0.0, -1 / (omega * c))
            val zL = if (l == null || l == 0.0 || s.componentType == ImpedanceComponentType.RC) Complex() else Complex(0.0, omega * l)

            val totalImpedance = when (s.circuitType) {
                ImpedanceCircuitType.SERIES -> {
                    zR + zL + zC
                }
                ImpedanceCircuitType.PARALLEL -> {
                    val one = Complex(1.0, 0.0)
                    var yTotal = Complex(0.0, 0.0)
                    if (r != null && r != 0.0 && s.componentType != ImpedanceComponentType.LC) yTotal += one / zR
                    if (l != null && l != 0.0 && s.componentType != ImpedanceComponentType.RC) yTotal += one / zL
                    if (c != null && c != 0.0 && s.componentType != ImpedanceComponentType.RL) yTotal += one / zC

                    if(yTotal.real == 0.0 && yTotal.imag == 0.0) Complex() else one / yTotal
                }
            }
            _uiState.update { it.copy(impedance = totalImpedance) }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RlcImpedanceCalculatorScreen(viewModel: RlcImpedanceViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(stringResource(id = R.string.rlc_impedance_calculator_title), style = MaterialTheme.typography.headlineSmall)

        SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
            SegmentedButton(selected = uiState.circuitType == ImpedanceCircuitType.SERIES, onClick = { viewModel.onCircuitTypeChange(ImpedanceCircuitType.SERIES) }, shape = SegmentedButtonDefaults.itemShape(0, 2)) { Text("Série") }
            SegmentedButton(selected = uiState.circuitType == ImpedanceCircuitType.PARALLEL, onClick = { viewModel.onCircuitTypeChange(ImpedanceCircuitType.PARALLEL) }, shape = SegmentedButtonDefaults.itemShape(1, 2)) { Text("Parallèle") }
        }

        SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
            SegmentedButton(selected = uiState.componentType == ImpedanceComponentType.RC, onClick = { viewModel.onComponentTypeChange(ImpedanceComponentType.RC) }, shape = SegmentedButtonDefaults.itemShape(0, 4)) { Text("RC") }
            SegmentedButton(selected = uiState.componentType == ImpedanceComponentType.RL, onClick = { viewModel.onComponentTypeChange(ImpedanceComponentType.RL) }, shape = SegmentedButtonDefaults.itemShape(1, 4)) { Text("RL") }
            SegmentedButton(selected = uiState.componentType == ImpedanceComponentType.LC, onClick = { viewModel.onComponentTypeChange(ImpedanceComponentType.LC) }, shape = SegmentedButtonDefaults.itemShape(2, 4)) { Text("LC") }
            SegmentedButton(selected = uiState.componentType == ImpedanceComponentType.RLC, onClick = { viewModel.onComponentTypeChange(ImpedanceComponentType.RLC) }, shape = SegmentedButtonDefaults.itemShape(3, 4)) { Text("RLC") }
        }

        if (uiState.componentType in listOf(ImpedanceComponentType.RC, ImpedanceComponentType.RL, ImpedanceComponentType.RLC)) {
            OutlinedTextField(value = uiState.resistance, onValueChange = { viewModel.onValueChange("r", it) }, label = { Text("Résistance (Ω)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text), modifier = Modifier.fillMaxWidth())
        }
        if (uiState.componentType in listOf(ImpedanceComponentType.RC, ImpedanceComponentType.LC, ImpedanceComponentType.RLC)) {
            OutlinedTextField(value = uiState.capacitance, onValueChange = { viewModel.onValueChange("c", it) }, label = { Text("Capacité (F)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text), modifier = Modifier.fillMaxWidth())
        }
        if (uiState.componentType in listOf(ImpedanceComponentType.RL, ImpedanceComponentType.LC, ImpedanceComponentType.RLC)) {
            OutlinedTextField(value = uiState.inductance, onValueChange = { viewModel.onValueChange("l", it) }, label = { Text("Inductance (H)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text), modifier = Modifier.fillMaxWidth())
        }
        OutlinedTextField(value = uiState.frequency, onValueChange = { viewModel.onValueChange("f", it) }, label = { Text("Fréquence (Hz)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text), modifier = Modifier.fillMaxWidth())

        uiState.impedance?.let {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    val magnitude = sqrt(it.real.pow(2) + it.imag.pow(2))
                    val phase = atan2(it.imag, it.real) * 180 / PI

                    ImpedanceResultRow("Impédance (Z)", magnitude, "Ω")
                    ImpedanceResultRow("Angle de Phase (φ)", phase, "°")
                    Text("Forme Rectangulaire: ${it.toString()} Ω", style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}

@Composable
private fun ImpedanceResultRow(label: String, value: Double?, unit: String) {
    val formatter = DecimalFormat("#.###")
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
