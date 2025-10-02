package com.joviansapps.ganymede.ui.screens.utilities.physics

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.Waves
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.joviansapps.ganymede.R
import com.joviansapps.ganymede.ui.components.UtilitiesCategoryGridScreen
import com.joviansapps.ganymede.ui.components.CategoryItem

@Composable
fun PhysicsCategoryScreen(
    onOpenFreeFallCalculator: () -> Unit,
    onOpenNewtonsSecondLawCalculator: () -> Unit,
    onOpenProjectileMotionCalculator: () -> Unit,
    onOpenIdealGasLawCalculator: () -> Unit,
    onOpenBernoulliCalculator: () -> Unit, // AJOUTÉ
    modifier: Modifier = Modifier
) {
    // Define the list of items for the physics category
    val physicsItems = listOf(
        CategoryItem(
            title = stringResource(id = R.string.free_fall_calculator_title),
            description = stringResource(id = R.string.free_fall_calculator_description),
            icon = Icons.Default.Speed, // Using a more appropriate icon
            onClick = onOpenFreeFallCalculator
        ),
        CategoryItem(
            title = stringResource(id = R.string.newtons_second_law_title),
            description = stringResource(id = R.string.newtons_second_law_description),
            icon = Icons.Default.Speed,
            onClick = onOpenNewtonsSecondLawCalculator
        ),
        CategoryItem(
            title = stringResource(id = R.string.projectile_motion_calculator_title),
            description = stringResource(id = R.string.projectile_motion_calculator_description),
            icon = Icons.Default.Speed,
            onClick = onOpenProjectileMotionCalculator
        ),
        CategoryItem(
            title = stringResource(id = R.string.ideal_gas_law_calculator_title),
            description = stringResource(id = R.string.ideal_gas_law_calculator_description),
            icon = Icons.Default.Thermostat,
            onClick = onOpenIdealGasLawCalculator
        ),
        // AJOUTÉ
        CategoryItem(
            title = stringResource(id = R.string.bernoulli_calculator_title),
            description = stringResource(id = R.string.bernoulli_calculator_description),
            icon = Icons.Default.Waves,
            onClick = onOpenBernoulliCalculator
        )
        // Add other physics calculators here in the future
    )

    // Use the generic CategoryGridScreen to display them
    UtilitiesCategoryGridScreen(items = physicsItems, modifier = modifier)
}
