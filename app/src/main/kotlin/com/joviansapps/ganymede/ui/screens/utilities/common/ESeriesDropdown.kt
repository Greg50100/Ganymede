package com.joviansapps.ganymede.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.joviansapps.ganymede.ui.screens.utilities.electronics.ESeries

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ESeriesDropdown(
    selectedSeries: ESeries,
    onSeriesSelected: (ESeries) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = selectedSeries.name,
            onValueChange = {},
            readOnly = true,
            label = { Text("Série E") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable, enabled = true).fillMaxWidth()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            ESeries.values().forEach { series ->
                DropdownMenuItem(
                    text = { Text(series.name) },
                    onClick = {
                        onSeriesSelected(series)
                        expanded = false
                    }
                )
            }
        }
    }
}
