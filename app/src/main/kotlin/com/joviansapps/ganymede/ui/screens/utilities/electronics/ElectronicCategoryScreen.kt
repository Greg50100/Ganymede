package com.joviansapps.ganymede.ui.screens.utilities.electronics

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.joviansapps.ganymede.R

private data class ElectronicUtility(
    val titleRes: Int,
    val descriptionRes: Int,
    val icon: ImageVector,
    val onClick: () -> Unit
)

@Composable
@Preview
fun ElectronicCategoryScreen(
    onOpenResistorCalculator: () -> Unit = {},
    onOpenInductanceCalculator: () -> Unit = {},
    onOpenTimeConstantCalculator: () -> Unit = {},
    onOpenParallelSeriesResistorCalculator: () -> Unit = {},
    onOpenParallelSeriesCapacitorCalculator: () -> Unit = {},
    onOpenOhmsLawCalculator: () -> Unit = {},
    onOpenVoltageDividerCalculator: () -> Unit = {}
) {
    val utilities = listOf(
        ElectronicUtility(
            R.string.resistor_calculator_title,
            R.string.resistor_calculator_description,
            Icons.Default.Tune,
            onOpenResistorCalculator
        ),
        ElectronicUtility(
            R.string.inductance_calculator_title,
            R.string.inductance_calculator_description,
            Icons.Default.LooksOne, // Using a generic icon, can be replaced
            onOpenInductanceCalculator
        ),
        ElectronicUtility(
            R.string.time_constant_calculator_title,
            R.string.time_constant_calculator_description,
            Icons.Default.Timer,
            onOpenTimeConstantCalculator
        ),
        ElectronicUtility(
            R.string.parallel_series_resistor_calculator_title,
            R.string.parallel_series_resistor_calculator_description,
            Icons.Default.CompareArrows,
            onOpenParallelSeriesResistorCalculator
        ),
        ElectronicUtility(
            R.string.parallel_series_capacitor_calculator_title,
            R.string.parallel_series_capacitor_calculator_description,
            Icons.Default.CompareArrows,
            onOpenParallelSeriesCapacitorCalculator
        ),
        ElectronicUtility(
            R.string.ohms_law_calculator_title,
            R.string.ohms_law_calculator_description,
            Icons.Default.FlashOn,
            onOpenOhmsLawCalculator
        ),
        ElectronicUtility(
            R.string.voltage_divider_calculator_title,
            R.string.voltage_divider_calculator_description,
            Icons.Default.VerticalSplit,
            onOpenVoltageDividerCalculator
        )
    )

    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 150.dp),
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(utilities) { utility ->
            UtilityCard(
                title = stringResource(id = utility.titleRes),
                description = stringResource(id = utility.descriptionRes),
                icon = utility.icon,
                onClick = utility.onClick
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun UtilityCard(
    title: String,
    description: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .height(150.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(40.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.height(12.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 3
            )
        }
    }
}
