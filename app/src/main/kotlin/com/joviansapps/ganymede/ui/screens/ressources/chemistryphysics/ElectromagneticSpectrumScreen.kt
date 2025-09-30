package com.joviansapps.ganymede.ui.screens.ressources.chemistryphysics

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.joviansapps.ganymede.R

private data class SpectrumBand(val name: String, val wavelength: String, val frequency: String)

@Composable
fun ElectromagneticSpectrumScreen() {
    val spectrum = listOf(
        SpectrumBand("Radio", "> 10 cm", "< 3 GHz"),
        SpectrumBand("Microwave", "1 mm - 10 cm", "3 - 300 GHz"),
        SpectrumBand("Infrared", "700 nm - 1 mm", "300 GHz - 430 THz"),
        SpectrumBand("Visible", "400 - 700 nm", "430 - 750 THz"),
        SpectrumBand("Ultraviolet", "10 - 400 nm", "750 THz - 30 PHz"),
        SpectrumBand("X-Rays", "0.01 - 10 nm", "30 PHz - 30 EHz"),
        SpectrumBand("Gamma Rays", "< 0.01 nm", "> 30 EHz")
    )

    Scaffold { padding ->
        Column(Modifier.padding(padding)) {
            //  would be ideal here.
            Text(
                text = stringResource(R.string.em_spectrum_description),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(16.dp)
            )
            LazyColumn {
                items(spectrum) { band ->
                    Card(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(band.name, style = MaterialTheme.typography.titleLarge)
                            Text("Wavelength: ${band.wavelength}", style = MaterialTheme.typography.bodyMedium)
                            Text("Frequency: ${band.frequency}", style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }
        }
    }
}
