// app/src/main/kotlin/com/joviansapps/ganymede/ui/theme/Theme.kt
package com.joviansapps.ganymede.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.joviansapps.ganymede.viewmodel.ThemeMode

@Composable
fun AppTheme(
    themeMode: ThemeMode,
    primaryColorLong: Long,
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    // choix clair/sombre/auto
    val systemDark = isSystemInDarkTheme()
    val useDark = when (themeMode) {
        ThemeMode.AUTO  -> systemDark
        ThemeMode.DARK  -> true
        ThemeMode.LIGHT -> false
    }

    // récupération du schéma dynamique ou statique
    val baseScheme = if (dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val ctx = LocalContext.current
        if (useDark) dynamicDarkColorScheme(ctx) else dynamicLightColorScheme(ctx)
    } else {
        if (useDark) darkColorScheme() else lightColorScheme()
    }

    // surcharge de primary
    val customScheme = baseScheme.copy(primary = Color(primaryColorLong))

    MaterialTheme(
        colorScheme   = customScheme,
        typography     = Typography(),
        content        = content
    )
}