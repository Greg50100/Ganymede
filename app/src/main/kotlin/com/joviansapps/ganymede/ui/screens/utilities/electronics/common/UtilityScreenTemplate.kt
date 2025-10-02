package com.joviansapps.ganymede.ui.screens.utilities.common

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import com.joviansapps.ganymede.R
import com.joviansapps.ganymede.ui.components.ResultField
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.DecimalFormat

// 1. DÉFINIR L'ÉTAT DE L'UI (À PERSONNALISER)
data class UtilityUiState<T>(
    val inputs: Map<String, String> = emptyMap(),
    val result: T? = null,
    val error: String? = null
)

// 2. CRÉER UN VIEWMODEL ABSTRAIT
abstract class UtilityViewModel<T>(initialInputs: Map<String, String>) : ViewModel() {
    private val _uiState = MutableStateFlow(UtilityUiState<T>(inputs = initialInputs))
    val uiState = _uiState.asStateFlow()

    fun onInputChange(key: String, value: String) {
        val newInputs = _uiState.value.inputs.toMutableMap()
        newInputs[key] = value
        _uiState.update { it.copy(inputs = newInputs) }
        calculate()
    }

    private fun calculate() {
        viewModelScope.launch {
            try {
                val result = performCalculation(_uiState.value.inputs)
                _uiState.update { it.copy(result = result, error = null) }
            } catch (e: Exception) {
                _uiState.update { it.copy(result = null, error = e.message ?: "Invalid input") }
            }
        }
    }

    abstract fun performCalculation(inputs: Map<String, String>): T?
}

// 3. CRÉER LE COMPOSABLE GÉNÉRIQUE
@Composable
fun <T> UtilityScreen(
    title: String,
    viewModel: UtilityViewModel<T>,
    inputFields: @Composable (uiState: UtilityUiState<T>, onInputChange: (String, String) -> Unit) -> Unit,
    resultContent: @Composable (result: T) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(title, style = MaterialTheme.typography.headlineSmall)

        // Champs de saisie spécifiques à la calculatrice
        inputFields(uiState, viewModel::onInputChange)

        // Affichage des résultats ou des erreurs
        uiState.error?.let {
            Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodyLarge)
        }

        uiState.result?.let { result ->
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    Text(stringResource(R.string.results_title), style = MaterialTheme.typography.titleLarge)
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                    resultContent(result)
                }
            }
        }
    }
}