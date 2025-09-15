package com.joviansapps.ganymede.ui.screens.utilities.math

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
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

// --- 1. UI State & Model ---
enum class PercentMode {
    PERCENT_OF, IS_WHAT_PERCENT, CHANGE
}

data class PercentageUiState(
    val valA: String = "20",
    val valB: String = "50",
    val result: String? = null,
    val mode: PercentMode = PercentMode.PERCENT_OF
)

// --- 2. ViewModel ---
class PercentageViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(PercentageUiState())
    val uiState = _uiState.asStateFlow()

    init {
        calculate()
    }

    fun onValAChange(value: String) {
        _uiState.update { it.copy(valA = value) }
        calculate()
    }

    fun onValBChange(value: String) {
        _uiState.update { it.copy(valB = value) }
        calculate()
    }

    fun onModeChange(mode: PercentMode) {
        _uiState.update { it.copy(mode = mode) }
        calculate()
    }

    private fun calculate() {
        viewModelScope.launch {
            val a = _uiState.value.valA.toDoubleOrNull()
            val b = _uiState.value.valB.toDoubleOrNull()
            val formatter = DecimalFormat("#.##")

            if (a == null || b == null) {
                _uiState.update { it.copy(result = null) }
                return@launch
            }

            val res = when (_uiState.value.mode) {
                PercentMode.PERCENT_OF -> "${formatter.format(a / 100 * b)}"
                PercentMode.IS_WHAT_PERCENT -> if (b != 0.0) "${formatter.format(a / b * 100)}%" else "N/A"
                PercentMode.CHANGE -> if (a != 0.0) "${formatter.format((b - a) / a * 100)}%" else "N/A"
            }
            _uiState.update { it.copy(result = res) }
        }
    }
}

// --- 3. Composable Screen ---
@Composable
fun PercentageCalculatorScreen(viewModel: PercentageViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        TabRow(selectedTabIndex = uiState.mode.ordinal) {
            Tab(selected = uiState.mode == PercentMode.PERCENT_OF, onClick = { viewModel.onModeChange(PercentMode.PERCENT_OF) }, text = { Text("X % of Y") })
            Tab(selected = uiState.mode == PercentMode.IS_WHAT_PERCENT, onClick = { viewModel.onModeChange(PercentMode.IS_WHAT_PERCENT) }, text = { Text("X is what % of Y") })
            Tab(selected = uiState.mode == PercentMode.CHANGE, onClick = { viewModel.onModeChange(PercentMode.CHANGE) }, text = { Text("% Change") })
        }
        Spacer(Modifier.height(16.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = uiState.valA,
                onValueChange = viewModel::onValAChange,
                label = { Text("Value A") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
                value = uiState.valB,
                onValueChange = viewModel::onValBChange,
                label = { Text("Value B") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f)
            )
        }

        uiState.result?.let {
            Text(
                text = "Result: $it",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(top = 16.dp)
            )
        }
    }
}
