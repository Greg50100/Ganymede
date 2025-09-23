package com.joviansapps.ganymede.ui.screens.utilities.electronics

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
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
import kotlin.math.pow

// Data class for complex number operations
data class Complex(val real: Double = 0.0, val imag: Double = 0.0) {
    operator fun plus(other: Complex) = Complex(real + other.real, imag + other.imag)
    operator fun times(other: Complex) = Complex(
        real * other.real - imag * other.imag,
        real * other.imag + imag * other.real
    )
    operator fun div(other: Complex): Complex {
        val denominator = other.real.pow(2) + other.imag.pow(2)
        if (denominator == 0.0) return Complex(Double.NaN, Double.NaN)
        val realPart = (real * other.real + imag * other.imag) / denominator
        val imagPart = (imag * other.real - real * other.imag) / denominator
        return Complex(realPart, imagPart)
    }
    override fun toString(): String {
        val formatter = DecimalFormat("#.###")
        val imagSign = if (imag < 0) "-" else "+"
        return "${formatter.format(real)} $imagSign j${formatter.format(kotlin.math.abs(imag))}"
    }
}


enum class ConversionType { DeltaToStar, StarToDelta }
enum class ComponentType { Resistor, Capacitor, Inductor, Complex }

data class DeltaStarUiState(
    val conversionType: ConversionType = ConversionType.DeltaToStar,
    val componentType: ComponentType = ComponentType.Resistor,
    // Real-only inputs
    val inA: String = "100",
    val inB: String = "100",
    val inC: String = "100",
    // Complex inputs
    val inA_real: String = "100", val inA_imag: String = "50",
    val inB_real: String = "100", val inB_imag: String = "-25",
    val inC_real: String = "100", val inC_imag: String = "75",
    // Real-only outputs
    val out1: Double? = null,
    val out2: Double? = null,
    val out3: Double? = null,
    // Complex outputs
    val out1_complex: Complex? = null,
    val out2_complex: Complex? = null,
    val out3_complex: Complex? = null
)

class DeltaStarViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(DeltaStarUiState())
    val uiState = _uiState.asStateFlow()

    init {
        calculate()
    }

    fun onValueChange(field: String, value: String) {
        _uiState.update {
            when (field) {
                "inA" -> it.copy(inA = value)
                "inB" -> it.copy(inB = value)
                "inC" -> it.copy(inC = value)
                "inA_real" -> it.copy(inA_real = value)
                "inA_imag" -> it.copy(inA_imag = value)
                "inB_real" -> it.copy(inB_real = value)
                "inB_imag" -> it.copy(inB_imag = value)
                "inC_real" -> it.copy(inC_real = value)
                "inC_imag" -> it.copy(inC_imag = value)
                else -> it
            }
        }
        calculate()
    }

    fun onConversionTypeChange(type: ConversionType) {
        _uiState.update { it.copy(conversionType = type) }
        calculate()
    }

    fun onComponentTypeChange(type: ComponentType) {
        _uiState.update { it.copy(componentType = type) }
        calculate()
    }

    private fun calculate() {
        viewModelScope.launch {
            val s = _uiState.value
            if (s.componentType == ComponentType.Complex) {
                calculateComplex()
            } else {
                calculateReal()
            }
        }
    }
    private fun clearOutputs() {
        _uiState.update { it.copy(
            out1 = null, out2 = null, out3 = null,
            out1_complex = null, out2_complex = null, out3_complex = null
        )}
    }

    private fun calculateReal() {
        val s = _uiState.value
        val inA = s.inA.toDoubleOrNull()
        val inB = s.inB.toDoubleOrNull()
        val inC = s.inC.toDoubleOrNull()

        if (inA == null || inB == null || inC == null) {
            clearOutputs()
            return
        }

        var out1: Double? = null
        var out2: Double? = null
        var out3: Double? = null

        if (s.conversionType == ConversionType.DeltaToStar) {
            if (s.componentType == ComponentType.Resistor || s.componentType == ComponentType.Inductor) {
                val sum = inA + inB + inC
                if (sum != 0.0) {
                    out1 = (inB * inC) / sum
                    out2 = (inA * inC) / sum
                    out3 = (inA * inB) / sum
                }
            } else { // Capacitor
                val num = (inA * inB) + (inB * inC) + (inC * inA)
                if (inA != 0.0) out1 = num / inA
                if (inB != 0.0) out2 = num / inB
                if (inC != 0.0) out3 = num / inC
            }
        } else { // Star to Delta
            if (s.componentType == ComponentType.Resistor || s.componentType == ComponentType.Inductor) {
                val num = (inA * inB) + (inB * inC) + (inC * inA)
                if (inA != 0.0) out1 = num / inA
                if (inB != 0.0) out2 = num / inB
                if (inC != 0.0) out3 = num / inC
            } else { // Capacitor
                val sum = inA + inB + inC
                if (sum != 0.0) {
                    out1 = (inB * inC) / sum
                    out2 = (inA * inC) / sum
                    out3 = (inA * inB) / sum
                }
            }
        }
        _uiState.update { it.copy(out1 = out1, out2 = out2, out3 = out3, out1_complex = null, out2_complex = null, out3_complex = null) }
    }

    private fun calculateComplex() {
        val s = _uiState.value
        val inA = Complex(s.inA_real.toDoubleOrNull() ?: return clearOutputs(), s.inA_imag.toDoubleOrNull() ?: return clearOutputs())
        val inB = Complex(s.inB_real.toDoubleOrNull() ?: return clearOutputs(), s.inB_imag.toDoubleOrNull() ?: return clearOutputs())
        val inC = Complex(s.inC_real.toDoubleOrNull() ?: return clearOutputs(), s.inC_imag.toDoubleOrNull() ?: return clearOutputs())

        var out1: Complex? = null
        var out2: Complex? = null
        var out3: Complex? = null

        if (s.conversionType == ConversionType.DeltaToStar) {
            val sum = inA + inB + inC
            out1 = (inB * inC) / sum
            out2 = (inA * inC) / sum
            out3 = (inA * inB) / sum
        } else { // Star to Delta
            val num = (inA * inB) + (inB * inC) + (inC * inA)
            out1 = num / inA
            out2 = num / inB
            out3 = num / inC
        }
        _uiState.update { it.copy(out1_complex = out1, out2_complex = out2, out3_complex = out3, out1 = null, out2 = null, out3 = null) }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeltaStarConverterScreen(viewModel: DeltaStarViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val unit = when(uiState.componentType){
        ComponentType.Resistor -> "Ω"
        ComponentType.Capacitor -> "F"
        ComponentType.Inductor -> "H"
        ComponentType.Complex -> "Ω"
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(stringResource(id = R.string.delta_star_converter_title), style = MaterialTheme.typography.headlineSmall)

        Text("Type de Conversion", style = MaterialTheme.typography.titleMedium)
        SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
            SegmentedButton(selected = uiState.conversionType == ConversionType.DeltaToStar, onClick = { viewModel.onConversionTypeChange(ConversionType.DeltaToStar) }, shape = SegmentedButtonDefaults.itemShape(0, 2)) { Text("Δ -> Y") }
            SegmentedButton(selected = uiState.conversionType == ConversionType.StarToDelta, onClick = { viewModel.onConversionTypeChange(ConversionType.StarToDelta) }, shape = SegmentedButtonDefaults.itemShape(1, 2)) { Text("Y -> Δ") }
        }

        Text("Type de Composant", style = MaterialTheme.typography.titleMedium)
        SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
            SegmentedButton(selected = uiState.componentType == ComponentType.Resistor, onClick = { viewModel.onComponentTypeChange(ComponentType.Resistor) }, shape = SegmentedButtonDefaults.itemShape(0, 4)) { Text("R") }
            SegmentedButton(selected = uiState.componentType == ComponentType.Capacitor, onClick = { viewModel.onComponentTypeChange(ComponentType.Capacitor) }, shape = SegmentedButtonDefaults.itemShape(1, 4)) { Text("C") }
            SegmentedButton(selected = uiState.componentType == ComponentType.Inductor, onClick = { viewModel.onComponentTypeChange(ComponentType.Inductor) }, shape = SegmentedButtonDefaults.itemShape(2, 4)) { Text("L") }
            SegmentedButton(selected = uiState.componentType == ComponentType.Complex, onClick = { viewModel.onComponentTypeChange(ComponentType.Complex) }, shape = SegmentedButtonDefaults.itemShape(3, 4)) { Text("Z") }
        }

        if (uiState.componentType == ComponentType.Complex) {
            val (inLabelA, inLabelB, inLabelC) = if (uiState.conversionType == ConversionType.DeltaToStar) Triple("Zₐ", "Zₑ", "Z𝒸") else Triple("Z₁", "Z₂", "Z₃")
            ComplexInputRow(label = inLabelA, real = uiState.inA_real, imag = uiState.inA_imag, onRealChange = {viewModel.onValueChange("inA_real", it)}, onImagChange = {viewModel.onValueChange("inA_imag", it)})
            ComplexInputRow(label = inLabelB, real = uiState.inB_real, imag = uiState.inB_imag, onRealChange = {viewModel.onValueChange("inB_real", it)}, onImagChange = {viewModel.onValueChange("inB_imag", it)})
            ComplexInputRow(label = inLabelC, real = uiState.inC_real, imag = uiState.inC_imag, onRealChange = {viewModel.onValueChange("inC_real", it)}, onImagChange = {viewModel.onValueChange("inC_imag", it)})
        } else {
            val (inLabelA, inLabelB, inLabelC) = if (uiState.conversionType == ConversionType.DeltaToStar) Triple("Zₐ ($unit)", "Zₑ ($unit)", "Z𝒸 ($unit)") else Triple("Z₁ ($unit)", "Z₂ ($unit)", "Z₃ ($unit)")
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = uiState.inA, onValueChange = { viewModel.onValueChange("inA", it) }, label = { Text(inLabelA) }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.weight(1f))
                OutlinedTextField(value = uiState.inB, onValueChange = { viewModel.onValueChange("inB", it) }, label = { Text(inLabelB) }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.weight(1f))
                OutlinedTextField(value = uiState.inC, onValueChange = { viewModel.onValueChange("inC", it) }, label = { Text(inLabelC) }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.weight(1f))
            }
        }

        if (uiState.out1 != null || uiState.out1_complex != null) {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(text = stringResource(id = R.string.result_title), style = MaterialTheme.typography.titleLarge)
                    if(uiState.componentType == ComponentType.Complex){
                        val (outLabel1, outLabel2, outLabel3) = if (uiState.conversionType == ConversionType.DeltaToStar) Triple("Z₁", "Z₂", "Z₃") else Triple("Zₐ", "Zₑ", "Z𝒸")
                        ComplexResultRow(label = outLabel1, value = uiState.out1_complex)
                        ComplexResultRow(label = outLabel2, value = uiState.out2_complex)
                        ComplexResultRow(label = outLabel3, value = uiState.out3_complex)
                    } else {
                        val (outLabel1, outLabel2, outLabel3) = if (uiState.conversionType == ConversionType.DeltaToStar) Triple("Z₁ ($unit)", "Z₂ ($unit)", "Z₃ ($unit)") else Triple("Zₐ ($unit)", "Zₑ ($unit)", "Z𝒸 ($unit)")
                        FileResultRow(label = outLabel1, value = uiState.out1)
                        FileResultRow(label = outLabel2, value = uiState.out2)
                        FileResultRow(label = outLabel3, value = uiState.out3)
                    }
                }
            }
        }
    }
}

@Composable
private fun ComplexInputRow(label: String, real: String, imag: String, onRealChange: (String)->Unit, onImagChange: (String)->Unit) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
        Text("$label = ", style = MaterialTheme.typography.titleMedium, modifier = Modifier.width(48.dp))
        OutlinedTextField(value = real, onValueChange = onRealChange, label = { Text("Réel") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.weight(1f))
        Text("+ j", style = MaterialTheme.typography.titleMedium)
        OutlinedTextField(value = imag, onValueChange = onImagChange, label = { Text("Imag.") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.weight(1f))
    }
}


@Composable
private fun FileResultRow(label: String, value: Double?) {
    val formatter = DecimalFormat("#.###")
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyLarge)
        Text(
            text = value?.let { formatter.format(it) } ?: "N/A",
            style = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.primary)
        )
    }
}

@Composable
private fun ComplexResultRow(label: String, value: Complex?) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyLarge)
        Text(
            text = value?.toString() ?: "N/A",
            style = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.primary)
        )
    }
}
