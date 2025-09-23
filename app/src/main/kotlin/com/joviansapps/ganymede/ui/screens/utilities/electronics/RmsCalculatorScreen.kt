package com.joviansapps.ganymede.ui.screens.utilities.electronics

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
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
import com.joviansapps.ganymede.ui.components.ResultField
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.DecimalFormat
import kotlin.math.sqrt

enum class WaveformType { SINE, SQUARE, TRIANGLE }

data class RmsUiState(
    val waveformType: WaveformType = WaveformType.SINE,
    val peakValue: String = "10",
    val rmsValue: Double? = null
)

class RmsViewModel: ViewModel() {
    private val _uiState = MutableStateFlow(RmsUiState())
    val uiState = _uiState.asStateFlow()

    init { calculate() }

    fun onValueChange(value: String) {
        _uiState.update { it.copy(peakValue = value) }
        calculate()
    }

    fun onWaveformChange(type: WaveformType) {
        _uiState.update { it.copy(waveformType = type) }
        calculate()
    }

    private fun calculate() {
        viewModelScope.launch {
            val peak = _uiState.value.peakValue.toDoubleOrNull()
            if (peak == null || peak < 0) {
                _uiState.update { it.copy(rmsValue = null) }
                return@launch
            }

            val rms = when (_uiState.value.waveformType) {
                WaveformType.SINE -> peak / sqrt(2.0)
                WaveformType.SQUARE -> peak
                WaveformType.TRIANGLE -> peak / sqrt(3.0)
            }
            _uiState.update { it.copy(rmsValue = rms) }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RmsCalculatorScreen(viewModel: RmsViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val formatter = remember { DecimalFormat("#.###") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(stringResource(id = R.string.rms_calculator_title), style = MaterialTheme.typography.headlineSmall)

        SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
            SegmentedButton(selected = uiState.waveformType == WaveformType.SINE, onClick = { viewModel.onWaveformChange(WaveformType.SINE) }, shape = SegmentedButtonDefaults.itemShape(0, 3)) { Text("Sinus") }
            SegmentedButton(selected = uiState.waveformType == WaveformType.SQUARE, onClick = { viewModel.onWaveformChange(WaveformType.SQUARE) }, shape = SegmentedButtonDefaults.itemShape(1, 3)) { Text("Carré") }
            SegmentedButton(selected = uiState.waveformType == WaveformType.TRIANGLE, onClick = { viewModel.onWaveformChange(WaveformType.TRIANGLE) }, shape = SegmentedButtonDefaults.itemShape(2, 3)) { Text("Triangle") }
        }

        OutlinedTextField(
            value = uiState.peakValue,
            onValueChange = viewModel::onValueChange,
            label = { Text("Tension Crête (Vp)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        if (uiState.rmsValue != null) {
            ResultField(
                label = "Tension Efficace (Vrms)",
                value = uiState.rmsValue?.let { formatter.format(it) } ?: "N/A",
                unit = "V"
            )
        }
    }
}
