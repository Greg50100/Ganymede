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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.DecimalFormat
import kotlin.math.*

private const val GRAVITY = 9.80665 // m/s²

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

            val timeToApex = v0y / GRAVITY
            val apexHeight = h0 + (v0y.pow(2) / (2 * GRAVITY))
            val timeFromApexToGround = sqrt(2 * apexHeight / GRAVITY)
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
        OutlinedTextField(value = uiState.initialVelocity, onValueChange = viewModel::onVelocityChange, label = { Text("Initial Velocity (m/s)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = uiState.launchAngle, onValueChange = viewModel::onAngleChange, label = { Text("Launch Angle (degrees)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = uiState.initialHeight, onValueChange = viewModel::onHeightChange, label = { Text("Initial Height (m)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth())

        uiState.maxHealth?.let {
            ResultsCard(uiState)
        }
    }
}

@Composable
private fun ResultsCard(uiState: ProjectileUiState) {
    val formatter = DecimalFormat("#.##")
    Card(modifier = Modifier.fillMaxWidth().padding(top = 16.dp)) {
        Column(Modifier.padding(16.dp)) {
            Text("Results", style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(16.dp))
            ResultRow("Max Height:", "${formatter.format(uiState.maxHealth)} m")
            ResultRow("Time of Flight:", "${formatter.format(uiState.timeOfFlight)} s")
            ResultRow("Range:", "${formatter.format(uiState.range)} m")
        }
    }
}

@Composable
private fun ResultRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(text = label, style = MaterialTheme.typography.bodyLarge)
        Text(text = value, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.primary)
    }
}
