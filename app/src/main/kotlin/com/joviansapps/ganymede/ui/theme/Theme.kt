// app/src/main/kotlin/com/joviansapps/ganymede/ui/theme/Theme.kt
package com.joviansapps.ganymede.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import com.joviansapps.ganymede.viewmodel.ThemeMode

@Composable
fun AppTheme(
    themeMode: ThemeMode,
    primaryColorLong: Long,
    content: @Composable () -> Unit
) {
    val systemDark = isSystemInDarkTheme()
    val useDark = when (themeMode) {
        ThemeMode.AUTO  -> systemDark
        ThemeMode.DARK  -> true
        ThemeMode.LIGHT -> false
    }
    val seed = Color(primaryColorLong)
    // nuances étendues dérivées de la seed (pour appliquer surfaceVariant/outline/surfaceTint)
    val ext = buildExtendedNuances(seed, useDark)
    val scheme = remember(themeMode, primaryColorLong, systemDark) {
        // utiliser le ColorScheme généré depuis la seed
        seedColorScheme(seed, useDark)
    }
    // En mode sombre, remplacer le background/surface pour cohérence
    val finalScheme = if (useDark) scheme.copy(
        background = Color(0xFF202C37),
        onBackground = Color(0xFFFFFFFF),
        surface = Color(0xFF161F28),
        surfaceVariant = ext.surfaceVariant,
        outline = ext.outline,
        surfaceTint = ext.primary,
        onSurface = Color(0xFFFFFFFF)
    ) else scheme

    MaterialTheme(colorScheme = finalScheme, typography = Typography(), content = content)
}