package com.joviansapps.ganymede.ui.screens.utilities.electronics

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.joviansapps.ganymede.R
import com.joviansapps.ganymede.ui.components.ResultField
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.DecimalFormat

enum class Timer555Mode { ASTABLE, MONOSTABLE }

data class Timer555UiState(
    val mode: Timer555Mode = Timer555Mode.ASTABLE,
    val r1: String = "10k",
    val r2: String = "47k",
    val r: String = "10k", // For monostable
    val c: String = "10u",
    val frequency: Double? = null,
    val dutyCycle: Double? = null,
    val highTime: Double? = null,
    val lowTime: Double? = null,
    val period: Double? = null, // Also monostable pulse width
)

class Timer555ViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(Timer555UiState())
    val uiState: StateFlow<Timer555UiState> = _uiState.asStateFlow()

    init {
        calculate()
    }

    fun onValueChange(value: String, field: String) {
        _uiState.update {
            when (field) {
                "r1" -> it.copy(r1 = value)
                "r2" -> it.copy(r2 = value)
                "r" -> it.copy(r = value)
                "c" -> it.copy(c = value)
                else -> it
            }
        }
        calculate()
    }

    fun onModeChange(mode: Timer555Mode) {
        _uiState.update { it.copy(mode = mode) }
        calculate()
    }

    private fun parseWithMultiplier(value: String): Double? {
        val cleanedValue = value.trim().lowercase()
        if (cleanedValue.isEmpty()) return null
        val multiplier = when {
            cleanedValue.endsWith("k") -> 1000.0
            cleanedValue.endsWith("m") -> 1_000_000.0
            cleanedValue.endsWith("u") -> 0.000_001
            cleanedValue.endsWith("n") -> 0.000_000_001
            cleanedValue.endsWith("p") -> 0.000_000_000_001
            else -> 1.0
        }
        val numberPart = if (multiplier != 1.0) cleanedValue.dropLast(1) else cleanedValue
        return numberPart.toDoubleOrNull()?.times(multiplier)
    }

    private fun calculate() {
        viewModelScope.launch {
            val state = _uiState.value
            if (state.mode == Timer555Mode.ASTABLE) {
                calculateAstable()
            } else {
                calculateMonostable()
            }
        }
    }

    private fun calculateAstable() {
        val r1 = parseWithMultiplier(_uiState.value.r1)
        val r2 = parseWithMultiplier(_uiState.value.r2)
        val c = parseWithMultiplier(_uiState.value.c)

        if (r1 != null && r2 != null && c != null && r1 > 0 && r2 > 0 && c > 0) {
            val period = 0.693 * (r1 + 2 * r2) * c
            val frequency = 1 / period
            val highTime = 0.693 * (r1 + r2) * c
            val lowTime = 0.693 * r2 * c
            val dutyCycle = (highTime / period) * 100

            _uiState.update {
                it.copy(
                    frequency = frequency,
                    dutyCycle = dutyCycle,
                    highTime = highTime,
                    lowTime = lowTime,
                    period = period
                )
            }
        } else {
            _uiState.update {
                it.copy(
                    frequency = null,
                    dutyCycle = null,
                    highTime = null,
                    lowTime = null,
                    period = null
                )
            }
        }
    }

    private fun calculateMonostable() {
        val r = parseWithMultiplier(_uiState.value.r)
        val c = parseWithMultiplier(_uiState.value.c)

        if (r != null && c != null && r > 0 && c > 0) {
            val pulseWidth = 1.1 * r * c
            _uiState.update {
                it.copy(
                    period = pulseWidth,
                    frequency = null, dutyCycle = null, highTime = null, lowTime = null
                )
            }
        } else {
            _uiState.update { it.copy(period = null) }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Timer555CalculatorScreen(vm: Timer555ViewModel = viewModel()) {
    val uiState by vm.uiState.collectAsStateWithLifecycle()
    val formatter = remember { DecimalFormat("#.###") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = stringResource(id = R.string.timer_555_astable_title),
            style = MaterialTheme.typography.headlineSmall
        )

        SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
            SegmentedButton(selected = uiState.mode == Timer555Mode.ASTABLE, onClick = { vm.onModeChange(Timer555Mode.ASTABLE) }, shape = SegmentedButtonDefaults.itemShape(0, 2)) { Text("Astable") }
            SegmentedButton(selected = uiState.mode == Timer555Mode.MONOSTABLE, onClick = { vm.onModeChange(Timer555Mode.MONOSTABLE) }, shape = SegmentedButtonDefaults.itemShape(1, 2)) { Text("Monostable") }
        }

        Image(
            painter = painterResource(id = R.drawable.ic_555_astable_circuit),
            contentDescription = "Circuit astable 555",
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        )

        if (uiState.mode == Timer555Mode.ASTABLE) {
            OutlinedTextField(
                value = uiState.r1,
                onValueChange = { vm.onValueChange(it, "r1") },
                label = { Text("Résistance R1 (Ω)") },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                ),
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = uiState.r2,
                onValueChange = { vm.onValueChange(it, "r2") },
                label = { Text("Résistance R2 (Ω)") },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                ),
                modifier = Modifier.fillMaxWidth()
            )
        } else {
            OutlinedTextField(
                value = uiState.r,
                onValueChange = { vm.onValueChange(it, "r") },
                label = { Text("Résistance R (Ω)") },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                ),
                modifier = Modifier.fillMaxWidth()
            )
        }

        OutlinedTextField(
            value = uiState.c,
            onValueChange = { vm.onValueChange(it, "c") },
            label = { Text("Capacité (F)") },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Done
            ),
            modifier = Modifier.fillMaxWidth()
        )

        if (uiState.frequency != null || uiState.period != null) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (uiState.mode == Timer555Mode.ASTABLE) {
                    ResultField(
                        label = "Fréquence",
                        value = uiState.frequency?.let { formatter.format(it) } ?: "N/A",
                        unit = "Hz"
                    )
                    ResultField(
                        label = "Cycle de service",
                        value = uiState.dutyCycle?.let { formatter.format(it) } ?: "N/A",
                        unit = "%"
                    )
                    ResultField(
                        label = "Temps Haut",
                        value = uiState.highTime?.let { formatter.format(it) } ?: "N/A",
                        unit = "s"
                    )
                    ResultField(
                        label = "Temps Bas",
                        value = uiState.lowTime?.let { formatter.format(it) } ?: "N/A",
                        unit = "s"
                    )
                    ResultField(
                        label = "Période",
                        value = uiState.period?.let { formatter.format(it) } ?: "N/A",
                        unit = "s"
                    )
                } else {
                    ResultField(
                        label = "Largeur d'Impulsion (T)",
                        value = uiState.period?.let { formatter.format(it) } ?: "N/A",
                        unit = "s"
                    )
                }
            }
        }
    }
}
