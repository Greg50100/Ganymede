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
// 1. ÉTAT DE L'UI & ÉVÉNEMENTS (Partagés)
// ---------------------------------------------------

// On réutilise l'enum ResistanceUnit et l'interface du fichier précédent si elles sont partagées,
// sinon on les redéfinit ici.

//interface EnumSymbolProvider {
//    val symbol: String
//}
//
//enum class ResistanceUnit(override val symbol: String, val multiplier: Double) : EnumSymbolProvider {
//    Ω("Ω", 1.0), kΩ("kΩ", 1e3), MΩ("MΩ", 1e6)
//}

// Représente une seule ligne de saisie pour une résistance
data class ResistorInput(
    val id: UUID = UUID.randomUUID(),
    val value: String = "",
    val unit: ResistanceUnit = ResistanceUnit.kΩ // Unité par défaut
)

// L'état de l'UI pour un calculateur (série ou parallèle)
data class ResistorCalculatorUiState(
    val resistors: List<ResistorInput> = listOf(ResistorInput(), ResistorInput()), // Commence avec 2 champs
    val totalResistance: Double? = null
)

// Événements générés par l'utilisateur
sealed interface ResistorCalculatorEvent {
    data class ResistanceChanged(val id: UUID, val value: String) : ResistorCalculatorEvent
    data class UnitChanged(val id: UUID, val unit: ResistanceUnit) : ResistorCalculatorEvent
    object AddResistor : ResistorCalculatorEvent
    data class RemoveResistor(val id: UUID) : ResistorCalculatorEvent
    object Reset : ResistorCalculatorEvent
}


// ---------------------------------------------------
// 2. VIEWMODELS (Architecture avec classe de base)
// ---------------------------------------------------

/**
 * Une classe de base abstraite qui contient toute la logique commune
 * pour gérer une liste de résistances et déclencher les calculs.
 */
abstract class BaseResistorCalculatorViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(ResistorCalculatorUiState())
    val uiState = _uiState.asStateFlow()

    // Méthode abstraite que chaque implémentation (Série, Parallèle) devra définir.
    protected abstract fun performCalculation(resistorsInOhms: List<Double>): Double

    fun onEvent(event: ResistorCalculatorEvent) {
        val currentState = _uiState.value
        when (event) {
            is ResistorCalculatorEvent.AddResistor -> {
                _uiState.update { it.copy(resistors = it.resistors + ResistorInput()) }
            }
            is ResistorCalculatorEvent.RemoveResistor -> {
                if (currentState.resistors.size > 2) { // Empêche de supprimer en dessous de 2 champs
                    _uiState.update { it.copy(resistors = it.resistors.filter { res -> res.id != event.id }) }
                }
            }
            is ResistorCalculatorEvent.ResistanceChanged -> {
                val newList = currentState.resistors.map { if (it.id == event.id) it.copy(value = event.value) else it }
                _uiState.update { it.copy(resistors = newList) }
            }
            is ResistorCalculatorEvent.UnitChanged -> {
                val newList = currentState.resistors.map { if (it.id == event.id) it.copy(unit = event.unit) else it }
                _uiState.update { it.copy(resistors = newList) }
            }
            is ResistorCalculatorEvent.Reset -> {
                _uiState.value = ResistorCalculatorUiState()
            }
        }
        // Après chaque modification, on recalcule.
        calculate()
    }

    private fun calculate() {
        viewModelScope.launch {
            val validResistors = _uiState.value.resistors
                .mapNotNull { input ->
                    input.value.toDoubleOrNull()?.let { value ->
                        if (value > 0) value * input.unit.multiplier else null
                    }
                }

            if (validResistors.isNotEmpty()) {
                val total = performCalculation(validResistors)
                _uiState.update { it.copy(totalResistance = total) }
            } else {
                _uiState.update { it.copy(totalResistance = null) }
            }
        }
    }
}

/**
 * ViewModel pour le calcul en SÉRIE.
 */
class SeriesResistorViewModel : BaseResistorCalculatorViewModel() {
    override fun performCalculation(resistorsInOhms: List<Double>): Double {
        return resistorsInOhms.sum()
    }
}

/**
 * ViewModel pour le calcul en PARALLÈLE.
 */
class ParallelResistorViewModel : BaseResistorCalculatorViewModel() {
    override fun performCalculation(resistorsInOhms: List<Double>): Double {
        return 1.0 / resistorsInOhms.sumOf { 1.0 / it }
    }
}


// ---------------------------------------------------
// 3. VUES (COMPOSABLES)
// ---------------------------------------------------

@Composable
fun ParallelSeriesResistorCalculatorScreen() {
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
private fun SeriesCalculatorTab(viewModel: SeriesResistorViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    CalculatorTabContent(
        title = "Résistance équivalente en Série",
        uiState = uiState,
        onEvent = viewModel::onEvent
    )
}

@Composable
private fun ParallelCalculatorTab(viewModel: ParallelResistorViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    CalculatorTabContent(
        title = "Résistance équivalente en Parallèle",
        uiState = uiState,
        onEvent = viewModel::onEvent
    )
}

/**
 * Le contenu d'un onglet, réutilisable pour les deux modes de calcul.
 */
@Composable
private fun CalculatorTabContent(
    title: String,
    uiState: ResistorCalculatorUiState,
    onEvent: (ResistorCalculatorEvent) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // --- Champ de résultat ---
        OutlinedTextField(
            value = formatResistance(uiState.totalResistance),
            onValueChange = {},
            label = { Text(title) },
            readOnly = true,
            modifier = Modifier.fillMaxWidth(),
            textStyle = LocalTextStyle.current.copy(fontSize = 18.sp)
        )

        Spacer(modifier = Modifier.padding(8.dp))

        // --- Liste des champs de saisie ---
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(uiState.resistors, key = { it.id }) { resistor ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    ResistorUnitInputField(
                        label = "R${uiState.resistors.indexOf(resistor) + 1}",
                        input = resistor,
                        onValueChange = { onEvent(ResistorCalculatorEvent.ResistanceChanged(resistor.id, it)) },
                        onUnitSelected = { onEvent(ResistorCalculatorEvent.UnitChanged(resistor.id, it)) },
                        modifier = Modifier.weight(1f)
                    )
                    if (uiState.resistors.size > 2) {
                        IconButton(onClick = { onEvent(ResistorCalculatorEvent.RemoveResistor(resistor.id)) }) {
                            Icon(Icons.Default.Delete, contentDescription = "Supprimer la résistance")
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.padding(8.dp))

        // --- Boutons d'action ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedButton(
                onClick = { onEvent(ResistorCalculatorEvent.Reset) },
                modifier = Modifier.weight(1f)
            ) {
                Text("Réinitialiser")
            }
            Button(
                onClick = { onEvent(ResistorCalculatorEvent.AddResistor) },
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.padding(end = 4.dp))
                Text("Ajouter")
            }
        }
    }
}


// --- Composables spécifiques et utilitaires ---

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ResistorUnitInputField(
    label: String,
    input: ResistorInput,
    onValueChange: (String) -> Unit,
    onUnitSelected: (ResistanceUnit) -> Unit,
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
                ResistanceUnit.values().forEach { unit ->
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
fun formatResistance(value: Double?): String {
    if (value == null) return ""
    val numberFormat = remember {
        NumberFormat.getNumberInstance(Locale.FRENCH).apply {
            maximumFractionDigits = 3
        }
    }
    // Formate le résultat en Ohms, kOhms, ou MOhms pour une meilleure lisibilité.
    return when {
        value >= 1_000_000 -> "${numberFormat.format(value / 1_000_000)} MΩ"
        value >= 1_000 -> "${numberFormat.format(value / 1_000)} kΩ"
        else -> "${numberFormat.format(value)} Ω"
    }
}

@Preview(showBackground = true)
@Composable
private fun ParallelSeriesResistorCalculatorScreenPreview() {
    MaterialTheme {
        ParallelSeriesResistorCalculatorScreen()
    }
}
