package com.joviansapps.ganymede.ui.screens.ressources.chemistryphysics

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

private data class RedoxPair(val pair: String, val potential: String)

@Composable
fun RedoxPotentialScreen() {
    val potentials = listOf(
        RedoxPair("Li⁺ + e⁻ ⇌ Li(s)", "-3.04 V"),
        RedoxPair("Zn²⁺ + 2e⁻ ⇌ Zn(s)", "-0.76 V"),
        RedoxPair("2H⁺ + 2e⁻ ⇌ H₂(g)", "0.00 V (Ref)"),
        RedoxPair("Cu²⁺ + 2e⁻ ⇌ Cu(s)", "+0.34 V"),
        RedoxPair("Ag⁺ + e⁻ ⇌ Ag(s)", "+0.80 V"),
        RedoxPair("O₂ + 4H⁺ + 4e⁻ ⇌ 2H₂O", "+1.23 V"),
        RedoxPair("F₂ + 2e⁻ ⇌ 2F⁻", "+2.87 V")
    )

    Scaffold { padding ->
        LazyColumn(modifier = Modifier.padding(padding).padding(16.dp)) {
            item {
                Row(Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.primaryContainer).padding(vertical = 8.dp)) {
                    Text("Redox Couple", modifier = Modifier.weight(2f).padding(start = 8.dp), fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimaryContainer)
                    Text("E° (V)", modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimaryContainer)
                }
            }
            items(potentials) { p ->
                Row(Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                    Text(p.pair, modifier = Modifier.weight(2f).padding(start = 8.dp))
                    Text(p.potential, modifier = Modifier.weight(1f))
                }
            }
        }
    }
}
