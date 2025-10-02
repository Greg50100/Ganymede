package com.joviansapps.ganymede.ui.screens.utilities.physics

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
import kotlin.math.*

private const val GRAVITY_PROJECTILE = 9.80665 // m/s²

// --- 1. UI State ---
data class ProjectileUiState(
    val initialVelocity: String = "20",
    val launchAngle: String = "45",
    val initialHeight: String = "0",
    val maxHealth: Double? = null,
    val timeOfFlight: Double? = null,
    val range: Double? = null
)

// --- 2. ViewModel ---
class ProjectileViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(ProjectileUiState())
    val uiState = _uiState.asStateFlow()

    init {
        calculate()
    }

    fun onVelocityChange(v: String) { _uiState.update { it.copy(initialVelocity = v) }; calculate() }
    fun onAngleChange(a: String) { _uiState.update { it.copy(launchAngle = a) }; calculate() }
    fun onHeightChange(h: String) { _uiState.update { it.copy(initialHeight = h) }; calculate() }

    private fun calculate() {
        viewModelScope.launch {
            val v0 = _uiState.value.initialVelocity.toDoubleOrNull()
            val angleDeg = _uiState.value.launchAngle.toDoubleOrNull()
            val h0 = _uiState.value.initialHeight.toDoubleOrNull()

            if (v0 == null || angleDeg == null || h0 == null) {
                _uiState.update { it.copy(maxHealth = null) }
                return@launch
            }

            val angleRad = Math.toRadians(angleDeg)
            val v0y = v0 * sin(angleRad)
            val v0x = v0 * cos(angleRad)

            val timeToApex = v0y / GRAVITY_PROJECTILE
            val apexHeight = h0 + (v0y.pow(2) / (2 * GRAVITY_PROJECTILE))
            val timeFromApexToGround = sqrt(2 * apexHeight / GRAVITY_PROJECTILE)
            val totalTime = timeToApex + timeFromApexToGround
            val totalRange = v0x * totalTime

            _uiState.update {
                it.copy(maxHealth = apexHeight, timeOfFlight = totalTime, range = totalRange)
            }
        }
    }
}

// --- 3. Composable Screen ---
@Composable
fun ProjectileMotionCalculatorScreen(viewModel: ProjectileViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp).verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        NumericTextField(value = uiState.initialVelocity, onValueChange = viewModel::onVelocityChange, label = stringResource(R.string.initial_velocity_label), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
        NumericTextField(value = uiState.launchAngle, onValueChange = viewModel::onAngleChange, label = stringResource(R.string.launch_angle_label), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
        NumericTextField(value = uiState.initialHeight, onValueChange = viewModel::onHeightChange, label = stringResource(R.string.initial_height_label), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))

        uiState.maxHealth?.let {
            ResultsCard(uiState)
        }
    }
}

@Composable
private fun ResultsCard(uiState: ProjectileUiState) {
    Card(modifier = Modifier.fillMaxWidth().padding(top = 16.dp)) {
        Column(Modifier.padding(16.dp)) {
            Text(stringResource(R.string.results_title), style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(16.dp))
            ResultField(stringResource(R.string.max_height_label), "${formatDouble(uiState.maxHealth, "#.##")} m")
            ResultField(stringResource(R.string.time_of_flight_label), "${formatDouble(uiState.timeOfFlight, "#.##")} s")
            ResultField(stringResource(R.string.range_label), "${formatDouble(uiState.range, "#.##")} m")
        }
    }
}
