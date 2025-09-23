/**
 * File: TransformerCalculatorScreen.kt
 * Project: Ganymede
 *
 * Author: Greg50100
 * Date: 23/09/2025
 *
 * Description:
 * This file contains the Composable screen for the Ideal Transformer Calculator.
 * It allows users to calculate various transformer properties such as voltages,
 * currents, and turns ratio based on the ideal transformer equations.
 * The UI is built with Jetpack Compose and follows the MVVM pattern.
 *
 * For more details, see the project repository:
 * https://github.com/Greg50100/Ganymede
 */
package com.joviansapps.ganymede.ui.screens.utilities.electronics

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
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

/**
 * Defines the variable to be calculated by the transformer screen.
 * This determines which input fields are enabled and which value is computed.
 */
enum class CalculationMode {
    CALCULATE_VS,
    CALCULATE_VP,
    CALCULATE_IS,
    CALCULATE_IP,
    CALCULATE_RATIO
}

/**
 * Represents the complete UI state for the Transformer Calculator screen.
 * It holds the string values of all input fields and the selected calculation mode.
 */
data class TransformerUiState(
    val primaryTurns: String = "1000",
    val secondaryTurns: String = "100",
    val primaryVoltage: String = "120",
    val secondaryVoltage: String = "",
    val primaryCurrent: String = "",
    val secondaryCurrent: String = "",
    val turnsRatio: String = "",
    val selectedMode: CalculationMode = CalculationMode.CALCULATE_VS
)

/**
 * Sealed class to provide type-safe identifiers for input fields,
 * avoiding the use of "magic strings".
 */
sealed class TransformerField {
    object PrimaryTurns : TransformerField()
    object SecondaryTurns : TransformerField()
    object PrimaryVoltage : TransformerField()
    object SecondaryVoltage : TransformerField()
    object PrimaryCurrent : TransformerField()
    object SecondaryCurrent : TransformerField()
}


/**
 * Handles the business logic for the Transformer Calculator screen.
 * It manages the UI state, processes user input, and performs calculations
 * based on the selected mode and the ideal transformer equations (Vp/Vs = Np/Ns = Is/Ip).
 */
@OptIn(ExperimentalMaterial3Api::class)
class TransformerViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(TransformerUiState())
    val uiState = _uiState.asStateFlow()
    private val formatter = DecimalFormat("#.####")

    init {
        calculate()
    }

    /**
     * Updates the corresponding input field in the UI state and triggers a recalculation.
     * @param field A type-safe identifier for the field being updated from the [TransformerField] sealed class.
     * @param value The new string value from the text field.
     */
    fun onValueChange(field: TransformerField, value: String) {
        _uiState.update {
            when (field) {
                TransformerField.PrimaryTurns -> it.copy(primaryTurns = value)
                TransformerField.SecondaryTurns -> it.copy(secondaryTurns = value)
                TransformerField.PrimaryVoltage -> it.copy(primaryVoltage = value)
                TransformerField.SecondaryVoltage -> it.copy(secondaryVoltage = value)
                TransformerField.PrimaryCurrent -> it.copy(primaryCurrent = value)
                TransformerField.SecondaryCurrent -> it.copy(secondaryCurrent = value)
            }
        }
        calculate()
    }

    /**
     * Changes the current calculation mode.
     * It resets all input fields to provide a clean slate for the new calculation.
     * @param mode The new [CalculationMode] selected by the user.
     */
    fun onModeSelected(mode: CalculationMode) {
        // Reset the state but keep the selected mode
        _uiState.value = TransformerUiState(selectedMode = mode)
        calculate()
    }

    /**
     * Performs the transformer calculation based on the current UI state and the selected
     * [CalculationMode]. It parses the input strings, applies the appropriate ideal
     * transformer formula, and updates the state with the formatted result.
     */
    private fun calculate() {
        viewModelScope.launch {
            val state = _uiState.value
            // When a calculation runs, clear the result field that is not the target
            // to avoid showing stale data if inputs are cleared.
            val clearedState = when (state.selectedMode) {
                CalculationMode.CALCULATE_VS -> state.copy(secondaryVoltage = "")
                CalculationMode.CALCULATE_VP -> state.copy(primaryVoltage = "")
                CalculationMode.CALCULATE_IS -> state.copy(secondaryCurrent = "")
                CalculationMode.CALCULATE_IP -> state.copy(primaryCurrent = "")
                CalculationMode.CALCULATE_RATIO -> state.copy(turnsRatio = "")
            }
            _uiState.value = clearedState

            when (clearedState.selectedMode) {
                CalculationMode.CALCULATE_VS -> {
                    val np = clearedState.primaryTurns.toDoubleOrNull()
                    val ns = clearedState.secondaryTurns.toDoubleOrNull()
                    val vp = clearedState.primaryVoltage.toDoubleOrNull()
                    if (np != null && ns != null && vp != null && np > 0 && ns > 0) {
                        val ratio = np / ns
                        val vs = vp / ratio
                        _uiState.update { it.copy(secondaryVoltage = formatter.format(vs)) }
                    }
                }
                CalculationMode.CALCULATE_VP -> {
                    val np = clearedState.primaryTurns.toDoubleOrNull()
                    val ns = clearedState.secondaryTurns.toDoubleOrNull()
                    val vs = clearedState.secondaryVoltage.toDoubleOrNull()
                    if (np != null && ns != null && vs != null && np > 0 && ns > 0) {
                        val ratio = np / ns
                        val vp = vs * ratio
                        _uiState.update { it.copy(primaryVoltage = formatter.format(vp)) }
                    }
                }
                CalculationMode.CALCULATE_IS -> {
                    val np = clearedState.primaryTurns.toDoubleOrNull()
                    val ns = clearedState.secondaryTurns.toDoubleOrNull()
                    val ip = clearedState.primaryCurrent.toDoubleOrNull()
                    if (np != null && ns != null && ip != null && np > 0 && ns > 0) {
                        val ratio = np / ns
                        val `is` = ip * ratio
                        _uiState.update { it.copy(secondaryCurrent = formatter.format(`is`)) }
                    }
                }
                CalculationMode.CALCULATE_IP -> {
                    val np = clearedState.primaryTurns.toDoubleOrNull()
                    val ns = clearedState.secondaryTurns.toDoubleOrNull()
                    val `is` = clearedState.secondaryCurrent.toDoubleOrNull()
                    if (np != null && ns != null && `is` != null && np > 0) {
                        val ratio = np / ns
                        val ip = `is` / ratio
                        _uiState.update { it.copy(primaryCurrent = formatter.format(ip)) }
                    }
                }
                CalculationMode.CALCULATE_RATIO -> {
                    val vp = clearedState.primaryVoltage.toDoubleOrNull()
                    val vs = clearedState.secondaryVoltage.toDoubleOrNull()
                    if (vp != null && vs != null && vs != 0.0) {
                        val ratio = vp / vs
                        _uiState.update { it.copy(turnsRatio = formatter.format(ratio)) }
                    }
                }
            }
        }
    }
}


/**
 * The main composable function for the Transformer Calculator screen.
 * It displays the UI, including an image, a mode selector, and dynamic input/output
 * fields based on the user's selection. It observes the state from the [TransformerViewModel].
 *
 * @param viewModel The [TransformerViewModel] instance that provides state and logic for the screen.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
fun TransformerCalculatorScreen(viewModel: TransformerViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val calculationOptions = mapOf(
        CalculationMode.CALCULATE_VS to "Us",
        CalculationMode.CALCULATE_VP to "Up",
        CalculationMode.CALCULATE_IS to "Is",
        CalculationMode.CALCULATE_IP to "Ip",
        CalculationMode.CALCULATE_RATIO to "Np/Ns"
    )
    val optionsList = calculationOptions.entries.toList()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.transformator),
            contentDescription = "Transformer circuit diagram",
            contentScale = ContentScale.FillWidth,
            modifier = Modifier.fillMaxWidth(),
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary)
        )

        SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
            optionsList.forEachIndexed { index, (mode, label) ->
                SegmentedButton(
                    shape = SegmentedButtonDefaults.itemShape(index = index, count = optionsList.size),
                    onClick = { viewModel.onModeSelected(mode) },
                    selected = uiState.selectedMode == mode
                ) {
                    Text(label)
                }
            }
        }

        // --- Fields are displayed conditionally ---
        when (uiState.selectedMode) {
            CalculationMode.CALCULATE_VS -> {
                TransformerInputField(value = uiState.primaryTurns, onValueChange = { viewModel.onValueChange(TransformerField.PrimaryTurns, it) }, label = "Np")
                TransformerInputField(value = uiState.secondaryTurns, onValueChange = { viewModel.onValueChange(TransformerField.SecondaryTurns, it) }, label = "Ns")
                TransformerInputField(value = uiState.primaryVoltage, onValueChange = { viewModel.onValueChange(TransformerField.PrimaryVoltage, it) }, label = "Up", unit = "V")
                if (uiState.secondaryVoltage.isNotBlank()) {
                    ResultField(label = "Result: Us", value = uiState.secondaryVoltage, unit = "V")
                }
            }
            CalculationMode.CALCULATE_VP -> {
                TransformerInputField(value = uiState.primaryTurns, onValueChange = { viewModel.onValueChange(TransformerField.PrimaryTurns, it) }, label = "Np")
                TransformerInputField(value = uiState.secondaryTurns, onValueChange = { viewModel.onValueChange(TransformerField.SecondaryTurns, it) }, label = "Ns")
                TransformerInputField(value = uiState.secondaryVoltage, onValueChange = { viewModel.onValueChange(TransformerField.SecondaryVoltage, it) }, label = "Us", unit = "V")
                if (uiState.primaryVoltage.isNotBlank()) {
                    ResultField(label = "Result: Up", value = uiState.primaryVoltage, unit = "V")
                }
            }
            CalculationMode.CALCULATE_IS -> {
                TransformerInputField(value = uiState.primaryTurns, onValueChange = { viewModel.onValueChange(TransformerField.PrimaryTurns, it) }, label = "Np")
                TransformerInputField(value = uiState.secondaryTurns, onValueChange = { viewModel.onValueChange(TransformerField.SecondaryTurns, it) }, label = "Ns")
                TransformerInputField(value = uiState.primaryCurrent, onValueChange = { viewModel.onValueChange(TransformerField.PrimaryCurrent, it) }, label = "Ip", unit = "A")
                if (uiState.secondaryCurrent.isNotBlank()) {
                    ResultField(label = "Result: Is", value = uiState.secondaryCurrent, unit = "A")
                }
            }
            CalculationMode.CALCULATE_IP -> {
                TransformerInputField(value = uiState.primaryTurns, onValueChange = { viewModel.onValueChange(TransformerField.PrimaryTurns, it) }, label = "Np")
                TransformerInputField(value = uiState.secondaryTurns, onValueChange = { viewModel.onValueChange(TransformerField.SecondaryTurns, it) }, label = "Ns")
                TransformerInputField(value = uiState.secondaryCurrent, onValueChange = { viewModel.onValueChange(TransformerField.SecondaryCurrent, it) }, label = "Is", unit = "A")
                if (uiState.primaryCurrent.isNotBlank()) {
                    ResultField(label = "Result: Ip", value = uiState.primaryCurrent, unit = "A")
                }
            }
            CalculationMode.CALCULATE_RATIO -> {
                TransformerInputField(value = uiState.primaryVoltage, onValueChange = { viewModel.onValueChange(TransformerField.PrimaryVoltage, it) }, label = "Up", unit = "V")
                TransformerInputField(value = uiState.secondaryVoltage, onValueChange = { viewModel.onValueChange(TransformerField.SecondaryVoltage, it) }, label = "Us", unit = "V")
                if (uiState.turnsRatio.isNotBlank()) {
                    ResultField(label = "Turns Ratio (Np/Ns)", value = uiState.turnsRatio)
                }
            }
        }
    }
}

/**
 * A reusable composable for a styled OutlinedTextField specific to this screen.
 *
 * @param value The current text to be displayed in the text field.
 * @param onValueChange The callback that is triggered when the value of the text field changes.
 * @param label The text to be displayed as the label of the text field.
 * @param unit An optional string to be displayed as a trailing icon.
 */
@Composable
private fun TransformerInputField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    unit: String? = null
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        modifier = Modifier.fillMaxWidth(),
        trailingIcon = unit?.let { { Text(it) } },
        singleLine = true
    )
}

