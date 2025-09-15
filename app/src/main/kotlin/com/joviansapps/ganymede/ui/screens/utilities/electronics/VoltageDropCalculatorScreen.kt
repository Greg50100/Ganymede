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

// Resistivity values (ρ) in Ω·m at 20°C
private const val COPPER_RESISTIVITY = 1.68e-8
private const val ALUMINUM_RESISTIVITY = 2.82e-8

@Composable
fun VoltageDropCalculatorScreen() {
    var voltage by remember { mutableStateOf("") }
    var current by remember { mutableStateOf("") }
    var wireLength by remember { mutableStateOf("") }
    var wireDiameter by remember { mutableStateOf("") }
    var isCopper by remember { mutableStateOf(true) }

    var voltageDrop by remember { mutableStateOf<Double?>(null) }
    var voltageDropPercentage by remember { mutableStateOf<Double?>(null) }
    var error by remember { mutableStateOf<String?>(null) }

    val ctx = LocalContext.current

    fun calculate() {
        val v = voltage.toDoubleOrNull()
        val i = current.toDoubleOrNull()
        val l = wireLength.toDoubleOrNull()
        val d = wireDiameter.toDoubleOrNull()
        error = null

        if (v == null || i == null || l == null || d == null) {
            error = ctx.getString(R.string.error_all_fields_required)
            return
        }

        val resistivity = if (isCopper) COPPER_RESISTIVITY else ALUMINUM_RESISTIVITY
        val area = Math.PI * (d / 2000) * (d / 2000) // Convert mm to m and calculate area
        val resistance = (resistivity * l) / area
        val drop = i * resistance

        voltageDrop = drop
        voltageDropPercentage = (drop / v) * 100
    }

    Column(modifier = Modifier.padding(16.dp).fillMaxSize()) {
        OutlinedTextField(value = voltage, onValueChange = { voltage = it }, label = { Text(stringResource(R.string.source_voltage_v)) }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(value = current, onValueChange = { current = it }, label = { Text(stringResource(R.string.current_a)) }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(value = wireLength, onValueChange = { wireLength = it }, label = { Text(stringResource(R.string.wire_length_m)) }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(value = wireDiameter, onValueChange = { wireDiameter = it }, label = { Text(stringResource(R.string.wire_diameter_mm)) }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(16.dp))

        Row {
            RadioButton(selected = isCopper, onClick = { isCopper = true })
            Text(stringResource(R.string.copper), modifier = Modifier.padding(start = 8.dp))
            Spacer(modifier = Modifier.width(16.dp))
            RadioButton(selected = !isCopper, onClick = { isCopper = false })
            Text(stringResource(R.string.aluminum), modifier = Modifier.padding(start = 8.dp))
        }

        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { calculate() }, modifier = Modifier.fillMaxWidth()) {
            Text(stringResource(R.string.calculate_button))
        }

        error?.let {
            Text(it, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(top = 8.dp))
        }

        voltageDrop?.let {
            Text(stringResource(R.string.voltage_drop_result, it, voltageDropPercentage ?: 0.0), style = MaterialTheme.typography.headlineSmall, modifier = Modifier.padding(top = 16.dp))
        }
    }
}
