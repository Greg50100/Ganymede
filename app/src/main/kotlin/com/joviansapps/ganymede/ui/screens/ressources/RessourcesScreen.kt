package com.joviansapps.ganymede.ui.screens.ressources

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Spellcheck
import androidx.compose.material.icons.filled.Functions
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.joviansapps.ganymede.R
import com.joviansapps.ganymede.ui.screens.utilities.common.CategoryGridScreen
import com.joviansapps.ganymede.ui.screens.utilities.common.CategoryItem
import androidx.compose.material3.Surface
import androidx.compose.material3.MaterialTheme
import androidx.compose.foundation.layout.fillMaxSize

@Composable
fun RessourcesScreen(
    modifier: Modifier = Modifier,
    onOpenSIPrefixes: () -> Unit = {},
    onOpenSIConstants: () -> Unit = {},
    onOpenSIUnits: () -> Unit = {},
    onOpenSIDerivedUnits: () -> Unit = {},
    onOpenASCIITables: () -> Unit = {},
    onOpenGreekAlphabet: () -> Unit = {},
    onOpenLogicGates: () -> Unit = {},
    onOpenPeriodicTable: () -> Unit = {}
) {
    val resourceItems = listOf(
        CategoryItem(
            title = stringResource(id = R.string.si_prefixes_title),
            description = stringResource(id = R.string.si_prefixes_description),
            icon = Icons.Default.Info,
            onClick = onOpenSIPrefixes
        ),
        CategoryItem(
            title = stringResource(id = R.string.si_constants_title),
            description = stringResource(id = R.string.si_constants_description),
            icon = Icons.Default.Info,
            onClick = onOpenSIConstants
        ),
        CategoryItem(
            title = stringResource(id = R.string.si_units_title),
            description = stringResource(id = R.string.si_units_description),
            icon = Icons.Default.Info,
            onClick = onOpenSIUnits
        ),
        CategoryItem(
            title = stringResource(id = R.string.si_derived_units_title),
            description = stringResource(id = R.string.si_derived_units_description),
            icon = Icons.Default.Info,
            onClick = onOpenSIDerivedUnits
        ),
        CategoryItem(
            title = "Tables ASCII",
            description = "Référence des caractères",
            icon = Icons.Default.Info,
            onClick = onOpenASCIITables
        ),
        CategoryItem(
            title = "Alphabet Grec",
            description = "Lettres pour les sciences",
            icon = Icons.Default.Spellcheck,
            onClick = onOpenGreekAlphabet
        ),
        CategoryItem(
            title = "Portes Logiques",
            description = "Symboles et tables de vérité",
            icon = Icons.Default.Functions,
            onClick = onOpenLogicGates
        ),
        CategoryItem(
            title = "Tableau Périodique",
            description = "Informations sur les éléments",
            icon = Icons.Default.GridOn,
            onClick = onOpenPeriodicTable
        )
    )

    Surface(modifier = modifier.fillMaxSize(), color = MaterialTheme.colorScheme.surface) {
        CategoryGridScreen(items = resourceItems, modifier = Modifier)
    }
}
