package com.joviansapps.ganymede.ui.screens.utilities.health

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MonitorHeart
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.joviansapps.ganymede.R
import com.joviansapps.ganymede.ui.screens.utilities.common.CategoryGridScreen
import com.joviansapps.ganymede.ui.screens.utilities.common.CategoryItem

@Composable
fun HealthCategoryScreen(
    onOpenBmiCalculator: () -> Unit,
    onOpenBmrCalculator: () -> Unit,
    onOpenBodyFatCalculator: () -> Unit,
    modifier: Modifier = Modifier
) {
    // We define the list of items for this specific category
    val healthItems = listOf(
        CategoryItem(
            title = stringResource(id = R.string.bmi_calculator_title),
            description = stringResource(id = R.string.bmi_calculator_description),
            icon = Icons.Default.MonitorHeart,
            onClick = onOpenBmiCalculator
        ),
        CategoryItem(
            title = stringResource(id = R.string.bmr_calculator_title),
            description = stringResource(id = R.string.bmr_calculator_description),
            icon = Icons.Default.MonitorHeart,
            onClick = onOpenBmrCalculator
        ),
        CategoryItem(
            title = stringResource(id = R.string.body_fat_calculator_title),
            description = stringResource(id = R.string.body_fat_calculator_description),
            icon = Icons.Default.MonitorHeart,
            onClick = onOpenBodyFatCalculator
        )
        // Add other health calculators here in the future
    )

    // We use the generic CategoryGridScreen to display them
    CategoryGridScreen(items = healthItems, modifier = modifier)
}
