package com.joviansapps.ganymede.ui.screens.ressources.chemistryphysics

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Science
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.joviansapps.ganymede.R
import com.joviansapps.ganymede.navigation.Dest
import com.joviansapps.ganymede.ui.screens.ressources.common.ResourcesCategoryGridScreen
import com.joviansapps.ganymede.ui.screens.ressources.common.CategoryItem

@Composable
fun ChemistryPhysicsCategoryScreen(
    onOpenPeriodicTable: () -> Unit,
    onOpenElectromagneticSpectrum: () -> Unit,
    onOpenMaterialProperties: () -> Unit,
    onOpenRedoxPotential: () -> Unit,
    modifier: Modifier
) {
    val chemistryPhysicsItems = listOf(
        CategoryItem(
            title = "Periode Table", //TODO À AJOUTER DANS strings.xml
            description = "Displays the periodic table of elements", //TODO À AJOUTER
            icon = Icons.Default.Science,
            onClick = onOpenPeriodicTable
        ),
        CategoryItem(
            title = "Electromagnetic Spectrum", //TODO À AJOUTER DANS strings.xml
            description = "Shows the electromagnetic spectrum chart", //TODO À AJOUTER
            icon = Icons.Default.Science,
            onClick = onOpenElectromagneticSpectrum
        ),
        CategoryItem(
            title = "Material Properties", //TODO À AJOUTER DANS strings.xml
            description = "Common material properties reference", //TODO À AJOUTER
            icon = Icons.Default.Science,
            onClick = onOpenMaterialProperties
        ),
        CategoryItem(
            title = "Redox Potential", //TODO À AJOUTER DANS strings.xml
            description = "Standard reduction potentials chart", //TODO À AJOUTER
            icon = Icons.Default.Science,
            onClick = onOpenRedoxPotential
        )
        // D'autres ressources de chimie/physique peuvent être ajoutées ici
    )

    ResourcesCategoryGridScreen(items = chemistryPhysicsItems, modifier = modifier)
}