package com.joviansapps.ganymede.ui.screens.utilities.physics

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.DecimalFormat
import kotlin.math.pow
import kotlin.math.sqrt

private const val GRAVITY = 9.80665 // m/s²

// --- 1. State and Enums ---
enum class BernoulliUnknown { P1, v1, h1, P2, v2, h2 }

data class BernoulliUiState(
    val p1: String = "101325", // Pa
    val v1: String = "10",     // m/s
    val h1: String = "20",     // m
    val p2: String = "",       // Pa
    val v2: String = "5",      // m/s
    val h2: String = "10",     // m
    val density: String = "1000", // kg/m³, default pour l'eau
    val unknown: BernoulliUnknown = BernoulliUnknown.P2,
    val result: String? = null,
    val error: String? = null
)

// --- 2. ViewModel ---
class BernoulliViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(BernoulliUiState())
    val uiState = _uiState.asStateFlow()

    fun onValueChange(field: String, value: String) {
        _uiState.update {
            when (field) {
                "p1" -> it.copy(p1 = value)
                "v1" -> it.copy(v1 = value)
                "h1" -> it.copy(h1 = value)
                "p2" -> it.copy(p2 = value)
                "v2" -> it.copy(v2 = value)
                "h2" -> it.copy(h2 = value)
                "density" -> it.copy(density = value)
                else -> it
            }
        }
        calculate()
    }

    fun onUnknownChange(unknown: BernoulliUnknown) {
        _uiState.update { it.copy(unknown = unknown) }
        calculate()
    }

    private fun calculate() {
        viewModelScope.launch {
            val s = _uiState.value
            val p1 = s.p1.toDoubleOrNull()
            val v1 = s.v1.toDoubleOrNull()
            val h1 = s.h1.toDoubleOrNull()
            val p2 = s.p2.toDoubleOrNull()
            val v2 = s.v2.toDoubleOrNull()
            val h2 = s.h2.toDoubleOrNull()
            val rho = s.density.toDoubleOrNull()

            if (rho == null || rho <= 0) {
                _uiState.update { it.copy(result = null, error = "La densité doit être positive.") }
                return@launch
            }

            var res: Double? = null
            var err: String? = null

            try {
                res = when (s.unknown) {
                    BernoulliUnknown.P1 -> p2!! + 0.5 * rho * (v2!!.pow(2) - v1!!.pow(2)) + rho * GRAVITY * (h2!! - h1!!)
                    BernoulliUnknown.v1 -> {
                        val underSqrt = v2!!.pow(2) + (2 / rho) * (p2!! - p1!!) + 2 * GRAVITY * (h2!! - h1!!)
                        if (underSqrt < 0) throw Exception("Valeurs impossibles (racine carrée négative)")
                        sqrt(underSqrt)
                    }
                    BernoulliUnknown.h1 -> h2!! + (p2!! - p1!!) / (rho * GRAVITY) + (v2!!.pow(2) - v1!!.pow(2)) / (2 * GRAVITY)
                    BernoulliUnknown.P2 -> p1!! + 0.5 * rho * (v1!!.pow(2) - v2!!.pow(2)) + rho * GRAVITY * (h1!! - h2!!)
                    BernoulliUnknown.v2 -> {
                        val underSqrt = v1!!.pow(2) + (2 / rho) * (p1!! - p2!!) + 2 * GRAVITY * (h1!! - h2!!)
                        if (underSqrt < 0) throw Exception("Valeurs impossibles (racine carrée négative)")
                        sqrt(underSqrt)
                    }
                    BernoulliUnknown.h2 -> h1!! + (p1!! - p2!!) / (rho * GRAVITY) + (v1!!.pow(2) - v2!!.pow(2)) / (2 * GRAVITY)
                }
            } catch (e: NullPointerException) {
                // Un champ nécessaire est vide
                res = null
                err = null
            } catch (e: Exception) {
                res = null
                err = e.message
            }


            val resultString = res?.let {
                val formatter = DecimalFormat("#.###")
                when (s.unknown) {
                    BernoulliUnknown.P1, BernoulliUnknown.P2 -> "${formatter.format(it)} Pa"
                    BernoulliUnknown.v1, BernoulliUnknown.v2 -> "${formatter.format(it)} m/s"
                    BernoulliUnknown.h1, BernoulliUnknown.h2 -> "${formatter.format(it)} m"
                }
            }
            _uiState.update { it.copy(result = resultString, error = err) }
        }
    }

    init {
        calculate()
    }
}

// --- 3. Composable Screen ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BernoulliCalculatorScreen(viewModel: BernoulliViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Principe de Bernoulli", style = MaterialTheme.typography.headlineSmall)
        Text("P + ½ρv² + ρgh = constante", style = MaterialTheme.typography.titleMedium)

        OutlinedTextField(
            value = uiState.density,
            onValueChange = { viewModel.onValueChange("density", it) },
            label = { Text("Densité du fluide (kg/m³)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        Divider(modifier = Modifier.padding(vertical = 8.dp))

        Text(stringResource(id = R.string.calculate_for), style = MaterialTheme.typography.titleMedium)
        SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
            BernoulliUnknown.values().forEach { variable ->
                SegmentedButton(
                    selected = uiState.unknown == variable,
                    onClick = { viewModel.onUnknownChange(variable) },
                    shape = SegmentedButtonDefaults.itemShape(variable.ordinal, BernoulliUnknown.values().size)
                ) {
                    Text(variable.name)
                }
            }
        }

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            BernoulliInput("P1 (Pa)", uiState.p1, { viewModel.onValueChange("p1", it) }, uiState.unknown != BernoulliUnknown.P1, Modifier.weight(1f))
            BernoulliInput("P2 (Pa)", uiState.p2, { viewModel.onValueChange("p2", it) }, uiState.unknown != BernoulliUnknown.P2, Modifier.weight(1f))
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            BernoulliInput("v1 (m/s)", uiState.v1, { viewModel.onValueChange("v1", it) }, uiState.unknown != BernoulliUnknown.v1, Modifier.weight(1f))
            BernoulliInput("v2 (m/s)", uiState.v2, { viewModel.onValueChange("v2", it) }, uiState.unknown != BernoulliUnknown.v2, Modifier.weight(1f))
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            BernoulliInput("h1 (m)", uiState.h1, { viewModel.onValueChange("h1", it) }, uiState.unknown != BernoulliUnknown.h1, Modifier.weight(1f))
            BernoulliInput("h2 (m)", uiState.h2, { viewModel.onValueChange("h2", it) }, uiState.unknown != BernoulliUnknown.h2, Modifier.weight(1f))
        }

        uiState.error?.let {
            Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
        }

        uiState.result?.let {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    Text(text = stringResource(id = R.string.result_title), style = MaterialTheme.typography.titleLarge)
                    Text(
                        text = it,
                        style = MaterialTheme.typography.headlineMedium.copy(color = MaterialTheme.colorScheme.primary)
                    )
                }
            }
        }
    }
}

@Composable
private fun BernoulliInput(label: String, value: String, onValueChange: (String) -> Unit, isEnabled: Boolean, modifier: Modifier = Modifier) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        modifier = modifier,
        enabled = isEnabled,
        readOnly = !isEnabled
    )
}
