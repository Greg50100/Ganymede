/**
 * File: SIUnitsScreen.kt
 * Project: Ganymede
 *
 * Author: Greg50100
 * Date: 28/09/2025
 *
 * Description:
 * Composable screen that lists and explains SI units used throughout the application.
 * Provides reference information for unit symbols, names, and standard usage.
 *
 * Repository: https://github.com/Greg50100/Ganymede
 */

package com.joviansapps.ganymede.ui.screens.ressources

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.joviansapps.ganymede.ui.theme.AppTheme
import com.joviansapps.ganymede.viewmodel.ThemeMode

// data class adaptée : on garde la grandeur (ex: "temps"), le symbole caractéristique (ex: "t"),
// puis le nom de l'unité SI (ex: "seconde") et son symbole (ex: "s").
data class SIUnit(
    val quantity: String,
    val characteristicSymbol: String,
    val name: String,
    val symbol: String
)

// La liste des unités de base avec le symbole caractéristique fourni dans le tableau.
val siBaseUnits = listOf(
    SIUnit("temps", "T", "seconde", "s"),
    SIUnit("longueur", "L", "mètre", "m"),
    SIUnit("masse", "M", "kilogramme", "kg"),
    SIUnit("courant électrique", "I", "ampère", "A"),
    SIUnit("température thermodynamique", "\u03B8", "kelvin", "K"),
    SIUnit("quantité de matière", "N", "mole", "mol"),
    SIUnit("intensité lumineuse", "J", "candela", "cd")
)

@Composable
fun SIUnitsScreen(modifier: Modifier = Modifier) {
    // Appliquer explicitement la couleur de fond utilisée dans les écrans utilities
    Surface(modifier = modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(modifier = Modifier.padding(8.dp)) {
            // En-tête: Grandeur de base | Symbole caractéristique | Nom | Symbole
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(vertical = 8.dp, horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Grandeur de base",
                    modifier = Modifier.weight(1.2f),
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    "Dimens°",
                    modifier = Modifier.weight(0.6f),
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center
                )
                Text(
                    "Nom",
                    modifier = Modifier.weight(0.7f),
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center
                )
                Text(
                    "Sy",
                    modifier = Modifier.weight(0.3f),
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center
                )
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

            LazyColumn {
                items(siBaseUnits) { unit ->
                    UnitRow(unit)
                    HorizontalDivider()
                }
            }
        }
    }
}

@Composable
private fun UnitRow(unit: SIUnit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = unit.quantity,
            modifier = Modifier.weight(1.2f)
        )
        Text(
            text = unit.characteristicSymbol,
            modifier = Modifier.weight(0.5f),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            text = unit.name,
            modifier = Modifier.weight(0.7f),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            text = unit.symbol,
            modifier = Modifier.weight(0.3f),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SIUnitsScreenPreview() {
    AppTheme(themeMode = ThemeMode.AUTO) {
        SIUnitsScreen()
    }
}
