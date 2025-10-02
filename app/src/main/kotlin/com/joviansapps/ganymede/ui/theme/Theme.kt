// app/src/main/kotlin/com/joviansapps/ganymede/ui/theme/Theme.kt
package com.joviansapps.ganymede.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.joviansapps.ganymede.viewmodel.ThemeMode
import androidx.compose.ui.graphics.Color

private val LightOrangeScheme = lightColorScheme(
    primary = Color(0xFFFB8C00),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFFD6A3),
    onPrimaryContainer = Color(0xFF0D1B2A),
    secondary = Color(0xFF1565C0), // bleu complémentaire
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFDFF2FF),
    onSecondaryContainer = Color(0xFF07223A),
    tertiary = Color(0xFFFFD54F),
    onTertiary = Color(0xFF0D1B2A),
    tertiaryContainer = Color(0xFFFFF3C2),
    onTertiaryContainer = Color(0xFF102027),
    background = Color(0xFFFFFBF4),
    onBackground = Color(0xFF102027),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF102027),
    surfaceVariant = Color(0xFFE6F0F6),
    onSurfaceVariant = Color(0xFF37474F),
    outline = Color(0xFF607D8B),
    error = Color(0xFFB3261E),
    onError = Color.White,
    errorContainer = Color(0xFFFCDAD7),
    onErrorContainer = Color(0xFF410002)
)

private val DarkOrangeScheme = darkColorScheme(
    primary = Color(0xFFFFB04C),
    onPrimary = Color(0xFF0D1B2A),
    primaryContainer = Color(0xFF12324A),
    onPrimaryContainer = Color(0xFFFFDBBF),
    secondary = Color(0xFF90CAF9),
    onSecondary = Color(0xFF0B2433),
    secondaryContainer = Color(0xFF12324A),
    onSecondaryContainer = Color(0xFFFFDCC6),
    tertiary = Color(0xFFFFD54F),
    onTertiary = Color(0xFF0D1B2A),
    tertiaryContainer = Color(0xFF12324A),
    onTertiaryContainer = Color(0xFFFFE599),
    background = Color(0xFF071428),
    onBackground = Color(0xFFEDEFF1),
    surface = Color(0xFF08121A),
    onSurface = Color(0xFFE5E9EB),
    surfaceVariant = Color(0xFF102027),
    onSurfaceVariant = Color(0xFFE2C3AE),
    outline = Color(0xFF607D8B),
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6)
)

@Composable
fun AppTheme(
    themeMode: ThemeMode,
    dynamicColors: Boolean = false,
    content: @Composable () -> Unit
) {
    val systemDark = isSystemInDarkTheme()
    val useDark = when (themeMode) {
        ThemeMode.AUTO -> systemDark
        ThemeMode.DARK -> true
        ThemeMode.LIGHT -> false
    }

    val context = LocalContext.current
    val supportsDynamicColors = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S

    val colorScheme = when {
        dynamicColors && supportsDynamicColors && useDark -> dynamicDarkColorScheme(context)
        dynamicColors && supportsDynamicColors && !useDark -> dynamicLightColorScheme(context)
        useDark -> DarkOrangeScheme
        else -> LightOrangeScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography(),
        content = content
    )
}
