package com.joviansapps.ganymede.ui.screens.ressources.SIUnitsSystem

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Science
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.joviansapps.ganymede.R
import com.joviansapps.ganymede.ui.screens.ressources.common.ResourcesCategoryGridScreen
import com.joviansapps.ganymede.ui.screens.ressources.common.CategoryItem

@Composable
fun SIUnitsSystemCategoryScreen(
    onOpenSIPrefixes: () -> Unit,
    onOpenSIConstants: () -> Unit,
    onOpenSIUnits: () -> Unit,
    onOpenSIDerivedUnits: () -> Unit,
    modifier: Modifier = Modifier
) {
    val siUnitsSystemItems = listOf(
        CategoryItem(
            title = "SI Prefixes", //TODO To be added in strings.xml
            description = "List of SI prefixes and their meanings.", //TODO To be added
            icon = Icons.Default.Science,
            onClick = onOpenSIPrefixes
        ),
        CategoryItem(
            title = "SI Constants", //TODO To be added in strings.xml
            description = "List of fundamental SI constants.", //TODO To be added
            icon = Icons.Default.Science,
            onClick = onOpenSIConstants
        ),
        CategoryItem(
            title = "SI Units", //TODO To be added in strings.xml
            description = "List of base SI units.", //TODO To be added
            icon = Icons.Default.Science,
            onClick = onOpenSIUnits
        ),
        CategoryItem(
            title = "SI Derived Units", //TODO To be added in strings.xml
            description = "List of derived SI units.", //TODO To be added
            icon = Icons.Default.Science,
            onClick = onOpenSIDerivedUnits
        )
        // More SI Units System resources can be added here

    )
    ResourcesCategoryGridScreen(items = siUnitsSystemItems, modifier = modifier)
}