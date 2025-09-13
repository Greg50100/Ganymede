package com.joviansapps.ganymede.ui.screens.utilities.electronics

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale
import java.util.UUID

// ---------------------------------------------------
// 1. ÉTAT DE L'UI & ÉVÉNEMENTS
// ---------------------------------------------------

// NOTE: On suppose que EnumSymbolProvider et CapacitanceUnit sont déjà définis
// dans un autre fichier du package (ex: TimeConstantCalculatorScreen.kt) pour éviter la redéclaration.

data class CapacitorInput(
    val id: UUID = UUID.randomUUID(),
    val value: String = "",
    val unit: CapacitanceUnit = CapacitanceUnit.µF // Unité par défaut
)

data class CapacitorCalculatorUiState(
    val capacitors: List<CapacitorInput> = listOf(CapacitorInput(), CapacitorInput()), // Commence avec 2 champs
    val totalCapacitance: Double? = null
)

sealed interface CapacitorCalculatorEvent {
    data class CapacitanceChanged(val id: UUID, val value: String) : CapacitorCalculatorEvent
    data class UnitChanged(val id: UUID, val unit: CapacitanceUnit) : CapacitorCalculatorEvent
    object AddCapacitor : CapacitorCalculatorEvent
    data class RemoveCapacitor(val id: UUID) : CapacitorCalculatorEvent
    object Reset : CapacitorCalculatorEvent
}


// ---------------------------------------------------
// 2. VIEWMODELS
// ---------------------------------------------------

abstract class BaseCapacitorCalculatorViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(CapacitorCalculatorUiState())
    val uiState = _uiState.asStateFlow()

    protected abstract fun performCalculation(capacitorsInFarads: List<Double>): Double

    fun onEvent(event: CapacitorCalculatorEvent) {
        val currentState = _uiState.value
        when (event) {
            is CapacitorCalculatorEvent.AddCapacitor -> {
                _uiState.update { it.copy(capacitors = it.capacitors + CapacitorInput()) }
            }
            is CapacitorCalculatorEvent.RemoveCapacitor -> {
                if (currentState.capacitors.size > 2) {
                    _uiState.update { it.copy(capacitors = it.capacitors.filter { cap -> cap.id != event.id }) }
                }
            }
            is CapacitorCalculatorEvent.CapacitanceChanged -> {
                val newList = currentState.capacitors.map { if (it.id == event.id) it.copy(value = event.value) else it }
                _uiState.update { it.copy(capacitors = newList) }
            }
            is CapacitorCalculatorEvent.UnitChanged -> {
                val newList = currentState.capacitors.map { if (it.id == event.id) it.copy(unit = event.unit) else it }
                _uiState.update { it.copy(capacitors = newList) }
            }
            is CapacitorCalculatorEvent.Reset -> {
                _uiState.value = CapacitorCalculatorUiState()
            }
        }
        calculate()
    }

    private fun calculate() {
        viewModelScope.launch {
            val validCapacitors = _uiState.value.capacitors
                .mapNotNull { input ->
                    input.value.toDoubleOrNull()?.let { value ->
                        if (value > 0) value * input.unit.multiplier else null
                    }
                }

            if (validCapacitors.isNotEmpty()) {
                val total = performCalculation(validCapacitors)
                _uiState.update { it.copy(totalCapacitance = total) }
            } else {
                _uiState.update { it.copy(totalCapacitance = null) }
            }
        }
    }
}

/**
 * ViewModel pour le calcul en SÉRIE.
 * Formule : 1 / (1/C1 + 1/C2 + ...)
 */
class SeriesCapacitorViewModel : BaseCapacitorCalculatorViewModel() {
    override fun performCalculation(capacitorsInFarads: List<Double>): Double {
        return 1.0 / capacitorsInFarads.sumOf { 1.0 / it }
    }
}

/**
 * ViewModel pour le calcul en PARALLÈLE.
 * Formule : C1 + C2 + ...
 */
class ParallelCapacitorViewModel : BaseCapacitorCalculatorViewModel() {
    override fun performCalculation(capacitorsInFarads: List<Double>): Double {
        return capacitorsInFarads.sum()
    }
}


// ---------------------------------------------------
// 3. VUES (COMPOSABLES)
// ---------------------------------------------------

@Composable
fun ParallelSeriesCapacitorCalculatorScreen() {
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabTitles = listOf("Série", "Parallèle")

    Column(modifier = Modifier.fillMaxSize()) {
        TabRow(selectedTabIndex = selectedTabIndex) {
            tabTitles.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },
                    text = { Text(title) }
                )
            }
        }
        when (selectedTabIndex) {
            0 -> SeriesCalculatorTab()
            1 -> ParallelCalculatorTab()
        }
    }
}

@Composable
private fun SeriesCalculatorTab(viewModel: SeriesCapacitorViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    CalculatorTabContent(
        title = "Capacité équivalente en Série",
        uiState = uiState,
        onEvent = viewModel::onEvent
    )
}

@Composable
private fun ParallelCalculatorTab(viewModel: ParallelCapacitorViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    CalculatorTabContent(
        title = "Capacité équivalente en Parallèle",
        uiState = uiState,
        onEvent = viewModel::onEvent
    )
}

@Composable
private fun CalculatorTabContent(
    title: String,
    uiState: CapacitorCalculatorUiState,
    onEvent: (CapacitorCalculatorEvent) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        OutlinedTextField(
            value = formatCapacitance(uiState.totalCapacitance),
            onValueChange = {},
            label = { Text(title) },
            readOnly = true,
            modifier = Modifier.fillMaxWidth(),
            textStyle = LocalTextStyle.current.copy(fontSize = 18.sp)
        )

        Spacer(modifier = Modifier.padding(8.dp))

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(uiState.capacitors, key = { it.id }) { capacitor ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    CapacitorUnitInputField(
                        label = "C${uiState.capacitors.indexOf(capacitor) + 1}",
                        input = capacitor,
                        onValueChange = { onEvent(CapacitorCalculatorEvent.CapacitanceChanged(capacitor.id, it)) },
                        onUnitSelected = { onEvent(CapacitorCalculatorEvent.UnitChanged(capacitor.id, it)) },
                        modifier = Modifier.weight(1f)
                    )
                    if (uiState.capacitors.size > 2) {
                        IconButton(onClick = { onEvent(CapacitorCalculatorEvent.RemoveCapacitor(capacitor.id)) }) {
                            Icon(Icons.Default.Delete, contentDescription = "Supprimer le condensateur")
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.padding(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedButton(
                onClick = { onEvent(CapacitorCalculatorEvent.Reset) },
                modifier = Modifier.weight(1f)
            ) {
                Text("Réinitialiser")
            }
            Button(
                onClick = { onEvent(CapacitorCalculatorEvent.AddCapacitor) },
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.padding(end = 4.dp))
                Text("Ajouter")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CapacitorUnitInputField(
    label: String,
    input: CapacitorInput,
    onValueChange: (String) -> Unit,
    onUnitSelected: (CapacitanceUnit) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = input.value,
            onValueChange = onValueChange,
            label = { Text(label) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.weight(1f),
            singleLine = true
        )
        Spacer(modifier = Modifier.width(8.dp))
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = input.unit.symbol,
                onValueChange = {},
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier.menuAnchor().width(90.dp)
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                CapacitanceUnit.values().forEach { unit ->
                    DropdownMenuItem(
                        text = { Text(unit.symbol) },
                        onClick = {
                            onUnitSelected(unit)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun formatCapacitance(value: Double?): String {
    if (value == null) return ""
    val numberFormat = remember {
        NumberFormat.getNumberInstance(Locale.FRENCH).apply {
            maximumFractionDigits = 3
        }
    }
    return when {
        value >= 1.0 -> "${numberFormat.format(value)} F"
        value >= 1e-3 -> "${numberFormat.format(value * 1e3)} mF"
        value >= 1e-6 -> "${numberFormat.format(value * 1e6)} µF"
        value >= 1e-9 -> "${numberFormat.format(value * 1e9)} nF"
        else -> "${numberFormat.format(value * 1e12)} pF"
    }
}

@Preview(showBackground = true)
@Composable
private fun ParallelSeriesCapacitorCalculatorScreenPreview() {
    MaterialTheme {
        ParallelSeriesCapacitorCalculatorScreen()
    }
}
