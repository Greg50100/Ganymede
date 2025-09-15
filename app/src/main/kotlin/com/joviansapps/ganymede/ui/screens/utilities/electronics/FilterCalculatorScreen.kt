package com.joviansapps.ganymede.ui.screens.utilities.electronics

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.joviansapps.ganymede.R
import kotlin.math.PI

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterCalculatorScreen() {
    var resistance by remember { mutableStateOf("") }
    var capacitance by remember { mutableStateOf("") }
    var inductance by remember { mutableStateOf("") }
    var frequency by remember { mutableStateOf("") }
    var filterCircuitType by remember { mutableStateOf(0) } // 0 for RC, 1 for RL
    var filterPassType by remember { mutableStateOf(0) } // 0 for Low-Pass, 1 for High-Pass
    var error by remember { mutableStateOf<String?>(null) }

    fun calculate(errorMessageNeeded: String) {
        error = null
        val r = resistance.toDoubleOrNull()
        val c = capacitance.toDoubleOrNull()
        val l = inductance.toDoubleOrNull()
        val f = frequency.toDoubleOrNull()

        val inputs = listOf(r, if (filterCircuitType == 0) c else l, f).filterNotNull()
        if (inputs.size != 2) {
            error = errorMessageNeeded
            return
        }

        if (filterCircuitType == 0) { // RC Filter
            when {
                r != null && c != null -> frequency = (1 / (2 * PI * r * c)).toString()
                r != null && f != null -> capacitance = (1 / (2 * PI * r * f)).toString()
                c != null && f != null -> resistance = (1 / (2 * PI * c * f)).toString()
            }
        } else { // RL Filter
            when {
                r != null && l != null -> frequency = (r / (2 * PI * l)).toString()
                r != null && f != null -> inductance = (r / (2 * PI * f)).toString()
                l != null && f != null -> resistance = (l * (2 * PI * f)).toString()
            }
        }
    }

    val circuitDrawable = when(filterCircuitType) {
        0 -> if (filterPassType == 0) R.drawable.ic_rc_low_pass_filter else R.drawable.ic_rc_high_pass_filter
        else -> if (filterPassType == 0) R.drawable.ic_rl_low_pass_filter else R.drawable.ic_rl_high_pass_filter
    }

    val errorTwoValuesNeeded = stringResource(id = R.string.error_two_values_needed)

    Column(modifier = Modifier.padding(16.dp)) {
        // Tabs for RC / RL
        TabRow(selectedTabIndex = filterCircuitType) {
            Tab(selected = filterCircuitType == 0, onClick = { filterCircuitType = 0 }, text = { Text(stringResource(R.string.rc_filter_tab)) })
            Tab(selected = filterCircuitType == 1, onClick = { filterCircuitType = 1 }, text = { Text(stringResource(R.string.rl_filter_tab)) })
        }
        Spacer(modifier = Modifier.height(8.dp))
        // Tabs for Low-Pass / High-Pass
        TabRow(selectedTabIndex = filterPassType) {
            Tab(selected = filterPassType == 0, onClick = { filterPassType = 0 }, text = { Text(stringResource(R.string.low_pass_filter_tab)) })
            Tab(selected = filterPassType == 1, onClick = { filterPassType = 1 }, text = { Text(stringResource(R.string.high_pass_filter_tab)) })
        }


        Spacer(modifier = Modifier.height(16.dp))

        // Circuit Diagram
        Icon(
            painter = painterResource(id = circuitDrawable),
            contentDescription = "Filter Circuit Diagram",
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .padding(vertical = 8.dp),
            tint = MaterialTheme.colorScheme.onSurface
        )

        OutlinedTextField(
            value = resistance,
            onValueChange = { resistance = it },
            label = { Text(stringResource(R.string.resistance_label)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        if (filterCircuitType == 0) {
            OutlinedTextField(
                value = capacitance,
                onValueChange = { capacitance = it },
                label = { Text(stringResource(R.string.capacitance_label)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
        } else {
            OutlinedTextField(
                value = inductance,
                onValueChange = { inductance = it },
                label = { Text(stringResource(R.string.inductance_label)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
        }
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = frequency,
            onValueChange = { frequency = it },
            label = { Text(stringResource(R.string.frequency_label)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        if (error != null) {
            Text(text = error!!, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(bottom = 8.dp))
        }

        Button(onClick = { calculate(errorTwoValuesNeeded) }, modifier = Modifier.fillMaxWidth()) {
            Text(stringResource(R.string.calculate_button))
        }
    }
}

