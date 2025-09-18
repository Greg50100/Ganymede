package com.joviansapps.ganymede.ui.screens.utilities.date

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.joviansapps.ganymede.R
import com.joviansapps.ganymede.ui.components.ResultRow
import com.joviansapps.ganymede.viewmodel.DateCalculatorViewModel
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@Composable
fun EasterCalculatorScreen(vm: DateCalculatorViewModel = viewModel()) {
    // Use collectAsState().value to get a stable uiState instance for Compose
    val uiState = vm.uiState.collectAsState().value
    val formatter = remember { DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            stringResource(id = R.string.easter_calculator_description),
            style = MaterialTheme.typography.bodyLarge
        )

        OutlinedTextField(
            value = uiState.easterYear,
            onValueChange = { newYear -> vm.onEvent(DateCalculatorViewModel.Event.SetEasterYear(newYear)) },
            label = { Text(stringResource(id = R.string.year_label)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        val easterResult = uiState.easterDateResult
        if (easterResult != null) {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    Text(
                        text = stringResource(id = R.string.results_title),
                        style = MaterialTheme.typography.titleLarge
                    )
                    Divider(modifier = Modifier.padding(vertical = 8.dp))
                    ResultRow(
                        label = stringResource(id = R.string.easter_date_result_label),
                        value = easterResult.format(formatter)
                    )
                }
            }
        }
    }
}
