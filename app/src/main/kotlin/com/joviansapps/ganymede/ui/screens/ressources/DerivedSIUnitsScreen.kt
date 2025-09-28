package com.joviansapps.ganymede.ui.screens.ressources

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.joviansapps.ganymede.ui.theme.AppTheme
import com.joviansapps.ganymede.viewmodel.ThemeMode

/**
 * Data class representing a derived SI unit, matching the user's specific table structure.
 */
data class DerivedUnit(
    val grandeurPhysique: String,
    val symbole: String,
    val usi: String,
    val nom: String,
    val fromOtherUsi: String,
    val m: Int,
    val l: Int,
    val t: Int,
    val i: Int,
    val theta: Int,
    val n: Int,
    val j: Int,
    val remarque: String
)

// Data transcribed exactly from the user's provided table.
val derivedSIUnits = listOf(
    // 22 units with special names, as requested
    DerivedUnit("Fréquence", "f", "Hz", "hertz", "s⁻¹", 0, 0, -1, 0, 0, 0, 0, "Fréquence = 1 / période"),
    DerivedUnit("Force", "F", "N", "newton", "kg·m·s⁻²", 1, 1, -2, 0, 0, 0, 0, "Force = masse × accélération"),
    DerivedUnit("Pression", "p", "Pa", "pascal", "N·m⁻², J·m⁻³", 1, -1, -2, 0, 0, 0, 0, "Pression = force / surface"),
    DerivedUnit("Énergie", "E", "J", "joule", "N·m", 1, 2, -2, 0, 0, 0, 0, "Travail = force × distance"),
    DerivedUnit("Puissance", "P", "W", "watt", "J·s⁻¹", 1, 2, -3, 0, 0, 0, 0, "Puissance = travail / temps"),
    DerivedUnit("Charge électrique", "q", "C", "coulomb", "A·s", 0, 0, 1, 1, 0, 0, 0, "Charge = intensité × temps"),
    DerivedUnit("Tension", "U", "V", "volt", "J·C⁻¹ ou J·s⁻¹·A⁻¹", 1, 2, -3, -1, 0, 0, 0, "Tension = travail / charge"),
    DerivedUnit("Capacité électrique", "C", "F", "farad", "C·V⁻¹", -1, -2, 4, 2, 0, 0, 0, "Capacité = charge / tension"),
    DerivedUnit("Résistance électrique", "R", "Ω", "ohm", "V·A⁻¹", 1, 2, -3, -2, 0, 0, 0, "Résistance = tension / intensité"),
    DerivedUnit("Conductance électrique", "G", "S", "siemens", "A·V⁻¹ ou Ω⁻¹", -1, -2, 3, 2, 0, 0, 0, "Conductance = intensité / tension"),
    DerivedUnit("Flux d'induction magnétique", "Φ", "Wb", "weber", "V·s", 1, 2, -2, -1, 0, 0, 0, "Flux d'induction = tension × temps"),
    DerivedUnit("Champ magnétique", "B", "T", "tesla", "kg·s⁻²·A⁻¹", 1, 0, -2, -1, 0, 0, 0, ""),
    DerivedUnit("Inductance électrique", "L", "H", "henry", "V·s·A⁻¹", 1, 2, -2, -2, 0, 0, 0, "Inductance = tension × temps / courant"),
    DerivedUnit("Température Celsius", "θ", "°C", "degré Celsius", "", 0, 0, 0, 0, 1, 0, 0, "θ(°C) = T(K) - 273,15"),
    DerivedUnit("Flux lumineux", "Φ", "lm", "lumen", "cd·sr", 0, 0, 0, 0, 0, 0, 1, ""),
    DerivedUnit("Éclairement lumineux", "E", "lx", "lux", "cd·sr·m⁻²", 0, -2, 0, 0, 0, 0, 1, ""),
    DerivedUnit("Activité d’un radionucléide", "j", "Bq", "becquerel", "s⁻¹", 0, 0, -1, 0, 0, 0, 0, "Désintégration par seconde"),
    DerivedUnit("Dose absorbée", "D", "Gy", "gray", "J·kg⁻¹", 0, 2, -2, 0, 0, 0, 0, ""),
    DerivedUnit("Dose efficace", "E", "Sv", "sievert", "J·kg⁻¹", 0, 2, -2, 0, 0, 0, 0, ""),
    DerivedUnit("Activité catalytique", "", "kat", "katal", "mol·s⁻¹", 0, 0, -1, 0, 0, 1, 0, ""),
    DerivedUnit("Angle plan", "α", "rad", "radian", "", 0, 0, 0, 0, 0, 0, 0, "1"),
    DerivedUnit("Angle solide", "Ω", "sr", "stéradian", "", 0, 0, 0, 0, 0, 0, 0, "1"),

    // Other units
    DerivedUnit("Accélération angulaire", "α", "rad·s⁻²", "radian par seconde carrée", "", 0, 0, -2, 0, 0, 0, 0, ""),
    DerivedUnit("Accélération", "a", "m·s⁻²", "mètre par seconde carrée", "", 0, 1, -2, 0, 0, 0, 0, ""),
    DerivedUnit("Action", "S", "J·s", "joule seconde", "", 1, 2, -1, 0, 0, 0, 0, "Énergie × temps"),
    DerivedUnit("Admittance", "Y", "S", "siemens", "A·V⁻¹", -1, -2, 3, 2, 0, 0, 0, "Inverse de l'impédance électrique"),
    DerivedUnit("Aimantation", "M", "A·m⁻¹", "ampère par mètre", "", 0, -1, 0, 1, 0, 0, 0, "Moment magnétique par unité de volume"),
    DerivedUnit("Capacité thermique", "c", "J·K⁻¹", "joule par kelvin", "", 1, 2, -2, 0, -1, 0, 0, "Chaleur par Kelvin"),
    DerivedUnit("Capacité thermique massique", "c", "J·kg⁻¹·K⁻¹", "joule par kilogramme-kelvin", "", 0, 2, -2, 0, -1, 0, 0, "Chaleur par Kelvin par kilogramme"),
    DerivedUnit("Capacité thermique molaire", "", "J·mol⁻¹·K⁻¹", "joule par mole", "", 1, 2, -2, 0, -1, -1, 0, "Chaleur par kelvin par mole"),
    DerivedUnit("Capacité thermique volumique", "", "J·m⁻³·K⁻¹", "joule par mètre cube-kelvin", "", 1, -1, -2, 0, -1, 0, 0, "Chaleur par kelvin par mètre cube"),
    DerivedUnit("Chaleur", "Q", "J", "joule", "N·m", 1, 2, -2, 0, 0, 0, 0, "(masse inertielle)"),
    DerivedUnit("Champ électrique", "E", "V·m⁻¹", "volt par mètre", "", 1, 1, -3, -1, 0, 0, 0, ""),
    DerivedUnit("Chemin optique", "L", "m", "mètre", "", 0, 1, 0, 0, 0, 0, 0, "Distance × indice de réfraction"),
    DerivedUnit("Coefficient d'absorption", "a", "m⁻¹", "", "", 0, -1, 0, 0, 0, 0, 0, ""),
    DerivedUnit("Coefficient de transfert thermique global", "a", "W·m⁻²·K⁻¹", "watt par mètre carré-kelvin", "", 1, 0, -3, 0, -1, 0, 0, ""),
    DerivedUnit("Concentration massique", "ρ", "kg·m⁻³", "kilogramme par mètre cube", "", 1, -3, 0, 0, 0, 0, 0, "(masse inerte : quantité de matière par mètre cube)"),
    DerivedUnit("Concentration molaire", "c", "mol·m⁻³", "mole par mètre cube", "", 0, -3, 0, 0, 0, 1, 0, ""),
    DerivedUnit("Conductance thermique", "", "W·K⁻¹", "", "", 1, 2, -3, 0, -1, 0, 0, "Puissance transférée / température"),
    DerivedUnit("Conductivité électrique", "σ", "S·m⁻¹", "", "", -1, -3, 3, 2, 0, 0, 0, ""),
    DerivedUnit("Conductivité hydraulique", "K", "m·s⁻¹", "", "", 0, 1, -1, 0, 0, 0, 0, ""),
    DerivedUnit("Conductivité thermique", "λ", "W·m⁻¹·K⁻¹", "watt par mètre-kelvin", "", 1, 1, -3, 0, -1, 0, 0, ""),
    DerivedUnit("Contrainte", "", "Pa", "pascal", "N·m⁻² ; J·m⁻³", 1, -1, -2, 0, 0, 0, 0, "Pression = force / surface"),
    DerivedUnit("Couple", "C", "N·m", "newton mètre", "", 1, 2, -2, 0, 0, 0, 0, "Force x bras de levier"),
    DerivedUnit("Débit massique", "", "kg·s⁻¹", "kilogramme par seconde", "", 1, 0, -1, 0, 0, 0, 0, "(masse inerte : quantité de matière par seconde)"),
    DerivedUnit("Débit volumique", "", "m³·s⁻¹", "mètre cube par seconde", "", 0, 3, -1, 0, 0, 0, 0, ""),
    DerivedUnit("Débit de dose radioactive", "D", "Gy·s⁻¹", "", "", 0, 2, -3, 0, 0, 0, 0, ""),
    DerivedUnit("Densité de charge", "", "C·m⁻³", "", "", 0, -3, 1, 1, 0, 0, 0, ""),
    DerivedUnit("Densité de colonne", "N", "m⁻²", "", "", 0, -2, 0, 0, 0, 0, 0, "Intégrale de la densité volumique"),
    DerivedUnit("Densité de courant", "j", "A·m⁻²", "ampère par mètre carré", "", 0, -2, 0, 1, 0, 0, 0, ""),
    DerivedUnit("Densité de flux thermique", "φ", "W·m⁻²", "watt par mètre carré", "", 1, 0, -3, 0, 0, 0, 0, "Flux thermique par unité de surface"),
    DerivedUnit("Densité de flux", "F", "W·m⁻²·Hz⁻¹", "", "", 1, 0, -2, 0, 0, 0, 0, "Flux électromagnétique par unité de fréquence"),
    DerivedUnit("Densité surfacique de puissance", "", "W·m⁻²", "watt par mètre carré", "", 1, 0, -3, 0, 0, 0, 0, "Débit d'énergie par unité de surface"),
    DerivedUnit("Densité de puissance volumique", "", "W·m⁻³", "", "", 1, -1, -3, 0, 0, 0, 0, "Puissance par unité de volume"),
    DerivedUnit("Densité volumique", "n", "m⁻³", "", "", 0, -3, 0, 0, 0, 0, 0, "Nombre d'objets par unité de volume"),
    DerivedUnit("Diffusivité thermique", "D", "m²·s⁻¹", "", "", 0, 2, -1, 0, 0, 0, 0, ""),
    DerivedUnit("Dose équivalente", "H", "Sv", "sievert", "J·kg⁻¹", 0, 2, -2, 0, 0, 0, 0, ""),
    DerivedUnit("Durée", "t", "s", "secondes", "", 0, 0, 1, 0, 0, 0, 0, ""),
    DerivedUnit("Éclairement énergétique", "ϕ", "W·m⁻²", "watt par mètre carré", "", 1, 0, -3, 0, 0, 0, 0, "Flux d'énergie par unité de surface"),
    DerivedUnit("Énergie cinétique", "E", "J", "joule", "N·m", 1, 2, -2, 0, 0, 0, 0, "Énergie cinétique = masse × vitesse²/2"),
    DerivedUnit("Enthalpie", "H", "J", "joule", "N·m", 1, 2, -2, 0, 0, 0, 0, ""),
    DerivedUnit("Entropie", "S", "J·K⁻¹", "", "", 1, 2, -2, 0, -1, 0, 0, ""),
    DerivedUnit("Exposition (rayonnement ionisant)", "X", "C·kg⁻¹", "", "", -1, 0, 1, 1, 0, 0, 0, ""),
    DerivedUnit("Fluence", "Φ", "m⁻²", "", "", 0, -2, 0, 0, 0, 0, 0, "Nombre de traversée par unité de surface"),
    DerivedUnit("Flux électrique", "Φ", "V·m", "", "", 1, 3, -3, -1, 0, 0, 0, ""),
    DerivedUnit("Flux énergétique", "Φ", "W", "watt", "", 1, 2, -3, 0, 0, 0, 0, "Énergie par unité de temps"),
    DerivedUnit("Flux thermique", "Φ", "kg·m²·s⁻³", "", "", 1, 2, -3, 0, 0, 0, 0, "Flux énergétique à travers une surface"),
    DerivedUnit("Force électromotrice", "e", "V", "volt", "J·C⁻¹ ou J·s⁻¹·A⁻¹", 1, 2, -3, -1, 0, 0, 0, "Tension = travail / charge"),
    DerivedUnit("Impédance mécanique", "Z", "kg·s⁻¹", "", "", 1, 0, -1, 0, 0, 0, 0, "Force / vitesse, pour une fréquence donnée"),
    DerivedUnit("Indice de réfraction", "n", "", "", "", 0, 0, 0, 0, 0, 0, 0, "Vitesse milieu / vitesse dans le vide"),
    DerivedUnit("Induction magnétique", "F", "T", "tesla", "V·s·m⁻²", 1, 0, -2, -1, 0, 0, 0, "Induction = tension × temps / surface"),
    DerivedUnit("Intensité acoustique", "I", "W·m⁻²", "watt par mètre carré", "", 1, 0, -3, 0, 0, 0, 0, "Puissance par unité de surface"),
    DerivedUnit("Intensité électrique", "I", "A", "ampère", "", 0, 0, 0, 1, 0, 0, 0, ""),
    DerivedUnit("Intensité énergétique", "I", "W·sr⁻¹", "watt par stéradian", "", 1, 2, -3, 0, 0, 0, 0, "Flux énergétique par unité d'angle solide"),
    DerivedUnit("Intensité lumineuse", "I", "cd", "candela", "", 0, 0, 0, 0, 0, 0, 1, ""),
    DerivedUnit("Kerma", "K", "Gy", "gray", "J·kg⁻¹", 0, 2, -2, 0, 0, 0, 0, ""),
    DerivedUnit("Longueur", "l", "m", "mètre", "", 0, 1, 0, 0, 0, 0, 0, ""),
    DerivedUnit("Luminance", "L", "cd·m⁻²", "candela par mètre carré", "", 0, -2, 0, 0, 0, 0, 1, ""),
    DerivedUnit("Masse linéique", "λ", "kg·m⁻¹", "", "", 1, -1, 0, 0, 0, 0, 0, "Quantité de matière par mètre"),
    DerivedUnit("Masse surfacique", "σ", "kg·m⁻²", "kilogramme par mètre carré", "", 1, -2, 0, 0, 0, 0, 0, "Quantité de matière par mètre carré"),
    DerivedUnit("Masse volumique", "ρ", "kg·m⁻³", "kilogramme par mètre cube", "", 1, -3, 0, 0, 0, 0, 0, "Quantité de matière par mètre cube"),
    DerivedUnit("Moment cinétique", "L", "kg·m²·s⁻¹", "", "", 1, 2, -1, 0, 0, 0, 0, ""),
    DerivedUnit("Moment d'inertie", "J", "kg·m²", "", "", 1, 2, 0, 0, 0, 0, 0, ""),
    DerivedUnit("Moment d'une force", "M", "N·m", "newton mètre", "", 1, 2, -2, 0, 0, 0, 0, ""),
    DerivedUnit("Moment magnétique", "μ", "A·m²", "", "", 0, 2, 0, 1, 0, 0, 0, ""),
    DerivedUnit("Moment quadratique", "l", "m⁴", "", "", 0, 4, 0, 0, 0, 0, 0, ""),
    DerivedUnit("Moment statique", "S", "m³", "mètre cube", "", 0, 3, 0, 0, 0, 0, 0, ""),
    DerivedUnit("Nombre d'onde", "k", "rad·m⁻¹", "radian par mètre", "", 0, -1, 0, 0, 0, 0, 0, ""),
    DerivedUnit("Perméabilité magnétique", "μ", "H·m⁻¹", "", "", 1, 1, -2, -2, 0, 0, 0, ""),
    DerivedUnit("Permittivité", "ε", "F·m⁻¹", "farad par mètre", "", -1, -3, 4, 2, 0, 0, 0, ""),
    DerivedUnit("Puissance apparente", "P", "VA", "voltampère", "W", 1, 2, -3, 0, 0, 0, 0, "Puissance apparente = intensité × tension"),
    DerivedUnit("Quantité de lumière", "", "lm·s", "lumen seconde", "", 0, 0, 1, 0, 0, 0, 1, ""),
    DerivedUnit("Quantité de matière", "n", "mol", "mole", "", 0, 0, 0, 0, 0, 1, 0, ""),
    DerivedUnit("Quantité de mouvement", "p", "kg·m·s⁻¹", "", "", 1, 1, -1, 0, 0, 0, 0, "Quantité de mouvement = masse × vitesse"),
    DerivedUnit("Raideur", "k", "N·m⁻¹", "newton par mètre", "", 1, 0, -2, 0, 0, 0, 0, "Raideur = force / déplacement"),
    DerivedUnit("Résistance thermique", "", "K·W⁻¹", "kelvin par watt", "R", -1, -2, 3, 0, 1, 0, 0, ""),
    DerivedUnit("Résistance thermique surfacique", "", "m²·K·W⁻¹", "mètre carré-kelvin par watt", "R", -1, -2, 3, 0, 1, 0, 0, ""),
    DerivedUnit("Superficie", "S", "m²", "mètre carré", "", 0, 2, 0, 0, 0, 0, 0, ""),
    DerivedUnit("Taux de cisaillement", "γ", "s⁻¹", "", "", 0, 0, -1, 0, 0, 0, 0, "Gradient de vitesse"),
    DerivedUnit("Température inverse", "β", "J⁻¹", "", "", -1, -2, 2, 0, 0, 0, 0, ""),
    DerivedUnit("Température", "T", "K", "kelvin", "", 0, 0, 0, 0, 1, 0, 0, ""),
    DerivedUnit("Tension superficielle", "γ", "N·m⁻¹", "newton par mètre", "", 1, 0, -2, 0, 0, 0, 0, ""),
    DerivedUnit("Travail d'une force", "W", "J", "joule", "N·m", 1, 2, -2, 0, 0, 0, 0, "Travail = force × distance"),
    DerivedUnit("Viscosité cinématique", "v", "m²·s⁻¹", "mètre carré par seconde", "", 0, 2, -1, 0, 0, 0, 0, ""),
    DerivedUnit("Viscosité dynamique", "μ", "Pa·s", "", "", 1, -1, -1, 0, 0, 0, 0, ""),
    DerivedUnit("Vitesse angulaire", "ω", "rad·s⁻¹", "", "", 0, 0, -1, 0, 0, 0, 0, ""),
    DerivedUnit("Vitesse de déformation", "", "s⁻¹", "", "", 0, 0, -1, 0, 0, 0, 0, ""),
    DerivedUnit("Vitesse", "v", "m·s⁻¹", "mètre par seconde", "", 0, 1, -1, 0, 0, 0, 0, ""),
    DerivedUnit("Volume massique", "v", "m³·kg⁻¹", "", "", -1, 3, 0, 0, 0, 0, 0, ""),
    DerivedUnit("Volume molaire", "", "m³·mol⁻¹", "", "", 0, 3, 0, 0, 0, -1, 0, ""),
    DerivedUnit("Volume", "V", "m³", "mètre cube", "", 0, 3, 0, 0, 0, 0, 0, "")
)

@Composable
fun DerivedSIUnitsScreen(modifier: Modifier = Modifier) {
    val hScroll = rememberScrollState()

    // Appliquer la même couleur de surface que les utilities
    Surface(modifier = modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(modifier = Modifier.padding(8.dp)) {
            Text(
                text = "Unités dérivées du système international",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(vertical = 6.dp)
            )
            Text(
                text = "Appuyez sur une cellule pour copier; appui long pour voir les détails.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Column(modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(hScroll)
            ) {
                // Header Row with requested columns
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .padding(vertical = 8.dp, horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Grandeur physique", modifier = Modifier.width(180.dp), fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.primary)
                    Text("S.", modifier = Modifier.width(40.dp), fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.primary, textAlign = TextAlign.Center)
                    Text("USI", modifier = Modifier.width(100.dp), fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.primary, textAlign = TextAlign.Center)
                    Text("Nom", modifier = Modifier.width(170.dp), fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.primary, textAlign = TextAlign.Center)
                    Text("À partir d'autres USI", modifier = Modifier.width(150.dp), fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.primary, textAlign = TextAlign.Center)
                    Text("M", modifier = Modifier.width(36.dp), fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.primary, textAlign = TextAlign.Center)
                    Text("L", modifier = Modifier.width(36.dp), fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.primary, textAlign = TextAlign.Center)
                    Text("T", modifier = Modifier.width(36.dp), fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.primary, textAlign = TextAlign.Center)
                    Text("I", modifier = Modifier.width(36.dp), fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.primary, textAlign = TextAlign.Center)
                    Text("Θ", modifier = Modifier.width(36.dp), fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.primary, textAlign = TextAlign.Center)
                    Text("N", modifier = Modifier.width(36.dp), fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.primary, textAlign = TextAlign.Center)
                    Text("J", modifier = Modifier.width(36.dp), fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.primary, textAlign = TextAlign.Center)
                    Text("Remarque", modifier = Modifier.width(250.dp), fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.primary)
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

                LazyColumn {
                    items(derivedSIUnits) { unit ->
                        DerivedUnitRowScrollable(unit)
                        HorizontalDivider()
                    }
                }
            }
        }
    }
}

@Composable
private fun DerivedUnitRowScrollable(unit: DerivedUnit) {
    val dialogState = remember { mutableStateOf<DerivedUnit?>(null) }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        CopyableCell(unit.grandeurPhysique, Modifier.width(180.dp)) { dialogState.value = unit }
        CopyableCell(unit.symbole, Modifier.width(40.dp), TextAlign.Center) { dialogState.value = unit }
        CopyableCell(unit.usi, Modifier.width(100.dp), TextAlign.Center, FontFamily.Monospace) { dialogState.value = unit }
        CopyableCell(unit.nom, Modifier.width(170.dp), TextAlign.Center) { dialogState.value = unit }
        CopyableCell(unit.fromOtherUsi, Modifier.width(150.dp), TextAlign.Center, FontFamily.Monospace) { dialogState.value = unit }
        CopyableCell(unit.m.toString(), Modifier.width(36.dp), TextAlign.Center) { dialogState.value = unit }
        CopyableCell(unit.l.toString(), Modifier.width(36.dp), TextAlign.Center) { dialogState.value = unit }
        CopyableCell(unit.t.toString(), Modifier.width(36.dp), TextAlign.Center) { dialogState.value = unit }
        CopyableCell(unit.i.toString(), Modifier.width(36.dp), TextAlign.Center) { dialogState.value = unit }
        CopyableCell(unit.theta.toString(), Modifier.width(36.dp), TextAlign.Center) { dialogState.value = unit }
        CopyableCell(unit.n.toString(), Modifier.width(36.dp), TextAlign.Center) { dialogState.value = unit }
        CopyableCell(unit.j.toString(), Modifier.width(36.dp), TextAlign.Center) { dialogState.value = unit }
        CopyableCell(unit.remarque, Modifier.width(250.dp)) { dialogState.value = unit }
    }

    // Dialog to show full row info on long press
    dialogState.value?.let { selected ->
        UnitDetailDialog(
            unit = selected,
            onDismiss = { dialogState.value = null }
        )
    }
}

@Composable
private fun UnitDetailDialog(unit: DerivedUnit, onDismiss: () -> Unit) {
    val clipboard = LocalClipboardManager.current
    val context = LocalContext.current
    val formatted = """
        Grandeur physique: ${unit.grandeurPhysique}
        Symbole: ${unit.symbole}
        USI: ${unit.usi}
        Nom: ${unit.nom}
        À partir d'autres USI: ${unit.fromOtherUsi}
        Dimensions: M:${unit.m} L:${unit.l} T:${unit.t} I:${unit.i} Θ:${unit.theta} N:${unit.n} J:${unit.j}
        Remarque: ${unit.remarque}
    """.trimIndent()

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                clipboard.setText(AnnotatedString(formatted))
                Toast.makeText(context, "Copié: ${unit.grandeurPhysique}", Toast.LENGTH_SHORT).show()
                onDismiss()
            }) {
                Text("Copier")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Fermer") }
        },
        title = { Text(text = "Détails de: ${unit.grandeurPhysique}") },
        text = {
            Column {
                Text("Grandeur physique: ${unit.grandeurPhysique}")
                Text("Symbole: ${unit.symbole}")
                Text("USI: ${unit.usi}")
                Text("Nom: ${unit.nom}")
                Text("À partir d'autres USI: ${unit.fromOtherUsi}")
                Text("Dimensions: M:${unit.m} L:${unit.l} T:${unit.t} I:${unit.i} Θ:${unit.theta} N:${unit.n} J:${unit.j}")
                if (unit.remarque.isNotEmpty()) Text("Remarque: ${unit.remarque}")
            }
        }
    )
}

@Composable
private fun CopyableCell(
    text: String,
    modifier: Modifier,
    textAlign: TextAlign = TextAlign.Start,
    fontFamily: FontFamily? = null,
    onLongPress: () -> Unit
) {
    val clipboard = LocalClipboardManager.current
    val context = LocalContext.current
    Text(
        text = text,
        modifier = modifier
            .pointerInput(text) {
                detectTapGestures(
                    onTap = {
                        clipboard.setText(AnnotatedString(text))
                        Toast.makeText(context, "Copié: $text", Toast.LENGTH_SHORT).show()
                    },
                    onLongPress = { onLongPress() }
                )
            },
        textAlign = textAlign,
        fontFamily = fontFamily,
        maxLines = 2,
        overflow = TextOverflow.Ellipsis
    )
}


@Preview(showBackground = true)
@Composable
fun DerivedSIUnitsScreenPreview() {
    AppTheme(themeMode = ThemeMode.AUTO) {
        DerivedSIUnitsScreen()
    }
}
