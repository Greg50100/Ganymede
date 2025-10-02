package com.joviansapps.ganymede.ui.screens.utilities.date

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.joviansapps.ganymede.R
import com.joviansapps.ganymede.ui.components.UtilitiesCategoryGridScreen
import com.joviansapps.ganymede.ui.components.CategoryItem

@Composable
fun DateCategoryScreen(
    onOpenDateCalculator: () -> Unit,
    onOpenEasterCalculator: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dateItems = listOf(
        CategoryItem(
            title = stringResource(id = R.string.date_calculator_title),
            description = stringResource(id = R.string.date_calculator_description),
            icon = Icons.Default.CalendarToday,
            onClick = onOpenDateCalculator
        ),
        CategoryItem(
            title = stringResource(id = R.string.easter_calculator_title),
            description = stringResource(id = R.string.easter_calculator_description),
            icon = Icons.Default.CalendarToday,
            onClick = onOpenEasterCalculator
        )
    )

    UtilitiesCategoryGridScreen(items = dateItems, modifier = modifier)
}
