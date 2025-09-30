package com.joviansapps.ganymede.ui.screens.ressources.mathematics

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.joviansapps.ganymede.R
import com.joviansapps.ganymede.ui.screens.ressources.common.CategoryItem
import com.joviansapps.ganymede.ui.screens.ressources.common.ResourcesCategoryGridScreen
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook

@Composable
fun MathematicsCategoryScreen(
    modifier: Modifier = Modifier,
    onOpenDerivativesIntegrals: () -> Unit = {},
    onOpenLaplaceTransforms: () -> Unit = {},
    onOpenTrigIdentities: () -> Unit = {}
) {
    val items = listOf(
        CategoryItem(
            title = stringResource(id = R.string.derivatives_integrals),
            description = stringResource(id = R.string.mathematics_category_description),
            icon = Icons.AutoMirrored.Filled.MenuBook,
            onClick = onOpenDerivativesIntegrals
        ),
        CategoryItem(
            title = stringResource(id = R.string.laplace_transforms),
            description = stringResource(id = R.string.mathematics_category_description),
            icon = Icons.AutoMirrored.Filled.MenuBook,
            onClick = onOpenLaplaceTransforms
        ),
        CategoryItem(
            title = stringResource(id = R.string.trig_identities),
            description = stringResource(id = R.string.mathematics_category_description),
            icon = Icons.AutoMirrored.Filled.MenuBook,
            onClick = onOpenTrigIdentities
        )
    )

    ResourcesCategoryGridScreen(items = items, modifier = modifier)
}
