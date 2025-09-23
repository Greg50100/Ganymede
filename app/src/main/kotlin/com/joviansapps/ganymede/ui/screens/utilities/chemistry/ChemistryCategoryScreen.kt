package com.joviansapps.ganymede.ui.screens.utilities.chemistry

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Science
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.joviansapps.ganymede.R
import com.joviansapps.ganymede.ui.screens.utilities.common.CategoryGridScreen
import com.joviansapps.ganymede.ui.screens.utilities.common.CategoryItem

@Composable
fun ChemistryCategoryScreen(
    onOpenMolarMassCalculator: () -> Unit,
    modifier: Modifier = Modifier
) {
    val chemistryItems = listOf(
        CategoryItem(
            title = "Masse Molaire", // À AJOUTER DANS strings.xml
            description = "Calcule la masse molaire d'une formule chimique.", // À AJOUTER
            icon = Icons.Default.Science,
            onClick = onOpenMolarMassCalculator
        )
        // D'autres calculateurs de chimie peuvent être ajoutés ici
    )

    CategoryGridScreen(items = chemistryItems, modifier = modifier)
}
