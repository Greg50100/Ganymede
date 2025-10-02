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
import com.joviansapps.ganymede.ui.components.NumericTextField
import com.joviansapps.ganymede.ui.components.formatDouble
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.DecimalFormat

// --- 1. UI State & Model ---
enum class Gender { MALE, FEMALE }

data class BmrUiState(
    val age: String = "30",
    val height: String = "180", // cm
    val weight: String = "75", // kg
    val gender: Gender = Gender.MALE,
    val bmrResult: Double? = null
)

// --- 2. ViewModel ---
class BmrViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(BmrUiState())
    val uiState = _uiState.asStateFlow()

    init {
        calculate()
    }

    fun onAgeChange(value: String) {
        _uiState.update { it.copy(age = value.filter { c -> c.isDigit() }) }
        calculate()
    }

    fun onHeightChange(value: String) {
        _uiState.update { it.copy(height = value) }
        calculate()
    }

    fun onWeightChange(value: String) {
        _uiState.update { it.copy(weight = value) }
        calculate()
    }

    fun onGenderChange(gender: Gender) {
        _uiState.update { it.copy(gender = gender) }
        calculate()
    }

    private fun calculate() {
        viewModelScope.launch {
            val age = uiState.value.age.toIntOrNull()
            val height = uiState.value.height.toDoubleOrNull()
            val weight = uiState.value.weight.toDoubleOrNull()

            if (age == null || height == null || weight == null || age <= 0 || height <= 0 || weight <= 0) {
                _uiState.update { it.copy(bmrResult = null) }
                return@launch
            }

            // Using Mifflin-St Jeor Equation
            val bmr = when (uiState.value.gender) {
                Gender.MALE -> (10 * weight) + (6.25 * height) - (5 * age) + 5
                Gender.FEMALE -> (10 * weight) + (6.25 * height) - (5 * age) - 161
            }
            _uiState.update { it.copy(bmrResult = bmr) }
        }
    }
}

// --- 3. Composable Screen ---
@Composable
fun BmrCalculatorScreen(viewModel: BmrViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(stringResource(R.string.bmr_calculator_title), style = MaterialTheme.typography.headlineSmall)

        // Gender Selection
        SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
            SegmentedButton(
                selected = uiState.gender == Gender.MALE,
                onClick = { viewModel.onGenderChange(Gender.MALE) },
                shape = MaterialTheme.shapes.medium
            ) { Text(stringResource(R.string.gender_male)) }
            SegmentedButton(
                selected = uiState.gender == Gender.FEMALE,
                onClick = { viewModel.onGenderChange(Gender.FEMALE) },
                shape = MaterialTheme.shapes.medium
            ) { Text(stringResource(R.string.gender_female)) }
        }

        // Input Fields
        NumericTextField(
            value = uiState.age,
            onValueChange = viewModel::onAgeChange,
            label = stringResource(R.string.age_years),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )
        NumericTextField(
            value = uiState.height,
            onValueChange = viewModel::onHeightChange,
            label = stringResource(R.string.height_cm),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )
        NumericTextField(
            value = uiState.weight,
            onValueChange = viewModel::onWeightChange,
            label = stringResource(R.string.weight_kg),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        // Result Display
        uiState.bmrResult?.let { bmr ->
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    Text(
                        text = stringResource(R.string.bmr_result_title),
                        style = MaterialTheme.typography.titleLarge
                    )
                    Text(
                        text = "${formatDouble(bmr, "#,##0")} kcal / day",
                        style = MaterialTheme.typography.headlineMedium.copy(color = MaterialTheme.colorScheme.primary)
                    )
                }
            }
        }
    }
}
