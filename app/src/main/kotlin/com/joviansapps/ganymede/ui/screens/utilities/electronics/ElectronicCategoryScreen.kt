package com.joviansapps.ganymede.ui.screens.utilities.electronics

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.joviansapps.ganymede.R

@Composable
fun ElectronicCategoryScreen(
    onOpenResistorCalculator: () -> Unit,
    onOpenInductanceCalculator: () -> Unit,
    onOpenCondensatorChargeCalculator: () -> Unit,
    onOpenParallelSeriesResistorCalculator: () -> Unit,
    onOpenParallelSeriesCapacitorCalculator: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Button(onClick = onOpenResistorCalculator) {
            Text(stringResource(id = R.string.resistor_calculator_title))
        }
        Button(onClick = onOpenInductanceCalculator) {
            Text(stringResource(id = R.string.inductance_calculator_title))
        }
        Button(onClick = onOpenCondensatorChargeCalculator) {
            Text(stringResource(id = R.string.time_constant_calculator_title))
        }
        Button(onClick = onOpenParallelSeriesResistorCalculator) {
            Text(stringResource(id = R.string.parallel_series_resistor_calculator_title))
        }
        Button(onClick = onOpenParallelSeriesCapacitorCalculator) {
            Text(stringResource(id = R.string.parallel_series_capacitor_calculator_title))
        }
    }
}
