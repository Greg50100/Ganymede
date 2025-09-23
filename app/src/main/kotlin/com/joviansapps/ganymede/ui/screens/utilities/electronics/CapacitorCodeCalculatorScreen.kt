package com.joviansapps.ganymede.ui.screens.utilities.electronics

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

// --- 1. State ---
data class CapacitorCodeUiState(
    val code: String = "104K",
    val capacitanceInPf: Double? = null,
    val tolerance: String? = null,
    val error: String? = null
)

// --- 2. ViewModel ---
class CapacitorCodeViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(CapacitorCodeUiState())
    val uiState = _uiState.asStateFlow()

    private val toleranceMap = mapOf(
        'B' to "±0.1 pF", 'C' to "±0.25 pF", 'D' to "±0.5 pF",
        'F' to "±1%", 'G' to "±2%", 'J' to "±5%", 'K' to "±10%", 'M' to "±20%",
        'Z' to "+80%, -20%"
    )

    init {
        parseCode()
    }

    fun onCodeChange(newCode: String) {
        _uiState.update { it.copy(code = newCode.uppercase()) }
        parseCode()
    }

    private fun parseCode() {
        viewModelScope.launch {
            val code = _uiState.value.code
            if (code.isBlank()) {
                _uiState.update { it.copy(capacitanceInPf = null, tolerance = null, error = null) }
                return@launch
            }

            try {
                // Sépare les chiffres et la lettre de tolérance
                val numericPart = code.filter { it.isDigit() }
                val toleranceChar = code.firstOrNull { it.isLetter() }

                if (numericPart.length < 3 || numericPart.length > 4) {
                    throw IllegalArgumentException("Le code doit contenir 3 ou 4 chiffres.")
                }

                val val1 = numericPart.substring(0, 1).toInt()
                val val2 = numericPart.substring(1, 2).toInt()
                val multiplierDigit = numericPart.substring(2, 3).toInt()

                val baseValue = (val1 * 10 + val2).toDouble()
                val capacitance = baseValue * Math.pow(10.0, multiplierDigit.toDouble())

                val toleranceValue = toleranceChar?.let { toleranceMap[it] }
                    ?: if (numericPart.length == 3 && toleranceChar == null) "±20% (défaut)" else null

                _uiState.update {
                    it.copy(
                        capacitanceInPf = capacitance,
                        tolerance = toleranceValue,
                        error = null
                    )
                }

            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        capacitanceInPf = null,
                        tolerance = null,
                        error = e.message ?: "Code non valide"
                    )
                }
            }
        }
    }
}

// --- 3. Composable Screen ---
@Composable
fun CapacitorCodeCalculatorScreen(viewModel: CapacitorCodeViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(stringResource(id = R.string.capacitor_code_calculator_title), style = MaterialTheme.typography.headlineSmall)
        Text("Décode les marquages standards des condensateurs.", style = MaterialTheme.typography.bodyMedium)

        OutlinedTextField(
            value = uiState.code,
            onValueChange = viewModel::onCodeChange,
            label = { Text("Code du condensateur (ex: 104K)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
            modifier = Modifier.fillMaxWidth(),
            isError = uiState.error != null
        )

        uiState.error?.let {
            Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
        }

        if (uiState.capacitanceInPf != null) {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    Text(text = stringResource(id = R.string.result_title), style = MaterialTheme.typography.titleLarge)
                    Spacer(modifier = Modifier.height(8.dp))

                    val pf = uiState.capacitanceInPf!!
                    val nf = pf / 1000.0
                    val uf = nf / 1000.0
                    val formatter = DecimalFormat("#.######")

                    Text(
                        text = "${formatter.format(pf)} pF",
                        style = MaterialTheme.typography.headlineMedium.copy(color = MaterialTheme.colorScheme.primary)
                    )
                    Text(
                        text = "= ${formatter.format(nf)} nF",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        text = "= ${formatter.format(uf)} µF",
                        style = MaterialTheme.typography.bodyLarge
                    )

                    uiState.tolerance?.let {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Tolérance : $it",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
        }
    }
}
