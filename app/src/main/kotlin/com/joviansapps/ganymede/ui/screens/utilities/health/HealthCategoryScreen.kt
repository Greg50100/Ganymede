package com.joviansapps.ganymede.ui.screens.utilities.health

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.joviansapps.ganymede.R
import com.joviansapps.ganymede.navigation.Dest
import com.joviansapps.ganymede.ui.components.UtilitiesCategoryGridScreen
import com.joviansapps.ganymede.ui.components.CategoryItem

@Composable
fun HealthCategoryScreen(
    onOpenBmiCalculator: () -> Unit,
    onOpenBmrCalculator: () -> Unit,
    modifier: Modifier = Modifier
) {
    // We define the list of items for this specific category
    val healthItems = listOf(
        CategoryItem(
            title = stringResource(id = R.string.bmi_calculator_title),
            description = stringResource(id = R.string.bmi_calculator_description),
            icon = Icons.Default.Favorite,
            onClick = onOpenBmiCalculator
        ),
        CategoryItem(
            title = stringResource(id = R.string.bmr_calculator_title),
            description = stringResource(id = R.string.bmr_calculator_description),
            icon = Icons.Default.LocalFireDepartment,
            onClick = onOpenBmrCalculator
        )
    )

    // We use the generic CategoryGridScreen to display them
    UtilitiesCategoryGridScreen(items = healthItems, modifier = modifier)
}
