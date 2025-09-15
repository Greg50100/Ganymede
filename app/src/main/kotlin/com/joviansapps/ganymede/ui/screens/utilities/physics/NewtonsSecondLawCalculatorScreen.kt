package com.joviansapps.ganymede.ui.screens.utilities.physics

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
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.joviansapps.ganymede.R
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class NewtonsLawUiState(
    val force: String = "",
    val mass: String = "",
    val acceleration: String = ""
)

class NewtonsLawViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(NewtonsLawUiState())
    val uiState = _uiState.asStateFlow()
    private var lastEdited: Char? = null

    fun onForceChange(value: String) { _uiState.update { it.copy(force = value) }; lastEdited = 'f'; calculate() }
    fun onMassChange(value: String) { _uiState.update { it.copy(mass = value) }; lastEdited = 'm'; calculate() }
    fun onAccelerationChange(value: String) { _uiState.update { it.copy(acceleration = value) }; lastEdited = 'a'; calculate() }

    private fun calculate() {
        viewModelScope.launch {
            val f = _uiState.value.force.toDoubleOrNull()
            val m = _uiState.value.mass.toDoubleOrNull()
            val a = _uiState.value.acceleration.toDoubleOrNull()

            when (lastEdited) {
                'f' -> if (m != null && a != null) _uiState.update { it.copy(force = (m * a).toString()) }
                'm' -> if (f != null && a != null && a != 0.0) _uiState.update { it.copy(mass = (f / a).toString()) }
                'a' -> if (f != null && m != null && m != 0.0) _uiState.update { it.copy(acceleration = (f / m).toString()) }
            }
        }
    }
}

@Composable
fun NewtonsSecondLawCalculatorScreen(viewModel: NewtonsLawViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp).verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(stringResource(R.string.newtons_second_law_title), style = MaterialTheme.typography.headlineSmall)
        Text(stringResource(R.string.newtons_second_law_formula), style = MaterialTheme.typography.titleMedium)
        OutlinedTextField(value = uiState.force, onValueChange = viewModel::onForceChange, label = { Text(stringResource(R.string.force_newtons)) }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = uiState.mass, onValueChange = viewModel::onMassChange, label = { Text(stringResource(R.string.mass_kg)) }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = uiState.acceleration, onValueChange = viewModel::onAccelerationChange, label = { Text(stringResource(R.string.acceleration_ms2)) }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth())
    }
}
