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
import kotlin.math.pow

data class SmdResistorUiState(
    val code: String = "103",
    val resistance: Double? = null,
    val error: String? = null
)

class SmdResistorViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(SmdResistorUiState())
    val uiState = _uiState.asStateFlow()

    // EIA-96 Code lookup table
    private val eia96CodeMap = mapOf(
        "01" to 100, "02" to 102, "03" to 105, "04" to 107, "05" to 110, "06" to 113, "07" to 115, "08" to 118,
        "09" to 121, "10" to 124, "11" to 127, "12" to 130, "13" to 133, "14" to 137, "15" to 140, "16" to 143,
        "17" to 147, "18" to 150, "19" to 154, "20" to 158, "21" to 162, "22" to 165, "23" to 169, "24" to 174,
        "25" to 178, "26" to 182, "27" to 187, "28" to 191, "29" to 196, "30" to 200, "31" to 205, "32" to 210,
        "33" to 215, "34" to 221, "35" to 226, "36" to 232, "37" to 237, "38" to 243, "39" to 249, "40" to 255,
        "41" to 261, "42" to 267, "43" to 274, "44" to 280, "45" to 287, "46" to 294, "47" to 301, "48" to 309,
        "49" to 316, "50" to 324, "51" to 332, "52" to 340, "53" to 348, "54" to 357, "55" to 365, "56" to 374,
        "57" to 383, "58" to 392, "59" to 402, "60" to 412, "61" to 422, "62" to 432, "63" to 442, "64" to 453,
        "65" to 464, "66" to 475, "67" to 487, "68" to 499, "69" to 511, "70" to 523, "71" to 536, "72" to 549,
        "73" to 562, "74" to 576, "75" to 590, "76" to 604, "77" to 619, "78" to 634, "79" to 649, "80" to 665,
        "81" to 681, "82" to 698, "83" to 715, "84" to 732, "85" to 750, "86" to 768, "87" to 787, "88" to 806,
        "89" to 825, "90" to 845, "91" to 866, "92" to 887, "93" to 909, "94" to 931, "95" to 953, "96" to 976
    )
    private val eia96MultiplierMap: Map<Char, Double> = mapOf(
        'Z' to 0.001, 'Y' to 0.01, 'R' to 0.01, 'X' to 0.1, 'S' to 0.1,
        'A' to 1.0, 'B' to 10.0, 'H' to 10.0, 'C' to 100.0, 'D' to 1000.0, 'E' to 10000.0, 'F' to 100000.0
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
                _uiState.update { it.copy(resistance = null, error = null) }
                return@launch
            }

            try {
                val resistance = when {
                    // Codes avec 'R' (ex: 4R7 = 4.7)
                    code.contains('R') -> {
                        code.replace('R', '.').toDouble()
                    }
                    // Codes à 3 ou 4 chiffres (ex: 103 = 10k, 1002 = 10k)
                    code.all { it.isDigit() } && code.length in 3..4 -> {
                        val base = code.dropLast(1).toDouble()
                        val multiplier = code.last().digitToInt()
                        base * 10.0.pow(multiplier.toDouble())
                    }
                    // Code EIA-96 (ex: 01A = 100 Ohms)
                    code.length == 3 && code.take(2).all { it.isDigit() } && eia96MultiplierMap.containsKey(code.last()) -> {
                        val valueCode = code.take(2)
                        val multiplierChar = code.last()
                        val baseInt = eia96CodeMap[valueCode] ?: throw IllegalArgumentException("Code EIA-96 non valide.")
                        val multiplier = eia96MultiplierMap[multiplierChar]!!
                        baseInt.toDouble() * multiplier
                    }
                    else -> throw IllegalArgumentException("Format de code non reconnu.")
                }
                _uiState.update { it.copy(resistance = resistance, error = null) }
            } catch (e: Exception) {
                _uiState.update { it.copy(resistance = null, error = e.message ?: "Code invalide") }
            }
        }
    }
}

@Composable
fun SmdResistorCalculatorScreen(viewModel: SmdResistorViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Décodage de Résistance SMD", style = MaterialTheme.typography.headlineSmall)
        Text("Décode les codes EIA à 3/4 chiffres, EIA-96 et avec 'R'.", style = MaterialTheme.typography.bodyMedium)

        OutlinedTextField(
            value = uiState.code,
            onValueChange = viewModel::onCodeChange,
            label = { Text("Code de la résistance CMS") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
            modifier = Modifier.fillMaxWidth(),
            isError = uiState.error != null
        )

        uiState.error?.let {
            Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
        }

        uiState.resistance?.let {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    Text(text = stringResource(id = R.string.result_title), style = MaterialTheme.typography.titleLarge)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = formatResistance(it),
                        style = MaterialTheme.typography.headlineMedium.copy(color = MaterialTheme.colorScheme.primary)
                    )
                }
            }
        }
    }
}

private fun formatResistance(value: Double): String {
    val formatter = DecimalFormat("#.##")
    return when {
        value >= 1_000_000 -> "${formatter.format(value / 1_000_000)} MΩ"
        value >= 1_000 -> "${formatter.format(value / 1_000)} kΩ"
        else -> "${formatter.format(value)} Ω"
    }
}
