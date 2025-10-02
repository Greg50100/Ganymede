package com.joviansapps.ganymede.ui.screens.utilities.health

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
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
import com.joviansapps.ganymede.ui.components.ResultField
import com.joviansapps.ganymede.ui.components.NumericTextField
import com.joviansapps.ganymede.ui.components.formatDouble
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.math.log10

// --- 1. State and Data ---

data class BodyFatUiState(
    val gender: BmiUnitSystem = BmiUnitSystem.Metric,
    val height: String = "",
    val neck: String = "",
    val waist: String = "",
    val hip: String = "", // Only for female
    val isMale: Boolean = true,
    val bodyFatPercentage: Double? = null
)

// --- 2. ViewModel ---
class BodyFatViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(BodyFatUiState())
    val uiState = _uiState.asStateFlow()

    fun onHeightChange(value: String) { _uiState.update { it.copy(height = value) }; calculate() }
    fun onNeckChange(value: String) { _uiState.update { it.copy(neck = value) }; calculate() }
    fun onWaistChange(value: String) { _uiState.update { it.copy(waist = value) }; calculate() }
    fun onHipChange(value: String) { _uiState.update { it.copy(hip = value) }; calculate() }
    fun onGenderChange(isMale: Boolean) { _uiState.update { it.copy(isMale = isMale) }; calculate() }

    private fun calculate() {
        viewModelScope.launch {
            val state = _uiState.value
            val height = state.height.toDoubleOrNull()
            val neck = state.neck.toDoubleOrNull()
            val waist = state.waist.toDoubleOrNull()
            val hip = state.hip.toDoubleOrNull()

            if (height == null || neck == null || waist == null) {
                _uiState.update { it.copy(bodyFatPercentage = null) }
                return@launch
            }

            val bfp = if (state.isMale) {
                if (height > 0) 86.010 * log10(waist - neck) - 70.041 * log10(height) + 36.76 else null
            } else {
                if (hip != null && height > 0) 163.205 * log10(waist + hip - neck) - 97.684 * log10(height) - 78.387 else null
            }
            _uiState.update { it.copy(bodyFatPercentage = bfp) }
        }
    }
}

// --- 3. Composable Screen ---
@Composable
fun BodyFatCalculatorScreen(viewModel: BodyFatViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(stringResource(R.string.body_fat_calculator_title), style = MaterialTheme.typography.headlineSmall)

        // Gender Selector
        SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
            SegmentedButton(
                selected = uiState.isMale,
                onClick = { viewModel.onGenderChange(true) },
                shape = SegmentedButtonDefaults.itemShape(0, 2)
            ) { Text(stringResource(R.string.gender_male)) }
            SegmentedButton(
                selected = !uiState.isMale,
                onClick = { viewModel.onGenderChange(false) },
                shape = SegmentedButtonDefaults.itemShape(1, 2)
            ) { Text(stringResource(R.string.gender_female)) }
        }

        NumericTextField(value = uiState.height, onValueChange = viewModel::onHeightChange, label = stringResource(R.string.height_cm))
        NumericTextField(value = uiState.neck, onValueChange = viewModel::onNeckChange, label = stringResource(R.string.neck_circumference_cm))
        NumericTextField(value = uiState.waist, onValueChange = viewModel::onWaistChange, label = stringResource(R.string.waist_circumference_cm))
        if (!uiState.isMale) {
            NumericTextField(value = uiState.hip, onValueChange = viewModel::onHipChange, label = stringResource(R.string.hip_circumference_cm))
        }

        uiState.bodyFatPercentage?.let {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(stringResource(R.string.body_fat_percentage_result), style = MaterialTheme.typography.titleLarge)
                    Text(
                        text = "${formatDouble(it, "#.0")} %",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}
