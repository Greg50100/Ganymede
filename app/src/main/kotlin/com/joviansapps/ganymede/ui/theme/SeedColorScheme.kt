package com.joviansapps.ganymede.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

// Génération d’un ColorScheme basé seed (limitée aux rôles supportés par copy() de la version actuelle).
fun seedColorScheme(seed: Color, dark: Boolean): androidx.compose.material3.ColorScheme {
    val ext = buildExtendedNuances(seed, dark)
    val base = if (dark) darkColorScheme() else lightColorScheme()
    return base.copy(
        primary = ext.primary,
        onPrimary = ext.onPrimary,
        primaryContainer = ext.primaryContainer,
        onPrimaryContainer = ext.onPrimaryContainer,
        secondary = ext.secondary,
        onSecondary = ext.onSecondary,
        secondaryContainer = ext.secondaryContainer,
        onSecondaryContainer = ext.onSecondaryContainer,
        tertiary = ext.tertiary,
        onTertiary = ext.onTertiary,
        tertiaryContainer = ext.tertiaryContainer,
        onTertiaryContainer = ext.onTertiaryContainer
        // Les rôles background/surface/etc. non disponibles dans copy() pour cette version ne sont pas surchargés.
    )
}
