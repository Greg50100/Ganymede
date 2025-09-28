package com.joviansapps.ganymede.ui.screens.ressources

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

private data class GreekLetter(val uppercase: String, val lowercase: String, val name: String)

private val greekAlphabet = listOf(
    GreekLetter("Α", "α", "Alpha"),
    GreekLetter("Β", "β", "Beta"),
    GreekLetter("Γ", "γ", "Gamma"),
    GreekLetter("Δ", "δ", "Delta"),
    GreekLetter("Ε", "ε", "Epsilon"),
    GreekLetter("Ζ", "ζ", "Zeta"),
    GreekLetter("Η", "η", "Eta"),
    GreekLetter("Θ", "θ", "Theta"),
    GreekLetter("Ι", "ι", "Iota"),
    GreekLetter("Κ", "κ", "Kappa"),
    GreekLetter("Λ", "λ", "Lambda"),
    GreekLetter("Μ", "μ", "Mu"),
    GreekLetter("Ν", "ν", "Nu"),
    GreekLetter("Ξ", "ξ", "Xi"),
    GreekLetter("Ο", "ο", "Omicron"),
    GreekLetter("Π", "π", "Pi"),
    GreekLetter("Ρ", "ρ", "Rho"),
    GreekLetter("Σ", "σ/ς", "Sigma"),
    GreekLetter("Τ", "τ", "Tau"),
    GreekLetter("Υ", "υ", "Upsilon"),
    GreekLetter("Φ", "φ", "Phi"),
    GreekLetter("Χ", "χ", "Chi"),
    GreekLetter("Ψ", "ψ", "Psi"),
    GreekLetter("Ω", "ω", "Omega")
)

@Composable
fun GreekAlphabetScreen() {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val headerStyle = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                Text("Majuscule", modifier = Modifier.weight(1f), style = headerStyle, textAlign = TextAlign.Center)
                Text("Minuscule", modifier = Modifier.weight(1f), style = headerStyle, textAlign = TextAlign.Center)
                Text("Nom", modifier = Modifier.weight(2f), style = headerStyle)
            }
        }
        itemsIndexed(greekAlphabet) { index, letter ->
            val backgroundColor = if (index % 2 == 0) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(backgroundColor)
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(letter.uppercase, modifier = Modifier.weight(1f), textAlign = TextAlign.Center, style = MaterialTheme.typography.bodyLarge)
                Text(letter.lowercase, modifier = Modifier.weight(1f), textAlign = TextAlign.Center, style = MaterialTheme.typography.bodyLarge)
                Text(letter.name, modifier = Modifier.weight(2f), style = MaterialTheme.typography.bodyLarge)
            }
            HorizontalDivider()
        }
    }
}
