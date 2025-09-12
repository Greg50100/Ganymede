package com.joviansapps.ganymede.ui.screens.utilities.electronics

import android.graphics.Paint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.joviansapps.ganymede.R
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Locale
import kotlin.math.*

// ---------------------------------------------------
// 1. MODÈLES D'ÉTAT (STATE MODELS)
// ---------------------------------------------------

/**
 * Représente une seule entrée de formulaire avec sa valeur et son état d'erreur.
 */
data class FormInput(
    val value: String = "",
    val error: String? = null
)

/**
 * L'état complet et immuable de l'écran du calculateur.
 */
data class CapacitorChargeState(
    val isChargeMode: Boolean = true,
    val resistance: FormInput = FormInput("1000.0"),
    val capacitance: FormInput = FormInput("1.0"),
    val initialVoltage: FormInput = FormInput("5.0"),
    val time: FormInput = FormInput("1.0"),
    val plotDuration: FormInput = FormInput("5.0"),
    val yMax: FormInput = FormInput(""), // Optionnel

    val resultVoltage: Double? = null,
    val resultCharge: Double? = null,
    val timeConstant: Double? = null, // Tau

    val graphPoints: List<Pair<Float, Float>> = emptyList(),
    val isFormValid: Boolean = false
)

// ---------------------------------------------------
// 2. VIEWMODEL
// ---------------------------------------------------

/**
 * Gère l'état et la logique métier de l'écran.
 */
class CapacitorChargeViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(CapacitorChargeState())
    val uiState: StateFlow<CapacitorChargeState> = _uiState.asStateFlow()

    init {
        // Valider l'état initial au démarrage
        validateForm()
    }

    // --- Gestionnaires d'événements de l'UI ---

    fun onModeChange(isChargeMode: Boolean) {
        _uiState.update { it.copy(isChargeMode = isChargeMode) }
    }

    fun onResistanceChange(value: String) {
        _uiState.update { it.copy(resistance = it.resistance.copy(value = value)) }
        validateForm()
    }

    fun onCapacitanceChange(value: String) {
        _uiState.update { it.copy(capacitance = it.capacitance.copy(value = value)) }
        validateForm()
    }

    fun onVoltageChange(value: String) {
        _uiState.update { it.copy(initialVoltage = it.initialVoltage.copy(value = value)) }
        validateForm()
    }

    fun onTimeChange(value: String) {
        _uiState.update { it.copy(time = it.time.copy(value = value)) }
        validateForm()
    }

    fun onPlotDurationChange(value: String) {
        _uiState.update { it.copy(plotDuration = it.plotDuration.copy(value = value)) }
        validateForm()
    }

    fun onYMaxChange(value: String) {
        _uiState.update { it.copy(yMax = it.yMax.copy(value = value)) }
        validateForm()
    }

    fun onCalculate() {
        if (!uiState.value.isFormValid) return

        viewModelScope.launch {
            val state = uiState.value
            val r = state.resistance.value.toDouble()
            val cMicro = state.capacitance.value.toDouble()
            val v0 = state.initialVoltage.value.toDouble()
            val t = state.time.value.toDouble()
            val plotDuration = state.plotDuration.value.toDouble()

            val cFarad = cMicro * 1e-6
            val tau = r * cFarad

            val voltageAtT = calculateVoltage(v0, t, tau, state.isChargeMode)
            val chargeAtT = cFarad * voltageAtT

            val points = generateGraphPoints(v0, tau, plotDuration, state.isChargeMode)

            _uiState.update {
                it.copy(
                    resultVoltage = voltageAtT,
                    resultCharge = chargeAtT,
                    timeConstant = tau,
                    graphPoints = points
                )
            }
        }
    }

    // --- Logique métier et validation ---

    private fun validateForm() {
        val state = _uiState.value
        val resistanceResult = validateDouble(state.resistance.value, "Resistance")
        val capacitanceResult = validateDouble(state.capacitance.value, "Capacitance")
        val voltageResult = validateDouble(state.initialVoltage.value, "Voltage")
        val timeResult = validateDouble(state.time.value, "Time")
        val plotDurationResult = validateDouble(state.plotDuration.value, "Plot Duration")
        val yMaxResult = validateDouble(state.yMax.value, "Max Y", allowEmpty = true)

        val isFormValid = resistanceResult.error == null && capacitanceResult.error == null &&
                voltageResult.error == null && timeResult.error == null &&
                plotDurationResult.error == null && yMaxResult.error == null

        _uiState.update {
            it.copy(
                resistance = resistanceResult,
                capacitance = capacitanceResult,
                initialVoltage = voltageResult,
                time = timeResult,
                plotDuration = plotDurationResult,
                yMax = yMaxResult,
                isFormValid = isFormValid
            )
        }
    }

    private fun validateDouble(value: String, fieldName: String, allowEmpty: Boolean = false): FormInput {
        if (value.isEmpty()) {
            return if (allowEmpty) FormInput(value, null) else FormInput(value, "$fieldName cannot be empty")
        }
        return if (value.toDoubleOrNull()!= null) {
            FormInput(value, null)
        } else {
            FormInput(value, "Invalid number for $fieldName")
        }
    }

    private fun calculateVoltage(v0: Double, t: Double, tau: Double, isCharge: Boolean): Double {
        return if (tau > 0) {
            if (isCharge) v0 * (1 - exp(-t / tau)) else v0 * exp(-t / tau)
        } else {
            if (isCharge) v0 else 0.0
        }
    }

    private fun generateGraphPoints(v0: Double, tau: Double, duration: Double, isCharge: Boolean): List<Pair<Float, Float>> {
        val samples = 200
        return (0..samples).map { i ->
            val time = duration * i / samples
            val voltage = calculateVoltage(v0, time, tau, isCharge)
            Pair(time.toFloat(), voltage.toFloat())
        }
    }
}

// ---------------------------------------------------
// 3. VUES (COMPOSABLES)
// ---------------------------------------------------

@Composable
fun TimeConstantCalculatorScreen(
    onBack: () -> Unit,
    viewModel: CapacitorChargeViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        ModeSelector(
            isChargeMode = uiState.isChargeMode,
            onModeChange = viewModel::onModeChange
        )

        ValidatedTextField(
            label = stringResource(id = R.string.resistor_label),
            input = uiState.resistance,
            onValueChange = viewModel::onResistanceChange
        )
        ValidatedTextField(
            label = stringResource(id = R.string.capacitor_label),
            supportingText = stringResource(id = R.string.capacitor_label_description),
            input = uiState.capacitance,
            onValueChange = viewModel::onCapacitanceChange
        )
        ValidatedTextField(
            label = stringResource(id = R.string.voltage_label),
            input = uiState.initialVoltage,
            onValueChange = viewModel::onVoltageChange
        )
        ValidatedTextField(
            label = stringResource(id = R.string.time_label),
            supportingText = stringResource(id = R.string.time_label_seconds),
            input = uiState.time,
            onValueChange = viewModel::onTimeChange
        )
        ValidatedTextField(
            label = "Plot duration (s)", // TODO: Externalize to strings.xml
            input = uiState.plotDuration,
            onValueChange = viewModel::onPlotDurationChange
        )
        ValidatedTextField(
            label = "Max Y (V) — optional", // TODO: Externalize to strings.xml
            supportingText = "Leave empty for auto scale", // TODO: Externalize to strings.xml
            input = uiState.yMax,
            onValueChange = viewModel::onYMaxChange
        )

        Button(
            onClick = viewModel::onCalculate,
            enabled = uiState.isFormValid,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(id = R.string.calculate_button))
        }

        Results(state = uiState)

        if (uiState.graphPoints.isNotEmpty()) {
            LineGraph(
                points = uiState.graphPoints,
                tau = uiState.timeConstant,
                userYMax = uiState.yMax.value.toFloatOrNull(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(420.dp)
                    .padding(top = 16.dp)
            )
        } else {
            Text(
                text = "Press 'Calculate' to generate the graph.", // TODO: Externalize
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(vertical = 24.dp)
            )
        }

        TextButton(onClick = onBack) {
            Text(stringResource(id = R.string.back_button_description))
        }
    }
}

@Composable
private fun ModeSelector(isChargeMode: Boolean, onModeChange: (Boolean) -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
        RadioButton(selected = isChargeMode, onClick = { onModeChange(true) })
        Text(stringResource(id = R.string.mode_charge))
        Spacer(Modifier.width(16.dp))
        RadioButton(selected =!isChargeMode, onClick = { onModeChange(false) })
        Text(stringResource(id = R.string.mode_discharge))
    }
}

@Composable
private fun ValidatedTextField(
    label: String,
    input: FormInput,
    onValueChange: (String) -> Unit,
    supportingText: String? = null
) {
    OutlinedTextField(
        value = input.value,
        onValueChange = onValueChange,
        label = { Text(label) },
        isError = input.error!= null,
        supportingText = {
            if (input.error!= null) {
                Text(input.error, color = MaterialTheme.colorScheme.error)
            } else if (supportingText!= null) {
                Text(supportingText)
            }
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        modifier = Modifier.fillMaxWidth(),
        singleLine = true
    )
}

@Composable
private fun Results(state: CapacitorChargeState) {
    if (state.resultVoltage!= null && state.resultCharge!= null) {
        Column(
            horizontalAlignment = Alignment.Start,
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = String.format(Locale.US, "Voltage at t: %.4f V", state.resultVoltage),
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = String.format(Locale.US, "Charge at t: %.4f µC", state.resultCharge * 1e6),
                style = MaterialTheme.typography.titleMedium
            )
            state.timeConstant?.let {
                Text(
                    text = String.format(Locale.US, "Time constant (τ): %.4f s", it),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
fun LineGraph(
    points: List<Pair<Float, Float>>,
    modifier: Modifier = Modifier,
    tau: Double?,
    userYMax: Float?
) {
    val bgColor = MaterialTheme.colorScheme.surfaceVariant
    val axisColor = MaterialTheme.colorScheme.outline
    val gridColor = MaterialTheme.colorScheme.secondaryContainer
    val textColor = MaterialTheme.colorScheme.onSurface
    val curveColor = MaterialTheme.colorScheme.primary
    val tauMarkerColor = MaterialTheme.colorScheme.error

    Canvas(modifier = modifier) {
        // --- Calcul des échelles et du zoom ---
        val maxVraw = points.maxOfOrNull { it.second }?.coerceAtLeast(0f)?: 1f
        val finalYMax = userYMax?.let { max(maxVraw, it) }?: maxVraw
        val paddingV = finalYMax * 0.15f
        val minV = 0f
        val maxV = (finalYMax + paddingV).coerceAtLeast(minV + 1e-3f)

        val fullDuration = points.maxOfOrNull { it.first }?: 1f
        val tauSeconds = tau?.toFloat()

        val viewDuration = if (tauSeconds!= null && tauSeconds > 0.0 && 5.0 * tauSeconds < fullDuration) {
            (5.0 * tauSeconds).toFloat().coerceAtLeast(0.1f)
        } else {
            fullDuration
        }

        val w = size.width
        val h = size.height
        val leftPad = 56f
        val rightPad = 12f
        val topPad = 12f
        val bottomPad = 36f
        val innerW = (w - leftPad - rightPad).coerceAtLeast(10f)
        val innerH = (h - topPad - bottomPad).coerceAtLeast(10f)

        drawRect(color = bgColor, size = Size(w, h))

        fun txToX(t: Float): Float = leftPad + (if (viewDuration <= 0f) 0f else (t.coerceAtMost(viewDuration) / viewDuration) * innerW)
        fun vToY(v: Float): Float = topPad + (1f - (v - minV) / (maxV - minV)) * innerH

        val xAxisY = topPad + innerH
        drawLine(color = axisColor, start = Offset(leftPad, xAxisY), end = Offset(leftPad + innerW, xAxisY), strokeWidth = 1.5f)
        drawLine(color = axisColor, start = Offset(leftPad, topPad), end = Offset(leftPad, xAxisY), strokeWidth = 1.5f)

        val labelPaint = Paint().apply { color = textColor.toArgb(); textSize = 12.sp.toPx(); isAntiAlias = true }

        // Grille et graduations Y
        val yRange = (maxV - minV).toDouble()
        val yStep = niceStep(yRange, 6)
        var yy = floor(minV / yStep) * yStep
        while (yy <= ceil(maxV / yStep) * yStep + 1e-9) {
            val raw = yy.toFloat()
            val vy = vToY(raw)
            drawLine(color = gridColor, start = Offset(leftPad, vy), end = Offset(leftPad + innerW, vy), strokeWidth = 1f)
            drawContext.canvas.nativeCanvas.drawText(formatNumber(raw), 6f, vy + 4f, labelPaint)
            yy += yStep
        }

        // Grille et graduations X
        val xRange = viewDuration.toDouble()
        val xStep = niceStep(xRange, 6)
        var xtv = 0.0
        while (xtv <= ceil(viewDuration.toDouble() / xStep) * xStep + 1e-9) {
            val txf = xtv.toFloat().coerceAtMost(viewDuration)
            val x = txToX(txf)
            drawLine(color = gridColor, start = Offset(x, topPad), end = Offset(x, topPad + innerH), strokeWidth = 1f)
            val label = String.format(Locale.US, "%.2fs", xtv)
            drawContext.canvas.nativeCanvas.drawText(label, x - 18f, xAxisY + 18f, labelPaint)
            xtv += xStep
        }

        // Courbe
        if (points.isNotEmpty()) {
            val path = Path().apply {
                val first = points.first()
                moveTo(txToX(first.first), vToY(first.second))
                for (i in 1 until points.size) {
                    val (t1, v1) = points[i]
                    if (t1 <= viewDuration) lineTo(txToX(t1), vToY(v1)) else break
                }
            }
            drawPath(path = path, color = curveColor, style = Stroke(width = 4f))
        }

        // Marqueur Tau
        if (tauSeconds!= null && fullDuration > 0f && tauSeconds <= viewDuration) {
            val xt = txToX(tauSeconds)
            val dash = 8f
            val gap = 6f
            var y = topPad
            while (y < topPad + innerH) {
                val y2 = (y + dash).coerceAtMost(topPad + innerH)
                drawLine(color = tauMarkerColor, start = Offset(xt, y), end = Offset(xt, y2), strokeWidth = 2f)
                y += dash + gap
            }
            val tauLabel = String.format(Locale.US, "τ=%.3fs", tauSeconds)
            drawContext.canvas.nativeCanvas.drawText(tauLabel, xt + 6f, topPad + 12f, Paint().apply { color = tauMarkerColor.toArgb(); textSize = 12.sp.toPx(); isAntiAlias = true })
        }

        // Indicateur de zoom
        if (viewDuration < fullDuration) {
            val info = String.format(Locale.US, "Zoom: 0..%.2fs (5·τ)", viewDuration)
            drawContext.canvas.nativeCanvas.drawText(info, leftPad + 6f, topPad + 14f, labelPaint)
        }
    }
}

// --- Fonctions utilitaires pour le graphique ---

private fun niceStep(range: Double, targetTicks: Int): Double {
    if (range <= 0.0 || targetTicks <= 0) return 1.0
    val raw = range / targetTicks
    val exp = floor(log10(raw))
    val candidates = listOf(1.0, 2.0, 2.5, 5.0, 10.0).map { it * 10.0.pow(exp) }
    return candidates.firstOrNull { it >= raw }?: candidates.last()
}

private fun formatNumber(v: Float): String {
    return when {
        abs(v) >= 1000f -> String.format(Locale.US, "%.0f", v)
        abs(v) >= 1f -> String.format(Locale.US, "%.2f", v)
        abs(v) >= 0.01f -> String.format(Locale.US, "%.3f", v)
        v == 0f -> "0"
        else -> String.format(Locale.US, "%.2e", v)
    }
}