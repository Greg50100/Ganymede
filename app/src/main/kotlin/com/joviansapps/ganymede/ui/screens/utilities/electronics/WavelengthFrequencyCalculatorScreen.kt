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
import com.joviansapps.ganymede.ui.components.NumericTextField
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class WavelengthUiState(
    val frequency: String = "",
    val wavelength: String = ""
)

class WavelengthViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(WavelengthUiState())
    val uiState = _uiState.asStateFlow()
    private val speedOfLight = 299792458.0 // m/s
    private var lastEdited: Char? = null

    fun onFrequencyChange(value: String) { _uiState.update { it.copy(frequency = value) }; lastEdited = 'f'; calculate() }
    fun onWavelengthChange(value: String) { _uiState.update { it.copy(wavelength = value) }; lastEdited = 'w'; calculate() }

    private fun calculate() {
        viewModelScope.launch {
            val freq = _uiState.value.frequency.toDoubleOrNull()
            val wave = _uiState.value.wavelength.toDoubleOrNull()

            when(lastEdited) {
                'f' -> if (freq != null && freq > 0) _uiState.update { it.copy(wavelength = (speedOfLight / freq).toString()) }
                'w' -> if (wave != null && wave > 0) _uiState.update { it.copy(frequency = (speedOfLight / wave).toString()) }
            }
        }
    }
}

@Composable
fun WavelengthFrequencyCalculatorScreen(viewModel: WavelengthViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp).verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(stringResource(R.string.wavelength_calculator_title), style = MaterialTheme.typography.headlineSmall)
        NumericTextField(value = uiState.frequency, onValueChange = viewModel::onFrequencyChange, label = stringResource(R.string.frequency_hz), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth())
        NumericTextField(value = uiState.wavelength, onValueChange = viewModel::onWavelengthChange, label = stringResource(R.string.wavelength_m), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth())
    }
}
