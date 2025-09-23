package com.joviansapps.ganymede.ui.screens.utilities.math

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.joviansapps.ganymede.R
import com.joviansapps.ganymede.ui.components.ResultField
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.math.BigInteger

data class GcdLcmUiState(
    val numberA: String = "",
    val numberB: String = "",
    val gcd: BigInteger? = null,
    val lcm: BigInteger? = null
)

class GcdLcmViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(GcdLcmUiState())
    val uiState = _uiState.asStateFlow()

    fun onNumberAChange(value: String) { _uiState.update { it.copy(numberA = value) }; calculate() }
    fun onNumberBChange(value: String) { _uiState.update { it.copy(numberB = value) }; calculate() }

    private fun calculate() {
        viewModelScope.launch {
            val numA = _uiState.value.numberA.toBigIntegerOrNull()
            val numB = _uiState.value.numberB.toBigIntegerOrNull()

            if (numA != null && numB != null && numA > BigInteger.ZERO && numB > BigInteger.ZERO) {
                val gcd = numA.gcd(numB)
                val lcm = (numA * numB) / gcd
                _uiState.update { it.copy(gcd = gcd, lcm = lcm) }
            } else {
                _uiState.update { it.copy(gcd = null, lcm = null) }
            }
        }
    }
}

@Composable
fun GcdLcmCalculatorScreen(viewModel: GcdLcmViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(stringResource(R.string.gcd_lcm_calculator_title), style = MaterialTheme.typography.headlineSmall)
        OutlinedTextField(value = uiState.numberA, onValueChange = viewModel::onNumberAChange, label = { Text(stringResource(R.string.first_number)) }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = uiState.numberB, onValueChange = viewModel::onNumberBChange, label = { Text(stringResource(R.string.second_number)) }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth())

        if (uiState.gcd != null && uiState.lcm != null) {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(stringResource(R.string.results_title), style = MaterialTheme.typography.titleLarge)
                    Divider(modifier = Modifier.padding(vertical = 8.dp))
                    ResultField(label = stringResource(id = R.string.gcd_result_label), value = uiState.gcd.toString())
                    ResultField(label = stringResource(id = R.string.lcm_result_label), value = uiState.lcm.toString())
                }
            }
        }
    }
}
