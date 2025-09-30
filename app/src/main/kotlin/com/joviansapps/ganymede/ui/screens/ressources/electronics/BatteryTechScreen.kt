package com.joviansapps.ganymede.ui.screens.ressources.electronics

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

private data class BatteryInfo(
    val name: String,
    val voltage: String,
    val energyDensity: String,
    val cycleLife: String,
    val pros: List<String>,
    val cons: List<String>
)

@Composable
fun BatteryTechScreen() {
    val batteries = listOf(
        BatteryInfo(
            "Lithium-Ion (Li-ion)", "3.6V", "150-250 Wh/kg", "500-1500 cycles",
            listOf("High energy density", "Low self-discharge"),
            listOf("Requires protection circuit", "Ages even when not in use")
        ),
        BatteryInfo(
            "Lithium-Polymer (Li-Po)", "3.7V", "100-260 Wh/kg", "300-500 cycles",
            listOf("Flexible form factor", "Lighter weight"),
            listOf("Higher cost", "Shorter lifespan")
        ),
        BatteryInfo(
            "Nickel-Metal Hydride (NiMH)", "1.2V", "60-120 Wh/kg", "300-500 cycles",
            listOf("No memory effect (compared to NiCd)", "Environmentally friendly"),
            listOf("High self-discharge", "Lower voltage per cell")
        ),
        BatteryInfo(
            "Lead-Acid", "2.1V", "30-40 Wh/kg", "200-300 cycles",
            listOf("Low cost", "High surge current capability"),
            listOf("Very heavy", "Contains toxic lead")
        )
    )

    Scaffold { padding ->
        LazyColumn(modifier = Modifier.padding(padding)) {
            items(batteries) { battery ->
                BatteryCard(battery)
            }
        }
    }
}

@Composable
private fun BatteryCard(info: BatteryInfo) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(info.name, style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(8.dp))
            Text("Voltage: ${info.voltage}", style = MaterialTheme.typography.bodyMedium)
            Text("Energy Density: ${info.energyDensity}", style = MaterialTheme.typography.bodyMedium)
            Text("Cycle Life: ${info.cycleLife}", style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.height(8.dp))
            Text("Pros:", fontWeight = FontWeight.Bold)
            info.pros.forEach { Text("• $it", style = MaterialTheme.typography.bodyMedium) }
            Spacer(Modifier.height(8.dp))
            Text("Cons:", fontWeight = FontWeight.Bold)
            info.cons.forEach { Text("• $it", style = MaterialTheme.typography.bodyMedium) }
        }
    }
}
