package com.joviansapps.ganymede.ui.screens.utilities.physics

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.joviansapps.ganymede.ui.components.NumericTextField
import com.joviansapps.ganymede.ui.components.formatDouble
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.math.sqrt

// --- Constants ---
private const val GRAVITY = 9.80665 // Standard gravity in m/s²

// --- 1. UI State ---
data class FreeFallUiState(
    val heightInput: String = "",
    val finalVelocity: Double? = null,
    val time: Double? = null,
    val error: String? = null
)

// --- 2. ViewModel ---
class FreeFallViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(FreeFallUiState())
    val uiState = _uiState.asStateFlow()

    fun onHeightChanged(height: String) {
        _uiState.update { it.copy(heightInput = height) }
    }

    fun calculate() {
        viewModelScope.launch {
            val height = _uiState.value.heightInput.toDoubleOrNull()
            if (height == null || height < 0) {
                _uiState.update {
                    it.copy(
                        error = "Please enter a valid positive height.",
                        finalVelocity = null,
                        time = null
                    )
                }
                return@launch
            }

            val finalVelocityResult = sqrt(2 * GRAVITY * height)
            val timeResult = sqrt(2 * height / GRAVITY)

            _uiState.update {
                it.copy(
                    error = null,
                    finalVelocity = finalVelocityResult,
                    time = timeResult
                )
            }
        }
    }
}

// --- 3. Composable Screen ---
@Composable
fun FreeFallCalculatorScreen(viewModel: FreeFallViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = stringResource(R.string.free_fall_calculator_description),
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        NumericTextField(
            value = uiState.heightInput,
            onValueChange = viewModel::onHeightChanged,
            label = stringResource(R.string.height_m_label),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = { viewModel.calculate() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.calculate_button))
        }

        uiState.error?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 16.dp)
            )
        }

        // Use a state-driven approach to show results
        if (uiState.finalVelocity != null && uiState.time != null) {
            Results(finalVelocity = uiState.finalVelocity!!, time = uiState.time!!)
        }
    }
}

@Composable
private fun Results(finalVelocity: Double, time: Double) {
    Column(modifier = Modifier.padding(top = 16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = stringResource(R.string.results_title),
            style = MaterialTheme.typography.titleLarge,
        )
        ResultField(
            label = stringResource(R.string.final_velocity_label),
            value = formatDouble(finalVelocity, "#.##"),
            unit = "m/s"
        )
        ResultField(
            label = stringResource(R.string.time_of_fall_label),
            value = formatDouble(time, "#.##"),
            unit = "s"
        )
    }
}
