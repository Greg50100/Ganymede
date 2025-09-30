package com.joviansapps.ganymede.ui.screens.ressources.electronics

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Straighten
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.joviansapps.ganymede.ui.screens.ressources.common.CategoryItem
import com.joviansapps.ganymede.ui.screens.ressources.common.ResourcesCategoryGridScreen

@Composable
fun ElectronicsResourcesCategoryScreen(
    onOpenElectronicSymbols: () -> Unit,
    onOpenComponentPinouts: () -> Unit,
    onOpenWireGauge: () -> Unit,
    onOpenBatteryTech: () -> Unit,
    onOpenConnectorsPinouts: () -> Unit,
    onOpenComponentPackages: () -> Unit,

    modifier: Modifier = Modifier
) {
    val items = listOf(
        CategoryItem(
            title = "Electronic Symbols",
            description = "IEC / ANSI symbols for components",
            icon = Icons.Filled.MenuBook,
            onClick = onOpenElectronicSymbols
        ),
        CategoryItem(
            title = "Component Pinouts",
            description = "Pinouts for common components",
            icon = Icons.Filled.Settings,
            onClick = onOpenComponentPinouts
        ),
        CategoryItem(
            title = "Wire Gauge",
            description = "AWG sizes and diameters",
            icon = Icons.Filled.Straighten,
            onClick = onOpenWireGauge
        ),
        CategoryItem(
            title = "Battery Tech",
            description = "Battery types and characteristics",
            icon = Icons.Filled.Settings,
            onClick = onOpenBatteryTech
        ),
        CategoryItem(
            title = "Connectors Pinouts",
            description = "Pinouts for common connectors",
            icon = Icons.Filled.Settings,
            onClick = onOpenConnectorsPinouts
        ),
        CategoryItem(
            title = "Component Packages",
            description = "Common IC package types",
            icon = Icons.Filled.Settings,
            onClick = onOpenComponentPackages
        )

    )

    ResourcesCategoryGridScreen(items = items, modifier = modifier)
}
