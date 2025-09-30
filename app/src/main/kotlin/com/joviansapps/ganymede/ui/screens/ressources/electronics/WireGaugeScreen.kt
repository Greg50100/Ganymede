package com.joviansapps.ganymede.ui.screens.ressources.electronics

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.ExperimentalMaterial3Api

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.joviansapps.ganymede.R

data class WireGaugeInfo(val awg: String, val diameterMm: String, val sectionMm2: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WireGaugeScreen() {
    val wireGauges = listOf(
        WireGaugeInfo("4/0", "11.68", "107.2"),
        WireGaugeInfo("2/0", "9.266", "67.4"),
        WireGaugeInfo("0", "8.252", "53.5"),
        WireGaugeInfo("2", "6.544", "33.6"),
        WireGaugeInfo("4", "5.189", "21.2"),
        WireGaugeInfo("6", "4.115", "13.3"),
        WireGaugeInfo("8", "3.264", "8.37"),
        WireGaugeInfo("10", "2.588", "5.26"),
        WireGaugeInfo("12", "2.053", "3.31"),
        WireGaugeInfo("14", "1.628", "2.08"),
        WireGaugeInfo("16", "1.291", "1.31"),
        WireGaugeInfo("18", "1.024", "0.823"),
        WireGaugeInfo("20", "0.812", "0.518"),
        WireGaugeInfo("22", "0.644", "0.326"),
        WireGaugeInfo("24", "0.511", "0.205"),
        WireGaugeInfo("26", "0.405", "0.129"),
        WireGaugeInfo("28", "0.321", "0.0810"),
        WireGaugeInfo("30", "0.255", "0.0509")
    )

    Scaffold { padding ->
        LazyColumn(modifier = Modifier.padding(padding).padding(16.dp)) {
            item {
                WireGaugeHeader()
            }
            items(wireGauges) { gauge ->
                WireGaugeRow(info = gauge)
            }
        }
    }
}

@Composable
fun WireGaugeHeader() {
    Row(
        Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primaryContainer)
            .padding(vertical = 8.dp)
    ) {
        Text("AWG", modifier = Modifier.weight(1f).padding(start=8.dp), fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimaryContainer)
        Text("Diamètre (mm)", modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimaryContainer)
        Text("Section (mm²)", modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimaryContainer)
    }
}

@Composable
fun WireGaugeRow(info: WireGaugeInfo) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(info.awg, modifier = Modifier.weight(1f).padding(start=8.dp))
        Text(info.diameterMm, modifier = Modifier.weight(1f))
        Text(info.sectionMm2, modifier = Modifier.weight(1f))
    }
}
