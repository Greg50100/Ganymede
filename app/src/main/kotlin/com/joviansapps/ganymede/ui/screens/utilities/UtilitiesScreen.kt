package com.joviansapps.ganymede.ui.screens.utilities

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.MonitorHeart
import androidx.compose.material.icons.filled.Speed
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.joviansapps.ganymede.R
import com.joviansapps.ganymede.ui.screens.utilities.common.CategoryGridScreen
import com.joviansapps.ganymede.ui.screens.utilities.common.CategoryItem

@Composable
fun UtilitiesScreen(
    modifier: Modifier = Modifier,
    onOpenElectronics: () -> Unit = {},
    onOpenHealth: () -> Unit = {},
    onOpenMath: () -> Unit = {},
    onOpenPhysics: () -> Unit = {}
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
        )
        // New utility categories can be added here easily
    )

    CategoryGridScreen(items = utilityItems, modifier = modifier)
}
