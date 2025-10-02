package com.joviansapps.ganymede.ui.screens.utilities.electronics

import androidx.compose.foundation.Image
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.focusable
import androidx.compose.foundation.clickable
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
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
import java.text.DecimalFormat


// --- State and ViewModel ---

enum class OpAmpConfig { Inverting, NonInverting, Differential }

data class OpAmpUiState(
    val config: OpAmpConfig = OpAmpConfig.Inverting,
    val r1: String = "1",
    val rf: String = "10",
    val vin: String = "1",
    val vin2: String = "2", // For differential config
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
                "vin2" -> it.copy(vin2 = value)
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
            val vin2 = state.vin2.toDoubleOrNull()

            if (r1 == null || rf == null || r1 <= 0) {
                _uiState.update { it.copy(result = null) }
                return@launch
            }

            val result = when (state.config) {
                OpAmpConfig.Inverting -> {
                    if (vin == null) {
                        _uiState.update { it.copy(result = null) }; return@launch
                    }
                    val gain = -rf / r1
                    val vout = gain * vin
                    OpAmpResult(gain, vout)
                }

                OpAmpConfig.NonInverting -> {
                    if (vin == null) {
                        _uiState.update { it.copy(result = null) }; return@launch
                    }
                    val gain = 1 + (rf / r1)
                    val vout = gain * vin
                    OpAmpResult(gain, vout)
                }

                OpAmpConfig.Differential -> {
                    if (vin == null || vin2 == null) {
                        _uiState.update { it.copy(result = null) }; return@launch
                    }
                    val gain = rf / r1
                    val vout = gain * (vin2 - vin)
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
@Preview
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
        // Remplacement du segmented button par un spinner (ExposedDropdownMenuBox)
        var expanded by remember { mutableStateOf(false) }
        val options = OpAmpConfig.values()

        ExposedDropdownMenuBox(
            expanded = expanded,
            // toggle l'état lors d'un changement (ouvre/ferme) — pattern recommandé
            onExpandedChange = { expanded = !expanded },
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = uiState.config.name,
                onValueChange = {},
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier
                    .menuAnchor(MenuAnchorType.PrimaryNotEditable, enabled = true)
                    .fillMaxWidth(),
                singleLine = true
            )

            // Appliquer un fond cohérent avec le thème au menu étendu
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface, shape = MaterialTheme.shapes.small)
            ) {
                options.forEach { config ->
                    val isSelected = uiState.config == config
                    DropdownMenuItem(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(if (isSelected) MaterialTheme.colorScheme.primaryContainer else Color.Transparent),
                        text = {
                            Text(
                                config.name,
                                style = MaterialTheme.typography.bodyMedium,
                                color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
                            )
                        },
                        leadingIcon = {
                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Filled.Check,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        },
                        onClick = {
                            viewModel.onConfigChange(config)
                            expanded = false
                        }
                    )
                }
            }
        }

        val imageRes = when (uiState.config) {
            OpAmpConfig.Inverting -> R.drawable.opampinverting
            OpAmpConfig.NonInverting -> R.drawable.opampnoninverting
            OpAmpConfig.Differential -> R.drawable.opampdifferential
        }

        Image(
            painter = painterResource(id = imageRes),
            contentDescription = stringResource(id = R.string.op_amp_circuit_diagram),
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .fillMaxWidth(),
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary)
        )

        if (uiState.config == OpAmpConfig.Differential) {
            OutlinedTextField(
                value = uiState.vin,
                onValueChange = { viewModel.onValueChange("vin", it) },
                label = { Text("Input Voltage (Vin1)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = uiState.vin2,
                onValueChange = { viewModel.onValueChange("vin2", it) },
                label = { Text("Input Voltage (Vin2)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
        } else {
            OutlinedTextField(
                value = uiState.vin,
                onValueChange = { viewModel.onValueChange("vin", it) },
                label = { Text(stringResource(R.string.input_voltage_vin)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
        }

        OutlinedTextField(
            value = uiState.r1,
            onValueChange = { viewModel.onValueChange("r1", it) },
            label = { Text(stringResource(R.string.resistance_r1_kohm)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = uiState.rf,
            onValueChange = { viewModel.onValueChange("rf", it) },
            label = { Text(stringResource(R.string.feedback_resistance_rf_kohm)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        if (uiState.config == OpAmpConfig.Differential) {
            Text(
                text = "Note: This circuit assumes R2=R1 and R4=Rf.",
                style = MaterialTheme.typography.bodySmall
            )
        }


        uiState.result?.let {
            // Afficher les résultats en utilisant le composant réutilisable ResultRow
            val resultsText =
                "${stringResource(R.string.voltage_gain)}: ${formatter.format(it.gain)}\n${
                    stringResource(R.string.output_voltage_vout_short)
                }: ${formatter.format(it.vout)} V"

            ResultField(
                label = stringResource(R.string.results_title),
                value = resultsText,
                modifier = Modifier.fillMaxWidth(),
                maxLines = 5
            )
        }
    }
}