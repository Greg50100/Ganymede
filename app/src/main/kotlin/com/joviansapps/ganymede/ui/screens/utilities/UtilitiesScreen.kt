package com.joviansapps.ganymede.ui.screens.utilities

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.MonitorHeart
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

// Data model for a utility, now with an icon for better visual representation
private data class Utility(
    val titleRes: Int,
    val descriptionRes: Int,
    val icon: ImageVector,
    val onClick: () -> Unit
)

@Composable
@Preview
fun UtilitiesScreen(
    onOpenElectronics: () -> Unit = {},
    onOpenHealth: () -> Unit = {}
) {
    // List of available utility categories
    val utilities = listOf(
        Utility(
            titleRes = R.string.electronics_category_title,
            descriptionRes = R.string.electronics_category_description,
            icon = Icons.Default.Memory,
            onClick = onOpenElectronics
        ),
        Utility(
            titleRes = R.string.health_category_title,
            descriptionRes = R.string.health_category_description,
            icon = Icons.Default.MonitorHeart,
            onClick = onOpenHealth
        )
        // New utilities can be added here easily
    )

    // A LazyVerticalGrid provides a more modern and scalable layout than a simple column
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
                .height(150.dp), // Fixed height for uniform cards in the grid
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
                maxLines = 2
            )
        }
    }
}
