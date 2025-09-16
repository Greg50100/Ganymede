package com.joviansapps.ganymede.ui.screens.utilities.date

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.joviansapps.ganymede.R
import com.joviansapps.ganymede.viewmodel.DateCalculatorViewModel
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateCalculatorScreen(vm: DateCalculatorViewModel = viewModel()) {
    val uiState by vm.uiState.collectAsState()
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf(
        stringResource(id = R.string.date_calculator_tab_difference),
        stringResource(id = R.string.date_calculator_tab_add_subtract)
    )

    Column(modifier = Modifier.fillMaxSize()) {
        TabRow(selectedTabIndex = selectedTabIndex) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },
                    text = { Text(title) }
                )
            }
        }
        when (selectedTabIndex) {
            0 -> DifferenceCalculator(uiState = uiState, onEvent = vm::onEvent)
            1 -> AddSubtractCalculator(uiState = uiState, onEvent = vm::onEvent)
        }
    }
}

@Composable
private fun DifferenceCalculator(uiState: DateCalculatorViewModel.UiState, onEvent: (DateCalculatorViewModel.Event) -> Unit) {
    var showDatePicker1 by remember { mutableStateOf(false) }
    var showDatePicker2 by remember { mutableStateOf(false) }
    val formatter = remember { DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        DateDisplayField(
            label = stringResource(id = R.string.date_calculator_start_date),
            date = uiState.startDate,
            formatter = formatter
        ) { showDatePicker1 = true }

        DateDisplayField(
            label = stringResource(id = R.string.date_calculator_end_date),
            date = uiState.endDate,
            formatter = formatter
        ) { showDatePicker2 = true }

        uiState.differenceResult?.let { result ->
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(stringResource(id = R.string.result_title), style = MaterialTheme.typography.titleLarge)
                    Text(result)
                }
            }
        }
    }

    if (showDatePicker1) {
        DatePickerDialog(
            initialDate = uiState.startDate,
            onDismissRequest = { showDatePicker1 = false },
            onDateSelected = {
                onEvent(DateCalculatorViewModel.Event.SetStartDate(it))
                showDatePicker1 = false
            }
        )
    }
    if (showDatePicker2) {
        DatePickerDialog(
            initialDate = uiState.endDate,
            onDismissRequest = { showDatePicker2 = false },
            onDateSelected = {
                onEvent(DateCalculatorViewModel.Event.SetEndDate(it))
                showDatePicker2 = false
            }
        )
    }
}

@Composable
private fun AddSubtractCalculator(uiState: DateCalculatorViewModel.UiState, onEvent: (DateCalculatorViewModel.Event) -> Unit) {
    var showDatePicker by remember { mutableStateOf(false) }
    val formatter = remember { DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        DateDisplayField(
            label = stringResource(id = R.string.date_calculator_initial_date),
            date = uiState.addSubtractDate,
            formatter = formatter
        ) { showDatePicker = true }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = uiState.yearsToAdd,
                onValueChange = { onEvent(DateCalculatorViewModel.Event.SetYears(it)) },
                label = { Text(stringResource(id = R.string.date_calculator_years)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
                value = uiState.monthsToAdd,
                onValueChange = { onEvent(DateCalculatorViewModel.Event.SetMonths(it)) },
                label = { Text(stringResource(id = R.string.date_calculator_months)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
                value = uiState.daysToAdd,
                onValueChange = { onEvent(DateCalculatorViewModel.Event.SetDays(it)) },
                label = { Text(stringResource(id = R.string.date_calculator_days)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f)
            )
        }

        uiState.addSubtractResult?.let { result ->
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    Text(stringResource(id = R.string.date_calculator_new_date), style = MaterialTheme.typography.titleLarge)
                    Text(result.format(formatter), style = MaterialTheme.typography.headlineMedium)
                }
            }
        }
    }

    if (showDatePicker) {
        DatePickerDialog(
            initialDate = uiState.addSubtractDate,
            onDismissRequest = { showDatePicker = false },
            onDateSelected = {
                onEvent(DateCalculatorViewModel.Event.SetAddSubtractDate(it))
                showDatePicker = false
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DateDisplayField(label: String, date: LocalDate, formatter: DateTimeFormatter, onClick: () -> Unit) {
    Box {
        OutlinedTextField(
            value = date.format(formatter),
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            modifier = Modifier.fillMaxWidth(),
            trailingIcon = {
                Icon(
                    painter = painterResource(id = android.R.drawable.ic_menu_my_calendar),
                    contentDescription = "Select date"
                )
            }
        )
        // Boîte transparente superposée pour garantir la capture du clic
        Box(
            modifier = Modifier
                .matchParentSize()
                .clickable(onClick = onClick)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerDialog(
    initialDate: LocalDate,
    onDismissRequest: () -> Unit,
    onDateSelected: (LocalDate) -> Unit
) {
    val initialMillis = remember { initialDate.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli() }
    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = initialMillis)

    androidx.compose.material3.DatePickerDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            Button(onClick = {
                datePickerState.selectedDateMillis?.let { millis ->
                    val selectedDate = Instant.ofEpochMilli(millis).atZone(ZoneOffset.UTC).toLocalDate()
                    onDateSelected(selectedDate)
                }
                onDismissRequest()
            }) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text("Cancel")
            }
        }
    ) {
        DatePicker(state = datePickerState)
    }
}

