package com.joviansapps.ganymede.ui.screens.utilities.electronics

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.joviansapps.ganymede.R
import com.joviansapps.ganymede.ui.components.ResultField
import java.text.DecimalFormat
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class BjtBiasingUiState(
    val vcc: String = "12",
    val r1: String = "10k",
    val r2: String = "2.2k",
    val rc: String = "1k",
    val re: String = "100",
    val beta: String = "100",
    val vbe: String = "0.7",
    val vb: Double? = null,
    val ve: Double? = null,
    val ie: Double? = null,
    val ic: Double? = null,
    val vce: Double? = null
)

class BjtBiasingViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(BjtBiasingUiState())
    val uiState = _uiState.asStateFlow()

    init { calculate() }

    fun onValueChange(field: String, value: String) {
        _uiState.update {
            when (field) {
                "vcc" -> it.copy(vcc = value)
                "r1" -> it.copy(r1 = value)
                "r2" -> it.copy(r2 = value)
                "rc" -> it.copy(rc = value)
                "re" -> it.copy(re = value)
                "beta" -> it.copy(beta = value)
                "vbe" -> it.copy(vbe = value)
                else -> it
            }
        }
        calculate()
    }

    private fun parseValue(str: String): Double? {
        val valueString = str.lowercase().trim()
        if (valueString.isEmpty()) return null
        var multiplier = 1.0
        val lastChar = valueString.last()
        if (!lastChar.isDigit()) {
            when (lastChar) {
                'k' -> multiplier = 1e3
                'm' -> multiplier = 1e6
            }
            return valueString.dropLast(1).toDoubleOrNull()?.times(multiplier)
        }
        return valueString.toDoubleOrNull()
    }

    private fun calculate() {
        viewModelScope.launch {
            val s = _uiState.value
            val vcc = s.vcc.toDoubleOrNull()
            val r1 = parseValue(s.r1)
            val r2 = parseValue(s.r2)
            val rc = parseValue(s.rc)
            val re = parseValue(s.re)
            val beta = s.beta.toDoubleOrNull()
            val vbe = s.vbe.toDoubleOrNull()

            if (vcc == null || r1 == null || r2 == null || rc == null || re == null || beta == null || vbe == null) {
                _uiState.update { it.copy(vb = null, ve = null, ie = null, ic = null, vce = null) }
                return@launch
            }

            val vb = vcc * (r2 / (r1 + r2))
            val ve = vb - vbe
            val ie = ve / re
            val ic = ie * (beta / (beta + 1))
            val vc = vcc - (ic * rc)
            val vce = vc - ve

            _uiState.update { it.copy(vb = vb, ve = ve, ie = ie, ic = ic, vce = vce) }
        }
    }
}

@Composable
@Preview
fun BjtBiasingCalculatorScreen(viewModel: BjtBiasingViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Image(
            painter = androidx.compose.ui.res.painterResource(id = R.drawable.bjt_biasing),
            contentDescription = "BJT Biasing",
            contentScale = ContentScale.FillWidth,
            modifier = Modifier
                .fillMaxWidth(),
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary)
        )

        Text("Pour configuration diviseur de tension", style = MaterialTheme.typography.bodyMedium)

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(value = uiState.vcc, onValueChange = { viewModel.onValueChange("vcc", it) }, label = { Text("Vcc (V)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.weight(1f))
            OutlinedTextField(value = uiState.beta, onValueChange = { viewModel.onValueChange("beta", it) }, label = { Text("Beta (hFE)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.weight(1f))
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(value = uiState.r1, onValueChange = { viewModel.onValueChange("r1", it) }, label = { Text("R1 (Ω)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text), modifier = Modifier.weight(1f))
            OutlinedTextField(value = uiState.r2, onValueChange = { viewModel.onValueChange("r2", it) }, label = { Text("R2 (Ω)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text), modifier = Modifier.weight(1f))
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(value = uiState.rc, onValueChange = { viewModel.onValueChange("rc", it) }, label = { Text("Rc (Ω)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text), modifier = Modifier.weight(1f))
            OutlinedTextField(value = uiState.re, onValueChange = { viewModel.onValueChange("re", it) }, label = { Text("Re (Ω)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text), modifier = Modifier.weight(1f))
        }

        if (uiState.ic != null) {
            val formatter = DecimalFormat("#.###")
            val resultsText = buildString {
                append("Point de Fonctionnement (Q):\n")
                append("Ic: ${uiState.ic?.let { formatter.format(it) + " A" } ?: "N/A"}\n")
                append("Vce: ${uiState.vce?.let { formatter.format(it) + " V" } ?: "N/A"}\n\n")

                append("Tensions de Nœud:\n")
                append("Vb: ${uiState.vb?.let { formatter.format(it) + " V" } ?: "N/A"}\n")
                append("Ve: ${uiState.ve?.let { formatter.format(it) + " V" } ?: "N/A"}")
            }

            ResultField(
                label = stringResource(id = R.string.results_title),
                value = resultsText,
                modifier = Modifier.fillMaxWidth(),
                maxLines = 6
            )
        }
    }
}
