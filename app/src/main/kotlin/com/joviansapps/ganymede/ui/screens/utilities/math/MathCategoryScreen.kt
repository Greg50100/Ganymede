package com.joviansapps.ganymede.ui.screens.utilities.math

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Functions
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.joviansapps.ganymede.R
import com.joviansapps.ganymede.ui.components.UtilitiesCategoryGridScreen
import com.joviansapps.ganymede.ui.components.CategoryItem

@Composable
fun MathCategoryScreen(
    onOpenQuadraticEquationSolver: () -> Unit,
    modifier: Modifier = Modifier,
    onOpenPercentageCalculator: () -> Unit,
    onOpenGCDandLCMCalculator: () -> Unit,
    onOpenMatrixCalculator: () -> Unit = {}
) {
    // Define the list of items for the math category
    val mathItems = listOf(
        CategoryItem(
            title = stringResource(id = R.string.quadratic_equation_solver_title),
            description = stringResource(id = R.string.quadratic_equation_solver_description),
            icon = Icons.Default.Functions, // Using a more appropriate icon
            onClick = onOpenQuadraticEquationSolver
        ),
        CategoryItem(
            title = stringResource(id = R.string.percentage_calculator_title),
            description = stringResource(id = R.string.percentage_calculator_description),
            icon = Icons.Default.Functions,
            onClick = onOpenPercentageCalculator
        ),
        CategoryItem(
            title = stringResource(id = R.string.gcd_lcm_calculator_title),
            description = stringResource(id = R.string.gcd_lcm_calculator_description),
            icon = Icons.Default.Functions,
            onClick = onOpenGCDandLCMCalculator
        ),
        CategoryItem(
            title = "Calculatrice de Matrices",
            description = "Effectuez des opérations sur les matrices : addition, soustraction, multiplication et déterminant",
            icon = Icons.Default.Functions,
            onClick = onOpenMatrixCalculator
        )
        // Add other math calculators here in the future
    )

    // Use the generic CategoryGridScreen to display them
    UtilitiesCategoryGridScreen(items = mathItems, modifier = modifier)
}
