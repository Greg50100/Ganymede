package com.joviansapps.ganymede.ui.screens.ressources.electronics

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

private data class PackageInfo(val name: String, val fullName: String, val description: String)

@Composable
fun ComponentPackagesScreen() {
    val packages = listOf(
        PackageInfo("DIP", "Dual In-line Package", "Through-hole IC package with two parallel rows of pins. Easy to solder by hand."),
        PackageInfo("SOIC", "Small Outline Integrated Circuit", "Surface-mount package, smaller than DIP, with leads on two sides."),
        PackageInfo("QFP", "Quad Flat Package", "Surface-mount package with leads on all four sides."),
        PackageInfo("TO-92", "Transistor Outline 92", "Commonly used for small transistors. Plastic package with a flat front."),
        PackageInfo("TO-220", "Transistor Outline 220", "Used for power transistors, includes a metal tab for heat sinking.")
    )

    Scaffold { padding ->
        LazyColumn(modifier = Modifier.padding(padding)) {
            items(packages) { pkg ->
                PackageCard(pkg)
            }
        }
    }
}

@Composable
private fun PackageCard(info: PackageInfo) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("${info.name} - ${info.fullName}", style = MaterialTheme.typography.titleLarge)
            Text(info.description, style = MaterialTheme.typography.bodyMedium)
        }
    }
}
