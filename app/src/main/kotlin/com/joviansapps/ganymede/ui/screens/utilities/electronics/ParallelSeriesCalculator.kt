package com.joviansapps.ganymede.ui.screens.utilities.electronics.common

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import com.joviansapps.ganymede.R
import java.text.DecimalFormat

// --- ViewModel ---
abstract class ParallelSeriesViewModel : ViewModel() {
    // Use Compose State inside the ViewModel so composables can read them directly
    protected val _values = mutableStateOf<List<String>>(listOf("", ""))
    val values: State<List<String>> get() = _values

    protected val _seriesResult = mutableStateOf<Double?>(null)
    val seriesResult: State<Double?> get() = _seriesResult

    protected val _parallelResult = mutableStateOf<Double?>(null)
    val parallelResult: State<Double?> get() = _parallelResult

    fun updateValue(index: Int, value: String) {
        // _values.value is non-null (List<String>), no safe-call needed
        val currentValues = _values.value.toMutableList()
        if (index < currentValues.size) {
            currentValues[index] = value
            _values.value = currentValues
            calculate()
        }
    }

    fun addValue() {
        val currentValues = _values.value.toMutableList()
        currentValues.add("")
        _values.value = currentValues
        calculate()
    }

    fun removeValue(index: Int) {
        val currentValues = _values.value.toMutableList()
        if (currentValues.size > 2 && index < currentValues.size) {
            currentValues.removeAt(index)
            _values.value = currentValues
            calculate()
        }
    }

    protected abstract fun calculate()
}


// --- Composable UI ---
@Composable
fun ParallelSeriesCalculatorScreen(
    viewModel: ParallelSeriesViewModel,
    componentName: String,
    unit: String
) {
    // Read State<T> from the ViewModel directly (no LiveData/observeAsState)
    val values by viewModel.values
    val seriesResult by viewModel.seriesResult
    val parallelResult by viewModel.parallelResult
    val formatter = DecimalFormat("#.####")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Results Section
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                val seriesText = seriesResult?.let { formatter.format(it) } ?: "N/A"
                val parallelText = parallelResult?.let { formatter.format(it) } ?: "N/A"

                Text(stringResource(R.string.results_title), style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(8.dp))
                Text(stringResource(R.string.series_result, seriesText, unit))
                Text(stringResource(R.string.parallel_result, parallelText, unit))
            }
        }

        Spacer(Modifier.height(16.dp))

        // Input List
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            itemsIndexed(values) { index, value ->
                ValueInputRow(
                    index = index,
                    value = value,
                    onValueChange = { viewModel.updateValue(index, it) },
                    onRemove = { viewModel.removeValue(index) },
                    canRemove = values.size > 2,
                    componentName = componentName,
                    unit = unit
                )
            }
        }

        // Add Button
        Button(
            onClick = { viewModel.addValue() },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text(stringResource(R.string.add_component, componentName))
        }
    }
}

@Composable
private fun ValueInputRow(
    index: Int,
    value: String,
    onValueChange: (String) -> Unit,
    onRemove: () -> Unit,
    canRemove: Boolean,
    componentName: String,
    unit: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(stringResource(R.string.component_value, componentName, index + 1, unit)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.weight(1f)
        )
        if (canRemove) {
            IconButton(onClick = onRemove) {
                Icon(Icons.Default.Delete, contentDescription = stringResource(R.string.remove_component, componentName))
            }
        }
    }
}
