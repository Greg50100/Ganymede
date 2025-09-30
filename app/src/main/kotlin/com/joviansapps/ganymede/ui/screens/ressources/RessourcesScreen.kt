package com.joviansapps.ganymede.ui.screens.ressources

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Straighten
import androidx.compose.material.icons.filled.ElectricalServices
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Memory
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.joviansapps.ganymede.R
import com.joviansapps.ganymede.ui.screens.ressources.common.ResourcesCategoryGridScreen
import com.joviansapps.ganymede.ui.screens.ressources.common.CategoryItem
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Text

@Composable
fun RessourcesScreen(
    modifier: Modifier = Modifier,
    onOpenSIUnitsSystem: () -> Unit = {},
    onOpenElectronics: () -> Unit = {},
    onOpenGeneralReferences: () -> Unit = {},
    onOpenChemistryPhysics: () -> Unit = {},
    onOpenComputing: () -> Unit = {},
    onOpenMathematics: () -> Unit = {},
    onOpenMechanics: () -> Unit = {}
) {
    val ressourcesItems = listOf(
        CategoryItem(
            title = stringResource(id = R.string.si_units_system_category_title),
            description = stringResource(id = R.string.si_units_system_category_description),
            icon = Icons.Filled.Straighten,
            onClick = onOpenSIUnitsSystem
        ),
        CategoryItem(
            title = stringResource(id = R.string.electronics_category_title),
            description = stringResource(id = R.string.electronics_category_description),
            icon = Icons.Filled.ElectricalServices,
            onClick = onOpenElectronics
        ),
        CategoryItem(
            title = stringResource(id = R.string.general_references_category_title),
            description = stringResource(id = R.string.general_references_category_description),
            icon = Icons.Filled.MenuBook,
            onClick = onOpenGeneralReferences
        ),
        CategoryItem(
            title = stringResource(id = R.string.chemistry_physics_category_title),
            description = stringResource(id = R.string.chemistry_physics_category_description),
            icon = Icons.Filled.Science,
            onClick = onOpenChemistryPhysics
        ),
        CategoryItem(
            title = stringResource(id = R.string.computing_category_title),
            description = stringResource(id = R.string.computing_category_description),
            icon = Icons.Filled.Memory,
            onClick = onOpenComputing
        ),
        CategoryItem(
            title = stringResource(id = R.string.mathematics_resources),
            description = stringResource(id = R.string.math_category_description),
            icon = Icons.Filled.MenuBook,
            onClick = onOpenMathematics
        ),
        CategoryItem(
            title = stringResource(id = R.string.mechanics_resources),
            description = "Classical mechanics references and calculators",
            icon = Icons.Filled.Science,
            onClick = onOpenMechanics
        )
    )

    ResourcesCategoryGridScreen(items = ressourcesItems, modifier = modifier)
}