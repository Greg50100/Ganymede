package com.joviansapps.ganymede.ui.screens.ressources.mechanics

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Science
import com.joviansapps.ganymede.R
import com.joviansapps.ganymede.ui.screens.ressources.common.ResourcesCategoryGridScreen
import com.joviansapps.ganymede.ui.screens.ressources.common.CategoryItem

@Composable
fun MechanicsCategoryScreen(
    onOpenBearingDesignation: () -> Unit,
    onOpenTappingDrill: () -> Unit,
    modifier: Modifier = Modifier
) {
    val items = listOf(
        CategoryItem(
            title = stringResource(id = R.string.bearing_designations),
            description = stringResource(id = R.string.bearing_designations),
            icon = Icons.AutoMirrored.Filled.MenuBook,
            onClick = onOpenBearingDesignation
        ),
        CategoryItem(
            title = stringResource(id = R.string.tapping_drill_chart),
            description = stringResource(id = R.string.tapping_drill_chart),
            icon = Icons.Default.Science,
            onClick = onOpenTappingDrill
        )
    )

    ResourcesCategoryGridScreen(items = items, modifier = modifier)
}
