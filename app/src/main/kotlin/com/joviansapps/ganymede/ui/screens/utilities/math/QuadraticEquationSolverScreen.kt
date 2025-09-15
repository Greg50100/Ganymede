package com.joviansapps.ganymede.ui.screens.utilities.math

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
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
import kotlin.math.pow
import kotlin.math.sqrt

// --- 1. UI State ---
// Represents the state of the screen
data class QuadraticEquationUiState(
    val a: String = "",
    val b: String = "",
    val c: String = "",
    val result: String = ""
)

// --- 2. ViewModel ---
// Handles the logic of the screen
class QuadraticEquationViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(QuadraticEquationUiState())
    val uiState = _uiState.asStateFlow()

    fun onAChanged(value: String) {
        _uiState.update { it.copy(a = value) }
    }

    fun onBChanged(value: String) {
        _uiState.update { it.copy(b = value) }
    }

    fun onCChanged(value: String) {
        _uiState.update { it.copy(c = value) }
    }

    fun solve() {
        viewModelScope.launch {
            val state = _uiState.value
            val valA = state.a.toDoubleOrNull()
            val valB = state.b.toDoubleOrNull()
            val valC = state.c.toDoubleOrNull()

            val newResult = if (valA != null && valB != null && valC != null) {
                if (valA == 0.0) {
                    "Coefficient 'a' cannot be zero."
                } else {
                    val discriminant = valB.pow(2) - 4 * valA * valC
                    val discriminantText = "Discriminant (Δ) = $discriminant\n"
                    when {
                        discriminant > 0 -> {
                            val root1 = (-valB + sqrt(discriminant)) / (2 * valA)
                            val root2 = (-valB - sqrt(discriminant)) / (2 * valA)
                            discriminantText + "Two real roots:\nx1 = $root1\nx2 = $root2"
                        }
                        discriminant == 0.0 -> {
                            val root = -valB / (2 * valA)
                            discriminantText + "One real root:\nx = $root"
                        }
                        else -> {
                            val realPart = -valB / (2 * valA)
                            val imaginaryPart = sqrt(-discriminant) / (2 * valA)
                            discriminantText + "Two complex roots:\nx1 = $realPart + ${imaginaryPart}i\nx2 = $realPart - ${imaginaryPart}i"
                        }
                    }
                }
            } else {
                "Please enter valid numbers for a, b, and c."
            }
            _uiState.update { it.copy(result = newResult) }
        }
    }
}

// --- 3. Composable Screen ---
// The UI is now stateless and driven by the ViewModel
@Composable
fun QuadraticEquationSolverScreen(viewModel: QuadraticEquationViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            stringResource(R.string.quadratic_equation_formula),
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(modifier = Modifier.height(8.dp))

        // Input fields are now dumb components
        OutlinedTextField(
            value = uiState.a,
            onValueChange = viewModel::onAChanged,
            label = { Text(stringResource(R.string.coefficient_a)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next),
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = uiState.b,
            onValueChange = viewModel::onBChanged,
            label = { Text(stringResource(R.string.coefficient_b)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next),
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = uiState.c,
            onValueChange = viewModel::onCChanged,
            label = { Text(stringResource(R.string.coefficient_c)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = { viewModel.solve() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.calculate))
        }

        if (uiState.result.isNotEmpty()) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = uiState.result,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}
