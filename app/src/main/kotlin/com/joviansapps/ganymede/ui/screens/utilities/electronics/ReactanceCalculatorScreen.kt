package com.joviansapps.ganymede.ui.screens.utilities.electronics

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
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
import kotlin.math.PI

// --- State, Enums, and ViewModel ---

enum class ReactanceType { Capacitive, Inductive }

// --- Refactoring Step 1: Define a sealed class for input fields ---
sealed class ReactanceField {
    data object Frequency : ReactanceField()
    data object Capacitance : ReactanceField()
    data object Inductance : ReactanceField()
}

enum class FrequencyUnit(override val symbol: String, val multiplier: Double) : EnumSymbolProvider {
    Hz("Hz", 1.0),
    kHz("kHz", 1e3),
    MHz("MHz", 1e6),
    GHz("GHz", 1e9)
}

enum class InductanceUnit(override val symbol: String, val multiplier: Double) : EnumSymbolProvider {
    µH("µH", 1e-6),
    mH("mH", 1e-3),
    H("H", 1.0)
}

data class ReactanceUiState(
    val frequency: String = "1",
    val frequencyUnit: FrequencyUnit = FrequencyUnit.kHz,
    val capacitance: String = "1",
    val capacitanceUnit: CapacitanceUnit = CapacitanceUnit.µF,
    val inductance: String = "10",
    val inductanceUnit: InductanceUnit = InductanceUnit.mH,
    val type: ReactanceType = ReactanceType.Capacitive,
    val result: Double? = null
)

class ReactanceViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(ReactanceUiState())
    val uiState = _uiState.asStateFlow()

    init {
        calculate()
    }

    // --- Refactoring Step 2: Update the event handler to use the sealed class ---
    fun onValueChange(field: ReactanceField, value: String) {
        _uiState.update {
            when (field) {
                is ReactanceField.Frequency -> it.copy(frequency = value)
                is ReactanceField.Capacitance -> it.copy(capacitance = value)
                is ReactanceField.Inductance -> it.copy(inductance = value)
            }
        }
        calculate()
    }

    fun onTypeChange(type: ReactanceType) {
        _uiState.update { it.copy(type = type) }
        calculate()
    }

    fun onFrequencyUnitChange(unit: FrequencyUnit) {
        _uiState.update { it.copy(frequencyUnit = unit) }
        calculate()
    }
    fun onCapacitanceUnitChange(unit: CapacitanceUnit) {
        _uiState.update { it.copy(capacitanceUnit = unit) }
        calculate()
    }
    fun onInductanceUnitChange(unit: InductanceUnit) {
        _uiState.update { it.copy(inductanceUnit = unit) }
        calculate()
    }

    private fun calculate() {
        viewModelScope.launch {
            val state = _uiState.value
            val fValue = state.frequency.toDoubleOrNull()
            val cValue = state.capacitance.toDoubleOrNull()
            val lValue = state.inductance.toDoubleOrNull()

            if (fValue == null || fValue <= 0) {
                _uiState.update { it.copy(result = null) }
                return@launch
            }
            val f = fValue * state.frequencyUnit.multiplier

            val reactance = when (state.type) {
                ReactanceType.Capacitive -> {
                    if (cValue == null || cValue <= 0) {
                        _uiState.update { it.copy(result = null) }; return@launch
                    }
                    val cFarads = cValue * state.capacitanceUnit.multiplier
                    1 / (2 * PI * f * cFarads)
                }
                ReactanceType.Inductive -> {
                    if (lValue == null || lValue <= 0) {
                        _uiState.update { it.copy(result = null) }; return@launch
                    }
                    val lHenrys = lValue * state.inductanceUnit.multiplier
                    2 * PI * f * lHenrys
                }
            }
            _uiState.update { it.copy(result = reactance) }
        }
    }
}

// --- UI ---

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReactanceCalculatorScreen(viewModel: ReactanceViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(stringResource(R.string.reactance_calculator_title), style = MaterialTheme.typography.headlineSmall)

        // --- Type Selector ---
        SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
            ReactanceType.values().forEach { type ->
                SegmentedButton(
                    selected = uiState.type == type,
                    onClick = { viewModel.onTypeChange(type) },
                    shape = SegmentedButtonDefaults.itemShape(type.ordinal, ReactanceType.values().size)
                ) {
                    Text(if (type == ReactanceType.Capacitive) stringResource(R.string.capacitive_reactance_xc) else stringResource(R.string.inductive_reactance_xl))
                }
            }
        }

        // --- Refactoring Step 3: Update UI calls to pass the sealed class instance ---
        UnitInputField(
            label = stringResource(R.string.frequency_label),
            value = uiState.frequency,
            onValueChange = { viewModel.onValueChange(ReactanceField.Frequency, it) },
            selectedUnit = uiState.frequencyUnit,
            units = FrequencyUnit.values().toList(),
            onUnitSelected = viewModel::onFrequencyUnitChange
        )

        UnitInputField(
            label = stringResource(R.string.capacitance_label),
            value = uiState.capacitance,
            onValueChange = { viewModel.onValueChange(ReactanceField.Capacitance, it) },
            selectedUnit = uiState.capacitanceUnit,
            units = CapacitanceUnit.values().toList(),
            onUnitSelected = viewModel::onCapacitanceUnitChange,
            enabled = uiState.type == ReactanceType.Capacitive
        )

        UnitInputField(
            label = stringResource(R.string.inductance_label),
            value = uiState.inductance,
            onValueChange = { viewModel.onValueChange(ReactanceField.Inductance, it) },
            selectedUnit = uiState.inductanceUnit,
            units = InductanceUnit.values().toList(),
            onUnitSelected = viewModel::onInductanceUnitChange,
            enabled = uiState.type == ReactanceType.Inductive
        )

        // --- Result ---
        uiState.result?.let {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(stringResource(R.string.results_title), style = MaterialTheme.typography.titleLarge)
                    Text(
                        text = formatResult(it),
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun <U> UnitInputField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    selectedUnit: U,
    units: List<U>,
    onUnitSelected: (U) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) where U : Enum<U>, U : EnumSymbolProvider {
    var expanded by remember { mutableStateOf(false) }

    Row(modifier = modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(label) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.weight(1f),
            singleLine = true,
            enabled = enabled
        )
        Spacer(modifier = Modifier.width(8.dp))
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { if (enabled) expanded = !expanded }
        ) {
            OutlinedTextField(
                value = selectedUnit.symbol,
                onValueChange = {},
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier.menuAnchor().width(100.dp),
                enabled = enabled
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                units.forEach { unit ->
                    DropdownMenuItem(
                        text = { Text(unit.symbol) },
                        onClick = {
                            onUnitSelected(unit)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

private fun formatResult(value: Double?): String {
    if (value == null) return "N/A"
    val formatter = when {
        value > 1e6 -> DecimalFormat("0.##E0") // Scientific for large numbers
        value < 1e-3 && value > 0 -> DecimalFormat("0.##E0") // Scientific for small numbers
        else -> DecimalFormat("#.###")
    }
    return "${formatter.format(value)} Ω"
}
