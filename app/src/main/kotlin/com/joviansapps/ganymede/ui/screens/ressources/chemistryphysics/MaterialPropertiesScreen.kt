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

private data class Material(val name: String, val density: String, val meltingPoint: String)

@Composable
fun MaterialPropertiesScreen() {
    val materials = listOf(
        Material("Aluminum", "2.70 g/cm³", "660 °C"),
        Material("Copper", "8.96 g/cm³", "1084 °C"),
        Material("Steel (Carbon)", "7.85 g/cm³", "1425-1540 °C"),
        Material("Titanium", "4.51 g/cm³", "1668 °C"),
        Material("PVC", "1.38 g/cm³", "100-260 °C"),
        Material("Water (liquid)", "1.00 g/cm³", "0 °C (freezing)")
    )

    Scaffold { padding ->
        LazyColumn(modifier = Modifier.padding(padding).padding(16.dp)) {
            item {
                Row(Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.primaryContainer).padding(vertical = 8.dp)) {
                    Text("Material", modifier = Modifier.weight(1.5f).padding(start = 8.dp), fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimaryContainer)
                    Text("Density", modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimaryContainer)
                    Text("Melting Point", modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimaryContainer)
                }
            }
            items(materials) { mat ->
                Row(Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                    Text(mat.name, modifier = Modifier.weight(1.5f).padding(start = 8.dp))
                    Text(mat.density, modifier = Modifier.weight(1f))
                    Text(mat.meltingPoint, modifier = Modifier.weight(1f))
                }
            }
        }
    }
}
