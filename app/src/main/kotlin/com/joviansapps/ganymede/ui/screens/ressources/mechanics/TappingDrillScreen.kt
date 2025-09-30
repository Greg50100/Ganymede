package com.joviansapps.ganymede.ui.screens.ressources.mechanics

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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

private data class TapDrillSize(val thread: String, val drillSize: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TappingDrillScreen(onBack: (() -> Unit)? = null) {
    val metricSizes = listOf(
        TapDrillSize("M3 x 0.5", "2.5 mm"),
        TapDrillSize("M4 x 0.7", "3.3 mm"),
        TapDrillSize("M5 x 0.8", "4.2 mm"),
        TapDrillSize("M6 x 1.0", "5.0 mm"),
        TapDrillSize("M8 x 1.25", "6.8 mm"),
        TapDrillSize("M10 x 1.5", "8.5 mm"),
        TapDrillSize("M12 x 1.75", "10.2 mm")
    )

    Scaffold { padding ->
        LazyColumn(modifier = Modifier.padding(padding).padding(16.dp)) {
            item {
                Row(Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.primaryContainer).padding(vertical = 8.dp)) {
                    Text(
                        "Thread Size",
                        modifier = Modifier.weight(1f).padding(start = 8.dp),
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        "Drill Diameter",
                        modifier = Modifier.weight(1f),
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
            items(metricSizes) { size ->
                Row(Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                    Text(size.thread, modifier = Modifier.weight(1f).padding(start = 8.dp))
                    Text(size.drillSize, modifier = Modifier.weight(1f))
                }
            }
        }
    }
}
