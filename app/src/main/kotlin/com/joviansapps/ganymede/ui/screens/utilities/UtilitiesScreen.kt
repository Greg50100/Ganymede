package com.joviansapps.ganymede.ui.screens.utilities

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.joviansapps.ganymede.R
import com.joviansapps.ganymede.ui.screens.utilities.common.UtilitiesCategoryGridScreen
import com.joviansapps.ganymede.ui.screens.utilities.common.CategoryItem

@Composable
fun UtilitiesScreen(
    modifier: Modifier = Modifier,
    onOpenElectronics: () -> Unit = {},
    onOpenHealth: () -> Unit = {},
    onOpenMath: () -> Unit = {},
    onOpenPhysics: () -> Unit = {},
    onOpenDate: () -> Unit = {},
    onOpenChemistry: () -> Unit = {} // AJOUTÉ
) {
    // List of available utility categories
    val utilityItems = listOf(
        CategoryItem(
            title = stringResource(id = R.string.electronics_category_title),
            description = stringResource(id = R.string.electronics_category_description),
            icon = Icons.Default.Memory,
            onClick = onOpenElectronics
        ),
        CategoryItem(
            title = stringResource(id = R.string.health_category_title),
            description = stringResource(id = R.string.health_category_description),
            icon = Icons.Default.MonitorHeart,
            onClick = onOpenHealth
        ),
        CategoryItem(
            title = stringResource(id = R.string.math_category_title),
            description = stringResource(id = R.string.math_category_description),
            icon = Icons.Default.Calculate,
            onClick = onOpenMath
        ),
        CategoryItem(
            title = stringResource(id = R.string.physics_category_title),
            description = stringResource(id = R.string.physics_category_description),
            icon = Icons.Default.Speed, // Changed icon for better distinction
            onClick = onOpenPhysics
        ),
        // AJOUTÉ : Nouvelle catégorie Chimie
        CategoryItem(
            title = "Chimie", // À AJOUTER DANS strings.xml
            description = "Outils pour les calculs chimiques.", // À AJOUTER
            icon = Icons.Default.Science,
            onClick = onOpenChemistry
        ),
        // Date utility category
        CategoryItem(
            title = stringResource(id = R.string.date_category_title),
            description = stringResource(id = R.string.date_category_description),
            icon = Icons.Default.DateRange,
            onClick = onOpenDate
        )
        // New utility categories can be added here easily
    )

    UtilitiesCategoryGridScreen(items = utilityItems, modifier = modifier)
}
