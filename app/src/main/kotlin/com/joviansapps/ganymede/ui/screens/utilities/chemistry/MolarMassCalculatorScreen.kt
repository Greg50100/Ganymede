package com.joviansapps.ganymede.ui.screens.utilities.chemistry

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
import java.text.DecimalFormat

// --- 1. State and Data ---
data class MolarMassUiState(
    val formula: String = "H2O",
    val result: Double? = null,
    val error: String? = null
)

// --- 2. ViewModel ---
class MolarMassViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(MolarMassUiState())
    val uiState = _uiState.asStateFlow()

    // Map des masses atomiques standards
    private val atomicMasses = mapOf(
        "H" to 1.008, "He" to 4.0026, "Li" to 6.94, "Be" to 9.0122, "B" to 10.81,
        "C" to 12.011, "N" to 14.007, "O" to 15.999, "F" to 18.998, "Ne" to 20.180,
        "Na" to 22.990, "Mg" to 24.305, "Al" to 26.982, "Si" to 28.085, "P" to 30.974,
        "S" to 32.06, "Cl" to 35.45, "Ar" to 39.948, "K" to 39.098, "Ca" to 40.078,
        "Sc" to 44.956, "Ti" to 47.867, "V" to 50.942, "Cr" to 51.996, "Mn" to 54.938,
        "Fe" to 55.845, "Co" to 58.933, "Ni" to 58.693, "Cu" to 63.546, "Zn" to 65.38,
        "Ga" to 69.723, "Ge" to 72.630, "As" to 74.922, "Se" to 78.971, "Br" to 79.904,
        "Kr" to 83.798, "Rb" to 85.468, "Sr" to 87.62, "Y" to 88.906, "Zr" to 91.224,
        "Nb" to 92.906, "Mo" to 95.96, "Tc" to 98.0, "Ru" to 101.07, "Rh" to 102.91,
        "Pd" to 106.42, "Ag" to 107.87, "Cd" to 112.41, "In" to 114.82, "Sn" to 118.71,
        "Sb" to 121.76, "Te" to 127.60, "I" to 126.90, "Xe" to 131.29, "Cs" to 132.91,
        "Ba" to 137.33, "La" to 138.91, "Ce" to 140.12, "Pr" to 140.91, "Nd" to 144.24,
        "Pm" to 145.0, "Sm" to 150.36, "Eu" to 151.96, "Gd" to 157.25, "Tb" to 158.93,
        "Dy" to 162.50, "Ho" to 164.93, "Er" to 167.26, "Tm" to 168.93, "Yb" to 173.05,
        "Lu" to 174.97, "Hf" to 178.49, "Ta" to 180.95, "W" to 183.84, "Re" to 186.21,
        "Os" to 190.23, "Ir" to 192.22, "Pt" to 195.08, "Au" to 196.97, "Hg" to 200.59,
        "Tl" to 204.38, "Pb" to 207.2, "Bi" to 208.98, "Po" to 209.0, "At" to 210.0,
        "Rn" to 222.0, "Fr" to 223.0, "Ra" to 226.0, "Ac" to 227.0, "Th" to 232.04,
        "Pa" to 231.04, "U" to 238.03, "Np" to 237.0, "Pu" to 244.0, "Am" to 243.0,
        "Cm" to 247.0, "Bk" to 247.0, "Cf" to 251.0, "Es" to 252.0, "Fm" to 257.0,
        "Md" to 258.0, "No" to 259.0, "Lr" to 262.0, "Rf" to 267.0, "Db" to 270.0,
        "Sg" to 271.0, "Bh" to 270.0, "Hs" to 277.0, "Mt" to 276.0, "Ds" to 281.0,
        "Rg" to 280.0, "Cn" to 285.0, "Nh" to 284.0, "Fl" to 289.0, "Mc" to 288.0,
        "Lv" to 293.0, "Ts" to 294.0, "Og" to 294.0
    )

    init {
        calculate()
    }

    fun onFormulaChange(value: String) {
        _uiState.update { it.copy(formula = value) }
        calculate()
    }

    private fun calculate() {
        viewModelScope.launch {
            val formula = _uiState.value.formula
            if (formula.isBlank()) {
                _uiState.update { it.copy(result = null, error = null) }
                return@launch
            }
            try {
                val mass = parseFormula(formula)
                _uiState.update { it.copy(result = mass, error = null) }
            } catch (e: Exception) {
                _uiState.update { it.copy(result = null, error = e.message ?: "Formule non valide") }
            }
        }
    }

    // Analyseur de formule chimique simple
    private fun parseFormula(formula: String): Double {
        // Regex pour trouver un élément (ex: H, Cl) suivi d'un nombre optionnel (ex: 2)
        val regex = "([A-Z][a-z]?)(\\d*)".toRegex()
        val matches = regex.findAll(formula).toList()

        // Si la formule n'est pas vide mais qu'aucun élément n'est trouvé, elle est invalide.
        if (matches.isEmpty() && formula.isNotBlank()) {
            throw IllegalArgumentException("Format de formule non valide.")
        }

        // Vérifie que la formule reconstruite correspond à l'original.
        // Cela détecte les caractères invalides entre les éléments valides (ex: "H2_O").
        val reconstructedFormula = matches.joinToString("") { it.value }
        if (reconstructedFormula != formula) {
            if (formula.any { it in "()[]{}<>" }) {
                throw IllegalArgumentException("Les parenthèses et crochets ne sont pas pris en charge.")
            }
            throw IllegalArgumentException("Format de formule non valide.")
        }

        var totalMass = 0.0
        for (match in matches) {
            val element = match.groups[1]?.value ?: throw IllegalArgumentException("Erreur d'analyse de la formule")
            val countStr = match.groups[2]?.value
            val count = if (countStr.isNullOrEmpty()) 1 else countStr.toInt()
            val atomicMass = atomicMasses[element] ?: throw IllegalArgumentException("Élément inconnu : $element")
            totalMass += atomicMass * count
        }

        return totalMass
    }
}

// --- 3. Composable Screen ---
@Composable
fun MolarMassCalculatorScreen(viewModel: MolarMassViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val formatter = DecimalFormat("#.###")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Calculateur de Masse Molaire", style = MaterialTheme.typography.headlineSmall)

        OutlinedTextField(
            value = uiState.formula,
            onValueChange = viewModel::onFormulaChange,
            label = { Text("Formule chimique (ex: H2O, C6H12O6)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
            modifier = Modifier.fillMaxWidth(),
            isError = uiState.error != null
        )

        uiState.error?.let {
            Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
        }

        uiState.result?.let {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    Text(
                        text = stringResource(id = R.string.result_title),
                        style = MaterialTheme.typography.titleLarge
                    )
                    Text(
                        text = "${formatter.format(it)} g/mol",
                        style = MaterialTheme.typography.headlineMedium.copy(color = MaterialTheme.colorScheme.primary)
                    )
                }
            }
        }
    }
}

