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

@Composable
fun EnergyCostCalculatorScreen() {
    var powerConsumption by remember { mutableStateOf("") }
    var usageHoursPerDay by remember { mutableStateOf("") }
    var costPerKwh by remember { mutableStateOf("") }

    var costPerDay by remember { mutableStateOf<Double?>(null) }
    var costPerMonth by remember { mutableStateOf<Double?>(null) }
    var costPerYear by remember { mutableStateOf<Double?>(null) }
    var error by remember { mutableStateOf<String?>(null) }

    val ctx = LocalContext.current

    fun calculate() {
        val powerW = powerConsumption.toDoubleOrNull()
        val hours = usageHoursPerDay.toDoubleOrNull()
        val cost = costPerKwh.toDoubleOrNull()
        error = null

        if (powerW == null || hours == null || cost == null) {
            error = ctx.getString(R.string.error_all_fields_required)
            return
        }

        val kwhPerDay = (powerW * hours) / 1000.0
        val dayCost = kwhPerDay * cost

        costPerDay = dayCost
        costPerMonth = dayCost * 30.44 // Average days in a month
        costPerYear = dayCost * 365.25 // Average days in a year
    }

    Column(modifier = Modifier.padding(16.dp).fillMaxSize()) {
        OutlinedTextField(value = powerConsumption, onValueChange = { powerConsumption = it }, label = { Text(stringResource(R.string.power_consumption_w)) }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(value = usageHoursPerDay, onValueChange = { usageHoursPerDay = it }, label = { Text(stringResource(R.string.usage_hours_per_day)) }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(value = costPerKwh, onValueChange = { costPerKwh = it }, label = { Text(stringResource(R.string.cost_per_kwh)) }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = { calculate() }, modifier = Modifier.fillMaxWidth()) {
            Text(stringResource(R.string.calculate_button))
        }

        error?.let {
            Text(it, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(top = 8.dp))
        }

        costPerDay?.let {
            Column(modifier = Modifier.padding(top = 16.dp)) {
                Text(stringResource(R.string.estimated_cost), style = MaterialTheme.typography.headlineSmall)
                Text(stringResource(R.string.cost_per_day, it))
                Text(stringResource(R.string.cost_per_month, costPerMonth ?: 0.0))
                Text(stringResource(R.string.cost_per_year, costPerYear ?: 0.0))
            }
        }
    }
}
