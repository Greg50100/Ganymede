package com.joviansapps.ganymede.ui.screens.utilities.health

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import kotlin.math.pow

// --- 1. ViewModel and State ---

enum class BmiUnitSystem { Metric, Imperial }

data class BmiUiState(
    val height: String = "",
    val weight: String = "",
    val unitSystem: BmiUnitSystem = BmiUnitSystem.Metric,
    val bmiResult: Float? = null
)

class BmiViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(BmiUiState())
    val uiState = _uiState.asStateFlow()

    fun onHeightChange(value: String) {
        _uiState.update { it.copy(height = value) }
        calculateBmi()
    }

    fun onWeightChange(value: String) {
        _uiState.update { it.copy(weight = value) }
        calculateBmi()
    }

    fun onUnitSystemChange(system: BmiUnitSystem) {
        // Reset fields when changing system to avoid confusion
        _uiState.update { it.copy(unitSystem = system, height = "", weight = "", bmiResult = null) }
    }

    private fun calculateBmi() {
        viewModelScope.launch {
            val state = _uiState.value
            val height = state.height.toFloatOrNull()
            val weight = state.weight.toFloatOrNull()

            if (height == null || weight == null || height <= 0 || weight <= 0) {
                _uiState.update { it.copy(bmiResult = null) }
                return@launch
            }

            val bmi = when (state.unitSystem) {
                BmiUnitSystem.Metric -> weight / (height / 100).pow(2) // weight in kg, height in cm
                BmiUnitSystem.Imperial -> (weight / height.pow(2)) * 703 // weight in lbs, height in inches
            }
            _uiState.update { it.copy(bmiResult = bmi) }
        }
    }
}


// --- 2. UI ---

@Composable
@Preview
fun BmiCalculatorScreen(viewModel: BmiViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(stringResource(R.string.bmi_calculator_title), style = MaterialTheme.typography.headlineSmall)

        // Unit System Toggle
        SegmentedButton(
            selected = uiState.unitSystem,
            onSelected = { viewModel.onUnitSystemChange(it) }
        )

        // Input Fields
        val heightLabel = if (uiState.unitSystem == BmiUnitSystem.Metric) stringResource(R.string.height_cm) else stringResource(R.string.height_in)
        val weightLabel = if (uiState.unitSystem == BmiUnitSystem.Metric) stringResource(R.string.weight_kg) else stringResource(R.string.weight_lbs)

        NumericTextField(
            value = uiState.height,
            onValueChange = { viewModel.onHeightChange(it) },
            label = heightLabel,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        NumericTextField(
            value = uiState.weight,
            onValueChange = { viewModel.onWeightChange(it) },
            label = weightLabel,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        // Result Display
        Spacer(modifier = Modifier.height(16.dp))
        BmiResultGauge(bmi = uiState.bmiResult)
    }
}

@Composable
private fun SegmentedButton(
    selected: BmiUnitSystem,
    onSelected: (BmiUnitSystem) -> Unit
) {
    SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
        SegmentedButton(
            selected = selected == BmiUnitSystem.Metric,
            onClick = { onSelected(BmiUnitSystem.Metric) },
            shape = MaterialTheme.shapes.medium
        ) {
            Text(stringResource(R.string.metric))
        }
        SegmentedButton(
            selected = selected == BmiUnitSystem.Imperial,
            onClick = { onSelected(BmiUnitSystem.Imperial) },
            shape = MaterialTheme.shapes.medium
        ) {
            Text(stringResource(R.string.imperial))
        }
    }
}

@Composable
fun BmiResultGauge(bmi: Float?) {
    // Determine the category and color based on the BMI value. This is non-composable logic.
    val (categoryResId, color) = when {
        bmi == null -> R.string.enter_values to MaterialTheme.colorScheme.onSurface
        bmi < 18.5 -> R.string.underweight to MaterialTheme.colorScheme.secondary // Blue
        bmi < 25 -> R.string.normal_weight to Color(0xFF4CAF50) // Green
        bmi < 30 -> R.string.overweight to MaterialTheme.colorScheme.tertiary // Amber
        else -> R.string.obesity to MaterialTheme.colorScheme.error // Red
    }

    // Now, call the composable stringResource function with the determined ID.
    val category = stringResource(id = categoryResId)

    Box(
        modifier = Modifier
            .size(200.dp)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        val angle = ((bmi?.coerceIn(10f, 40f) ?: 10f) - 10f) / 30f * 270f
        val strokeWidth = 12.dp
        val colorScheme = MaterialTheme.colorScheme

        // Background Arc
        Canvas(modifier = Modifier.matchParentSize()) {
            drawArc(
                color = colorScheme.surfaceVariant,
                startAngle = 135f,
                sweepAngle = 270f,
                useCenter = false,
                style = Stroke(width = strokeWidth.toPx(), cap = StrokeCap.Round)
            )
        }

        // Foreground Arc
        if (bmi != null) {
            Canvas(modifier = Modifier.matchParentSize()) {
                drawArc(
                    color = color,
                    startAngle = 135f,
                    sweepAngle = angle,
                    useCenter = false,
                    style = Stroke(width = strokeWidth.toPx(), cap = StrokeCap.Round)
                )
            }
        }

        // Text in the center
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            if (bmi != null) {
                Text(
                    text = formatDouble(bmi.toDouble(), "#.1"),
                    style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold),
                    color = color
                )
            }
            Text(
                text = category,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = color
            )
        }
    }
}