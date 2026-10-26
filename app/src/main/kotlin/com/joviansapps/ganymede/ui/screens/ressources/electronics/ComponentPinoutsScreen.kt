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
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.joviansapps.ganymede.R

data class PinoutInfo(val componentName: String, val pins: List<Pair<String, String>>, val description: String = "")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComponentPinoutsScreen() {
    val pinouts = listOf(
        PinoutInfo(
            componentName = "Regulateur de Tension (7805)",
            pins = listOf(
                "Pin 1" to "Input (IN)",
                "Pin 2" to "Ground (GND)",
                "Pin 3" to "Output (OUT)"
            ),
            description = "Régulateur de tension positive +5V."
        ),
        PinoutInfo(
            componentName = "Timer (NE555)",
            pins = listOf(
                "Pin 1" to "Ground (GND)",
                "Pin 2" to "Trigger (TRIG)",
                "Pin 3" to "Output (OUT)",
                "Pin 4" to "Reset (RESET)",
                "Pin 5" to "Control Voltage (CTRL)",
                "Pin 6" to "Threshold (THRES)",
                "Pin 7" to "Discharge (DISCH)",
                "Pin 8" to "Supply Voltage (VCC)"
            ),
            description = "Circuit intégré utilisé pour la temporisation et les multivibrateurs."
        ),
        // TO-92 : face plate vers soi, pattes vers le bas, broche 1 à gauche.
        // Les BC5xx et les 2N2222 / 2N2907 n'ont pas le même brochage : fiches onsemi BC546, BC556, PN2222A, PN2907A.
        PinoutInfo(
            componentName = "Transistor NPN (BC547)",
            pins = listOf(
                "Pin 1" to "Collector (C)",
                "Pin 2" to "Base (B)",
                "Pin 3" to "Emitter (E)"
            ),
            description = "Boîtier TO-92, face plate vers soi, pattes vers le bas. Même brochage : BC546, BC548, BC549, BC550, BC337."
        ),
        PinoutInfo(
            componentName = "Transistor PNP (BC557)",
            pins = listOf(
                "Pin 1" to "Collector (C)",
                "Pin 2" to "Base (B)",
                "Pin 3" to "Emitter (E)"
            ),
            description = "Boîtier TO-92, face plate vers soi, pattes vers le bas. Même brochage : BC556, BC558, BC559, BC560, BC327."
        ),
        PinoutInfo(
            componentName = "Transistor NPN (2N2222A / PN2222A)",
            pins = listOf(
                "Pin 1" to "Emitter (E)",
                "Pin 2" to "Base (B)",
                "Pin 3" to "Collector (C)"
            ),
            description = "Boîtier TO-92, face plate vers soi, pattes vers le bas : l'inverse du BC547. Exception : le P2N2222A d'onsemi est câblé C, B, E. En boîtier métal (TO-18), l'ergot repère l'émetteur."
        ),
        PinoutInfo(
            componentName = "Transistor PNP (2N2907A / PN2907A)",
            pins = listOf(
                "Pin 1" to "Emitter (E)",
                "Pin 2" to "Base (B)",
                "Pin 3" to "Collector (C)"
            ),
            description = "Boîtier TO-92, face plate vers soi, pattes vers le bas : l'inverse du BC557. En boîtier métal (TO-18), l'ergot repère l'émetteur."
        ),
        PinoutInfo(
            componentName = "Amplificateur Opérationnel (LM741)",
            pins = listOf(
                "Pin 1" to "Offset Null",
                "Pin 2" to "Inverting Input (-)",
                "Pin 3" to "Non-Inverting Input (+)",
                "Pin 4" to "V-",
                "Pin 5" to "Offset Null",
                "Pin 6" to "Output",
                "Pin 7" to "V+",
                "Pin 8" to "Not Connected (NC)"
            ),
            description = "AOP standard en boîtier DIP-8."
        )
    )

    Scaffold { padding ->
        LazyColumn(modifier = Modifier.padding(padding)) {
            items(pinouts) { pinout ->
                PinoutCard(pinoutInfo = pinout)
            }
        }
    }
}

@Composable
fun PinoutCard(pinoutInfo: PinoutInfo) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(pinoutInfo.componentName, style = MaterialTheme.typography.titleLarge)
            if (pinoutInfo.description.isNotEmpty()) {
                Text(pinoutInfo.description, style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(bottom = 8.dp))
            }
            Spacer(modifier = Modifier.height(8.dp))
            // Placeholder for pinout image/diagram
            // You could draw a simple representation here using Canvas or basic Composables
            // For now, we list the pins
            pinoutInfo.pins.forEach { pin ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "${pin.first}:",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        modifier = Modifier.width(60.dp)
                    )
                    Text(text = pin.second, fontSize = 14.sp)
                }
            }
        }
    }
}
