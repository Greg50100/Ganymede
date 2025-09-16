package com.joviansapps.ganymede.ui.screens.utilities.date

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.joviansapps.ganymede.R
import com.joviansapps.ganymede.ui.screens.utilities.common.CategoryGridScreen
import com.joviansapps.ganymede.ui.screens.utilities.common.CategoryItem

@Composable
fun DateCategoryScreen(
    onOpenDateCalculator: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dateItems = listOf(
        CategoryItem(
            title = stringResource(id = R.string.date_calculator_title),
            description = stringResource(id = R.string.date_calculator_description),
            icon = Icons.Default.DateRange,
            onClick = onOpenDateCalculator
        )
    )

    CategoryGridScreen(items = dateItems, modifier = modifier)
}
