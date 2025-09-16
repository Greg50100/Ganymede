package com.joviansapps.ganymede.ui.screens.utilities.electronics

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.joviansapps.ganymede.R
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale
import kotlin.math.exp
import kotlin.math.pow

// ---------------------------------------------------
// 1. ÉTAT DE L'UI & ÉVÉNEMENTS
// ---------------------------------------------------


enum class VoltageUnit(override val symbol: String, val multiplier: Double) : EnumSymbolProvider {
    V("V", 1.0), mV("mV", 1e-3)
}

enum class CapacitanceUnit(override val symbol: String, val multiplier: Double) : EnumSymbolProvider {
    F("F", 1.0), mF("mF", 1e-3), µF("µF", 1e-6), nF("nF", 1e-9), pF("pF", 1e-12)
}

enum class ResistanceUnit(override val symbol: String, val multiplier: Double) : EnumSymbolProvider {
    Ω("Ω", 1.0), kΩ("kΩ", 1e3), MΩ("MΩ", 1e6)
}

data class FormInputWithUnit<U>(
    val value: String = "",
    val unit: U
)

data class GraphPoint(
    val timeFactor: Float,
    val voltagePercent: Float,
    val currentPercent: Float
)

data class CapacitorChargeUiState(
    val voltage: FormInputWithUnit<VoltageUnit> = FormInputWithUnit(unit = VoltageUnit.V),
    val capacitance: FormInputWithUnit<CapacitanceUnit> = FormInputWithUnit(unit = CapacitanceUnit.µF),
    val resistance: FormInputWithUnit<ResistanceUnit> = FormInputWithUnit(unit = ResistanceUnit.kΩ),
    val timeConstant: Double? = null,
    val energy: Double? = null,
    val graphPoints: List<GraphPoint> = emptyList()
)

sealed interface CalculatorEvent {
    data class VoltageChanged(val value: String) : CalculatorEvent
    data class VoltageUnitChanged(val unit: VoltageUnit) : CalculatorEvent
    data class CapacitanceChanged(val value: String) : CalculatorEvent
    data class CapacitanceUnitChanged(val unit: CapacitanceUnit) : CalculatorEvent
    data class ResistanceChanged(val value: String) : CalculatorEvent
    data class ResistanceUnitChanged(val unit: ResistanceUnit) : CalculatorEvent
    object Reset : CalculatorEvent
}

// ---------------------------------------------------
// 2. VIEWMODEL (AVEC LA LOGIQUE CORRIGÉE)
// ---------------------------------------------------

class CapacitorChargeViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(CapacitorChargeUiState())
    val uiState = _uiState.asStateFlow()

    fun onEvent(event: CalculatorEvent) {
        // Mettre à jour l'état en fonction de l'événement
        val updatedState = when (event) {
            is CalculatorEvent.VoltageChanged -> _uiState.value.copy(voltage = _uiState.value.voltage.copy(value = event.value))
            is CalculatorEvent.VoltageUnitChanged -> _uiState.value.copy(voltage = _uiState.value.voltage.copy(unit = event.unit))
            is CalculatorEvent.CapacitanceChanged -> _uiState.value.copy(capacitance = _uiState.value.capacitance.copy(value = event.value))
            is CalculatorEvent.CapacitanceUnitChanged -> _uiState.value.copy(capacitance = _uiState.value.capacitance.copy(unit = event.unit))
            is CalculatorEvent.ResistanceChanged -> _uiState.value.copy(resistance = _uiState.value.resistance.copy(value = event.value))
            is CalculatorEvent.ResistanceUnitChanged -> _uiState.value.copy(resistance = _uiState.value.resistance.copy(unit = event.unit))
            CalculatorEvent.Reset -> CapacitorChargeUiState()
        }
        // Appliquer la mise à jour et lancer la validation
        _uiState.value = updatedState
        validateAndCalculate(updatedState)
    }

    private fun validateAndCalculate(state: CapacitorChargeUiState) {
        val isVoltageValid = state.voltage.value.toDoubleOrNull()?.let { it > 0 } ?: false
        val isCapacitanceValid = state.capacitance.value.toDoubleOrNull()?.let { it > 0 } ?: false
        val isResistanceValid = state.resistance.value.toDoubleOrNull()?.let { it > 0 } ?: false

        if (isVoltageValid && isCapacitanceValid && isResistanceValid) {
            calculate(state)
        } else {
            clearResults()
        }
    }

    private fun clearResults() {
        _uiState.update {
            it.copy(
                timeConstant = null,
                energy = null,
                graphPoints = emptyList()
            )
        }
    }

    private fun calculate(state: CapacitorChargeUiState) {
        viewModelScope.launch {
            // Utiliser l'état passé en paramètre garantit que les données sont les bonnes
            val v = state.voltage.value.toDouble() * state.voltage.unit.multiplier
            val c = state.capacitance.value.toDouble() * state.capacitance.unit.multiplier
            val r = state.resistance.value.toDouble() * state.resistance.unit.multiplier

            val tau = r * c
            val energy = 0.5 * c * v.pow(2)
            val points = generateGraphPoints(tau)

            // Mettre à jour l'état avec les nouveaux résultats en une seule fois
            _uiState.update {
                it.copy(
                    timeConstant = tau,
                    energy = energy,
                    graphPoints = points
                )
            }
        }
    }

    private fun generateGraphPoints(tau: Double): List<GraphPoint> {
        val maxTimeFactor = 5.0f
        val samples = 200
        return (0..samples).map { i ->
            val timeFactor = (i.toFloat() / samples) * maxTimeFactor
            val t = timeFactor * tau
            val voltagePercent = (1 - exp(-t / tau)) * 100
            val currentPercent = exp(-t / tau) * 100

            GraphPoint(
                timeFactor = timeFactor,
                voltagePercent = voltagePercent.toFloat(),
                currentPercent = currentPercent.toFloat()
            )
        }
    }
}


// ---------------------------------------------------
// 3. VUES (COMPOSABLES) - Inchangé
// ---------------------------------------------------

@Composable

fun TimeConstantCalculatorScreen(viewModel: CapacitorChargeViewModel = viewModel()) {
    // ... (Le reste du fichier est identique à la version précédente)
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabTitles = listOf("Calculateur", "Graphique")

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
            0 -> CalculatorTabContent(uiState = uiState, onEvent = viewModel::onEvent)
            1 -> GraphTabContent(uiState = uiState)
        }
    }
}

@Composable
private fun CalculatorTabContent(
    uiState: CapacitorChargeUiState,
    onEvent: (CalculatorEvent) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        UnitInputField(
            label = stringResource(id = R.string.voltage_label),
            value = uiState.voltage.value,
            onValueChange = { onEvent(CalculatorEvent.VoltageChanged(it)) },
            selectedUnit = uiState.voltage.unit,
            units = VoltageUnit.values().toList(),
            onUnitSelected = { onEvent(CalculatorEvent.VoltageUnitChanged(it)) }
        )

        UnitInputField(
            label = stringResource(id = R.string.capacitance_label),
            value = uiState.capacitance.value,
            onValueChange = { onEvent(CalculatorEvent.CapacitanceChanged(it)) },
            selectedUnit = uiState.capacitance.unit,
            units = CapacitanceUnit.values().toList(),
            onUnitSelected = { onEvent(CalculatorEvent.CapacitanceUnitChanged(it)) }
        )

        UnitInputField(
            label = stringResource(id = R.string.resistance_label),
            value = uiState.resistance.value,
            onValueChange = { onEvent(CalculatorEvent.ResistanceChanged(it)) },
            selectedUnit = uiState.resistance.unit,
            units = ResistanceUnit.values().toList(),
            onUnitSelected = { onEvent(CalculatorEvent.ResistanceUnitChanged(it)) }
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = formatNumber(uiState.timeConstant, "s"),
                onValueChange = {},
                label = { Text("Cste de temps (τ)") },
                readOnly = true,
                modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
                value = formatNumber(uiState.energy, "J"),
                onValueChange = {},
                label = { Text("Énergie (E)") },
                readOnly = true,
                modifier = Modifier.weight(1f)
            )
            OutlinedButton(
                onClick = { onEvent(CalculatorEvent.Reset) },
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp), // Hauteur similaire à OutlinedTextField
                shape = MaterialTheme.shapes.extraSmall, // Bordures similaires
                colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
            ) {
                Text(stringResource(id = R.string.reset_button))
            }
        }

        Image(
            painter = painterResource(id = R.drawable.time_constant_equation),
            contentDescription = "Illustration d'un condensateur en charge",
            contentScale = ContentScale.FillWidth,
            modifier = Modifier
                .fillMaxWidth(),
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary)
        )

        Image(
            painter = painterResource(id = R.drawable.time_constant_circuit),
            contentDescription = "Illustration d'un circuit RC",
            contentScale = ContentScale.FillWidth,
            modifier = Modifier
                .fillMaxWidth(),
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Suppress("DEPRECATION")
@Composable
private fun <U> UnitInputField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    selectedUnit: U,
    units: List<U>,
    onUnitSelected: (U) -> Unit,
    modifier: Modifier = Modifier
) where U : Enum<U>, U : EnumSymbolProvider {
    var expanded by remember { mutableStateOf(false) }

    Row(modifier = modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        OutlinedTextField(
            value = value,
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
                value = selectedUnit.symbol,
                onValueChange = {},
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier.menuAnchor().width(100.dp)
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                units.forEach { unit ->
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
private fun GraphTabContent(uiState: CapacitorChargeUiState) {
    Box(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        if (uiState.graphPoints.isNotEmpty()) {
            ChargeDischargeGraph(points = uiState.graphPoints)
        } else {
            Text(
                text = "Veuillez effectuer un calcul pour afficher le graphique.",
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun formatNumber(value: Double?, unit: String): String {
    val number = value ?: 0.0
    val formatter = remember {
        NumberFormat.getNumberInstance(Locale.FRENCH).apply {
            maximumFractionDigits = 4
        }
    }
    return "${formatter.format(number)} $unit"
}

@Composable
fun ChargeDischargeGraph(points: List<GraphPoint>) {
    val textMeasurer = rememberTextMeasurer()
    val voltageColor = MaterialTheme.colorScheme.primary
    val currentColor = MaterialTheme.colorScheme.error
    val gridColor = MaterialTheme.colorScheme.outlineVariant
    val textColor = MaterialTheme.colorScheme.onSurface
    val voltageLegend = stringResource(id = R.string.voltage_legend)
    val currentLegend = stringResource(id = R.string.current_legend)

    Box(modifier = Modifier.fillMaxWidth()) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
        ) {
            val leftPadding = 80f
            val rightPadding = 80f
            val topPadding = 40f
            val bottomPadding = 60f

            val innerWidth = size.width - leftPadding - rightPadding
            val innerHeight = size.height - topPadding - bottomPadding

            fun timeToX(timeFactor: Float): Float = leftPadding + (timeFactor / 5f) * innerWidth
            fun percentToY(percent: Float): Float = topPadding + (1 - (percent / 100f)) * innerHeight

            (0..5).forEach { i ->
                val x = timeToX(i.toFloat())
                drawLine(gridColor, start = Offset(x, topPadding), end = Offset(x, topPadding + innerHeight))
                val textResult = textMeasurer.measure("${i}τ", style = TextStyle(fontSize = 12.sp, color = textColor))
                drawText(textResult, topLeft = Offset(x - textResult.size.width / 2, topPadding + innerHeight + 10f))
            }
            (0..5).forEach { i ->
                val y = percentToY(i * 20f)
                drawLine(gridColor, start = Offset(leftPadding, y), end = Offset(leftPadding + innerWidth, y))
                val labelText = "${i * 20}%"
                val textLeft = textMeasurer.measure(labelText, style = TextStyle(fontSize = 12.sp, color = voltageColor))
                drawText(textLeft, topLeft = Offset(leftPadding - textLeft.size.width - 10f, y - textLeft.size.height / 2))
                val textRight = textMeasurer.measure(labelText, style = TextStyle(fontSize = 12.sp, color = currentColor))
                drawText(textRight, topLeft = Offset(leftPadding + innerWidth + 10f, y - textRight.size.height / 2))
            }

            points.firstOrNull()?.let { firstPoint ->
                val voltagePath = Path().apply {
                    moveTo(timeToX(firstPoint.timeFactor), percentToY(firstPoint.voltagePercent))
                    points.forEach { lineTo(timeToX(it.timeFactor), percentToY(it.voltagePercent)) }
                }
                val currentPath = Path().apply {
                    moveTo(timeToX(firstPoint.timeFactor), percentToY(firstPoint.currentPercent))
                    points.forEach { lineTo(timeToX(it.timeFactor), percentToY(it.currentPercent)) }
                }
                drawPath(voltagePath, color = voltageColor, style = Stroke(width = 4f))
                drawPath(currentPath, color = currentColor, style = Stroke(width = 4f))
            }

            val yLeftTitle = textMeasurer.measure(voltageLegend, style = TextStyle(fontSize = 12.sp, color = voltageColor))
            drawText(yLeftTitle, topLeft = Offset(leftPadding - yLeftTitle.size.width - 10f, topPadding - yLeftTitle.size.height - 5f))

            val yRightTitle = textMeasurer.measure(currentLegend, style = TextStyle(fontSize = 12.sp, color = currentColor))
            drawText(yRightTitle, topLeft = Offset(leftPadding + innerWidth + 10f, topPadding - yRightTitle.size.height - 5f))
        }

        Row(
            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 1.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                Box(modifier = Modifier.size(12.dp).background(voltageColor))
                Text(voltageLegend, fontSize = 12.sp)
            }
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                Box(modifier = Modifier.size(12.dp).background(currentColor))
                Text(currentLegend, fontSize = 12.sp)
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
private fun TimeConstantCalculatorScreenPreview() {
    MaterialTheme {
        TimeConstantCalculatorScreen()
    }
}