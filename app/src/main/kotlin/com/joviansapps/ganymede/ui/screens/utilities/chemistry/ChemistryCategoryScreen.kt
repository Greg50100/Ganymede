package com.joviansapps.ganymede.ui.screens.utilities.chemistry

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Science
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.joviansapps.ganymede.R
import com.joviansapps.ganymede.ui.components.UtilitiesCategoryGridScreen
import com.joviansapps.ganymede.ui.components.CategoryItem

@Composable
fun ChemistryCategoryScreen(
    onOpenMolarMassCalculator: () -> Unit,
    modifier: Modifier = Modifier
) {
    val chemistryItems = listOf(
        CategoryItem(
            title = stringResource(id = R.string.molar_mass_calculator_title),
            description = stringResource(id = R.string.molar_mass_calculator_description),
            icon = Icons.Default.Science,
            onClick = onOpenMolarMassCalculator
        )
        // D'autres calculateurs de chimie peuvent être ajoutés ici
    )

    UtilitiesCategoryGridScreen(items = chemistryItems, modifier = modifier)
}
