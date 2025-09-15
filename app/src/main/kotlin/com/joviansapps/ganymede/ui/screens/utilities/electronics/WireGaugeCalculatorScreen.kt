package com.joviansapps.ganymede.ui.screens.utilities.electronics

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.joviansapps.ganymede.R
import kotlin.math.ceil

// Simplified AWG data: Map<AWG, Diameter in mm>
val awgData = mapOf(
    0 to 8.25, 1 to 7.35, 2 to 6.54, 3 to 5.83, 4 to 5.19, 5 to 4.62, 6 to 4.11, 7 to 3.66, 8 to 3.26, 9 to 2.91,
    10 to 2.59, 11 to 2.30, 12 to 2.05, 13 to 1.83, 14 to 1.63, 15 to 1.45, 16 to 1.29, 17 to 1.15, 18 to 1.02,
    19 to 0.912, 20 to 0.812, 21 to 0.723, 22 to 0.644, 23 to 0.573, 24 to 0.511
)

@Composable
fun WireGaugeCalculatorScreen() {
    var voltage by remember { mutableStateOf("12") }
    var current by remember { mutableStateOf("") }
    var wireLength by remember { mutableStateOf("") }
    var maxDropPercentage by remember { mutableStateOf("3") }

    var resultGauge by remember { mutableStateOf<Int?>(null) }
    var error by remember { mutableStateOf<String?>(null) }

    val ctx = LocalContext.current

    fun calculate() {
        val v = voltage.toDoubleOrNull()
        val i = current.toDoubleOrNull()
        val l = wireLength.toDoubleOrNull()
        val dropPercent = maxDropPercentage.toDoubleOrNull()
        error = null

        if (v == null || i == null || l == null || dropPercent == null) {
            error = ctx.getString(R.string.error_all_fields_required)
            return
        }

        val maxVoltageDrop = v * (dropPercent / 100.0)
        val requiredResistance = maxVoltageDrop / i
        // Using copper resistivity for this calculation
        val requiredArea = (1.68e-8 * l) / requiredResistance
        val requiredDiameterM = 2 * kotlin.math.sqrt(requiredArea / Math.PI)
        val requiredDiameterMm = requiredDiameterM * 1000

        val suitableGauge = awgData.entries
            .filter { it.value >= requiredDiameterMm }
            .minByOrNull { it.key }

        resultGauge = suitableGauge?.key
    }

    Column(modifier = Modifier.padding(16.dp).fillMaxSize()) {
        OutlinedTextField(value = voltage, onValueChange = { voltage = it }, label = { Text(stringResource(R.string.source_voltage_v)) }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(value = current, onValueChange = { current = it }, label = { Text(stringResource(R.string.current_a)) }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(value = wireLength, onValueChange = { wireLength = it }, label = { Text(stringResource(R.string.wire_length_m)) }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(value = maxDropPercentage, onValueChange = { maxDropPercentage = it }, label = { Text(stringResource(R.string.max_voltage_drop_percent)) }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = { calculate() }, modifier = Modifier.fillMaxWidth()) {
            Text(stringResource(R.string.calculate_button))
        }

        error?.let {
            Text(it, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(top = 8.dp))
        }

        resultGauge?.let {
            Text(stringResource(R.string.recommended_wire_gauge_result, it, awgData[it] ?: 0.0), style = MaterialTheme.typography.headlineSmall, modifier = Modifier.padding(top = 16.dp))
        }
    }
}
