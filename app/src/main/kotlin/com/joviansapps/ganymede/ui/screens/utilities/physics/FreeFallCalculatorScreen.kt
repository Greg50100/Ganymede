package com.joviansapps.ganymede.ui.screens.utilities.physics

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.joviansapps.ganymede.R
import kotlin.math.sqrt

private const val GRAVITY = 9.80665 // Standard gravity in m/s²

@Composable
fun FreeFallCalculatorScreen() {
    var heightInput by remember { mutableStateOf("") }
    var finalVelocity by remember { mutableStateOf<Double?>(null) }
    var time by remember { mutableStateOf<Double?>(null) }
    var error by remember { mutableStateOf<String?>(null) }

    fun calculate() {
        val height = heightInput.toDoubleOrNull()
        if (height == null || height < 0) {
            error = "Please enter a valid positive height."
            finalVelocity = null
            time = null
            return
        }
        error = null
        finalVelocity = sqrt(2 * GRAVITY * height)
        time = sqrt(2 * height / GRAVITY)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.free_fall_calculator_description),
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        OutlinedTextField(
            value = heightInput,
            onValueChange = { heightInput = it },
            label = { Text(stringResource(R.string.height_m_label)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { calculate() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.calculate_button))
        }

        if (error != null) {
            Text(
                text = error!!,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 16.dp)
            )
        }

        if (finalVelocity != null && time != null) {
            Divider(modifier = Modifier.padding(vertical = 24.dp))

            Text(
                text = stringResource(R.string.results_title),
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            ResultRow(
                label = stringResource(R.string.final_velocity_label),
                value = "%.2f m/s".format(finalVelocity)
            )
            ResultRow(
                label = stringResource(R.string.time_of_fall_label),
                value = "%.2f s".format(time)
            )
        }
    }
}

@Composable
private fun ResultRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyLarge)
        Text(text = value, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.primary)
    }
}
