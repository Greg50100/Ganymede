package com.joviansapps.ganymede.ui.screens.ressources.electronics

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private data class ConnectorPinout(val name: String, val pins: List<Pair<String, String>>)

@Composable
fun ConnectorsPinoutsScreen() {
    val connectors = listOf(
        ConnectorPinout("USB Type-A", listOf(
            "Pin 1" to "VCC (+5V)",
            "Pin 2" to "Data-",
            "Pin 3" to "Data+",
            "Pin 4" to "GND"
        )),
        ConnectorPinout("Ethernet (RJ45, T568B)", listOf(
            "Pin 1" to "TX+ (Orange/White)",
            "Pin 2" to "TX- (Orange)",
            "Pin 3" to "RX+ (Green/White)",
            "Pin 4" to "Unused (Blue)",
            "Pin 5" to "Unused (Blue/White)",
            "Pin 6" to "RX- (Green)",
            "Pin 7" to "Unused (Brown/White)",
            "Pin 8" to "Unused (Brown)"
        )),
        ConnectorPinout("HDMI Type A", listOf(
            "Pin 1-9" to "TMDS Data & Clock",
            "Pin 10-12" to "TMDS Clock",
            "Pin 13" to "CEC",
            "Pin 14" to "Reserved",
            "Pin 15-16" to "DDC (SCL/SDA)",
            "Pin 17" to "GND + CEC",
            "Pin 18" to "+5V Power",
            "Pin 19" to "Hot Plug Detect"
        ))
    )

    Scaffold { padding ->
        LazyColumn(modifier = Modifier.padding(padding)) {
            items(connectors) { connector ->
                ConnectorCard(connector)
            }
        }
    }
}

@Composable
private fun ConnectorCard(connector: ConnectorPinout) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(connector.name, style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(8.dp))
            connector.pins.forEach { pin ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "${pin.first}:",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        modifier = Modifier.width(80.dp)
                    )
                    Text(text = pin.second, fontSize = 14.sp)
                }
            }
        }
    }
}
