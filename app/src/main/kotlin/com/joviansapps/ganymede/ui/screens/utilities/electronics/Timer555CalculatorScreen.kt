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
import com.joviansapps.ganymede.ui.components.ResultRow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.DecimalFormat
import kotlin.math.ln

data class Timer555UiState(
    val r1: String = "", // kΩ
    val r2: String = "", // kΩ
    val c1: String = "", // µF
    val frequency: Double? = null,
    val period: Double? = null,
    val dutyCycle: Double? = null,
    val timeHigh: Double? = null,
    val timeLow: Double? = null
)

class Timer555ViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(Timer555UiState())
    val uiState = _uiState.asStateFlow()

    fun onValueChange(field: String, value: String) {
        _uiState.update {
            when (field) {
                "r1" -> it.copy(r1 = value)
                "r2" -> it.copy(r2 = value)
                "c1" -> it.copy(c1 = value)
                else -> it
            }
        }
        calculate()
    }

    private fun calculate() {
        viewModelScope.launch {
            val state = _uiState.value
            val r1_kOhm = state.r1.toDoubleOrNull()
            val r2_kOhm = state.r2.toDoubleOrNull()
            val c1_uF = state.c1.toDoubleOrNull()

            if (r1_kOhm != null && r2_kOhm != null && c1_uF != null && r1_kOhm > 0 && r2_kOhm > 0 && c1_uF > 0) {
                val r1_Ohm = r1_kOhm * 1000
                val r2_Ohm = r2_kOhm * 1000
                val c1_F = c1_uF / 1_000_000

                val timeHigh = ln(2.0) * (r1_Ohm + r2_Ohm) * c1_F
                val timeLow = ln(2.0) * r2_Ohm * c1_F
                val period = timeHigh + timeLow
                val frequency = 1 / period
                val dutyCycle = (timeHigh / period) * 100

                _uiState.update {
                    it.copy(
                        frequency = frequency,
                        period = period,
                        dutyCycle = dutyCycle,
                        timeHigh = timeHigh,
                        timeLow = timeLow
                    )
                }
            } else {
                _uiState.update {
                    it.copy(
                        frequency = null,
                        period = null,
                        dutyCycle = null,
                        timeHigh = null,
                        timeLow = null
                    )
                }
            }
        }
    }
}


@Composable
fun Timer555CalculatorScreen(viewModel: Timer555ViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val formatter = DecimalFormat("#.####")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(stringResource(R.string.timer_555_astable_title), style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(16.dp))

        Image(
            painter = painterResource(id = R.drawable.ic_555_astable_circuit),
            contentDescription = stringResource(R.string.timer_555_astable_circuit_desc),
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
        )
        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = uiState.r1,
            onValueChange = { viewModel.onValueChange("r1", it) },
            label = { Text(stringResource(R.string.resistance_r1_kohm)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = uiState.r2,
            onValueChange = { viewModel.onValueChange("r2", it) },
            label = { Text(stringResource(R.string.resistance_r2_kohm)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = uiState.c1,
            onValueChange = { viewModel.onValueChange("c1", it) },
            label = { Text(stringResource(R.string.capacitance_c1_uf)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(16.dp))

        if (uiState.frequency != null) {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(stringResource(R.string.results_title), style = MaterialTheme.typography.titleLarge)
                    Divider(modifier = Modifier.padding(vertical = 8.dp))
                    ResultRow(label = stringResource(R.string.frequency_label), value = "${formatter.format(uiState.frequency)} Hz")
                    ResultRow(label = stringResource(R.string.period_label), value = "${formatter.format(uiState.period)} s")
                    ResultRow(label = stringResource(R.string.duty_cycle_label), value = "${formatter.format(uiState.dutyCycle)} %")
                    ResultRow(label = stringResource(R.string.time_high_label), value = "${formatter.format(uiState.timeHigh)} s")
                    ResultRow(label = stringResource(R.string.time_low_label), value = "${formatter.format(uiState.timeLow)} s")
                }
            }
        }
    }
}
