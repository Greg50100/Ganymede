package com.joviansapps.ganymede.ui.theme

import androidx.compose.ui.graphics.Color
import kotlin.math.max
import kotlin.math.min

/**
 * Palette simplifiée générée à partir d'une couleur seed.
 */
data class PrimaryNuances(
    val seed: Color,
    val primary: Color,
    val onPrimary: Color,
    val primaryContainer: Color,
    val onPrimaryContainer: Color
)

data class ExtendedNuances(
    val seed: Color,
    val primary: Color,
    val onPrimary: Color,
    val primaryContainer: Color,
    val onPrimaryContainer: Color,
    val secondary: Color,
    val onSecondary: Color,
    val secondaryContainer: Color,
    val onSecondaryContainer: Color,
    val tertiary: Color,
    val onTertiary: Color,
    val tertiaryContainer: Color,
    val onTertiaryContainer: Color,
    // Ajouts pour cohérence chromatique complète
    val background: Color,
    val onBackground: Color,
    val surface: Color,
    val onSurface: Color,
    val surfaceVariant: Color,
    val onSurfaceVariant: Color,
    val outline: Color,
    val outlineVariant: Color,
    val inverseSurface: Color,
    val inverseOnSurface: Color,
    val inversePrimary: Color,
    val scrim: Color
)

private fun Color.toHsl(): FloatArray {
    val r = (this.red)
    val g = (this.green)
    val b = (this.blue)
    val maxC = max(r, max(g, b))
    val minC = min(r, min(g, b))
    val delta = maxC - minC
    var h = 0f
    val l = (maxC + minC) / 2f
    val s = if (delta == 0f) 0f else delta / (1f - kotlin.math.abs(2f * l - 1f))
    if (delta != 0f) {
        h = when (maxC) {
            r -> 60f * (((g - b) / delta) % 6f)
            g -> 60f * (((b - r) / delta) + 2f)
            else -> 60f * (((r - g) / delta) + 4f)
        }
    }
    if (h < 0f) h += 360f
    return floatArrayOf(h, s, l)
}

private fun hslToColor(h: Float, s: Float, l: Float): Color {
    val c = (1f - kotlin.math.abs(2f * l - 1f)) * s
    val x = c * (1f - kotlin.math.abs((h / 60f) % 2f - 1f))
    val m = l - c / 2f
    val (r1, g1, b1) = when {
        h < 60f -> floatArrayOf(c, x, 0f)
        h < 120f -> floatArrayOf(x, c, 0f)
        h < 180f -> floatArrayOf(0f, c, x)
        h < 240f -> floatArrayOf(0f, x, c)
        h < 300f -> floatArrayOf(x, 0f, c)
        else -> floatArrayOf(c, 0f, x)
    }
    return Color(r1 + m, g1 + m, b1 + m)
}

private fun Color.lighten(delta: Float): Color {
    val (h, s, l) = this.toHsl()
    val newL = (l + delta).coerceIn(0f, 1f)
    return hslToColor(h, s, newL)
}

private fun Color.darken(delta: Float): Color {
    val (h, s, l) = this.toHsl()
    val newL = (l - delta).coerceIn(0f, 1f)
    return hslToColor(h, s, newL)
}

private fun contrastOn(color: Color): Color {
    // Luminance simple approx
    val y = 0.2126f * color.red + 0.7152f * color.green + 0.0722f * color.blue
    return if (y > 0.6f) Color.Black else Color.White
}

private fun shiftHue(color: Color, delta: Float): Color {
    val (h, s, l) = color.toHsl()
    val newH = (h + delta + 360f) % 360f
    return hslToColor(newH, s, l)
}

fun buildNuances(seed: Color, dark: Boolean): PrimaryNuances {
    val primary = if (dark) seed.lighten(0.15f) else seed
    val container = if (dark) seed.darken(0.3f) else seed.lighten(0.35f)
    return PrimaryNuances(
        seed = seed,
        primary = primary,
        onPrimary = contrastOn(primary),
        primaryContainer = container,
        onPrimaryContainer = contrastOn(container)
    )
}

fun buildExtendedNuances(seed: Color, dark: Boolean): ExtendedNuances {
    // Primary proche seed (éclairci en dark pour contraste)
    val primary = if (dark) seed.lighten(0f) else seed
    val primaryContainer = if (dark) seed.darken(0.35f) else seed.lighten(0.38f)

    // Harmonisation: secondary & tertiary restent dans la même famille chromatique.
    val (h, s, l) = seed.toHsl()

    // Secondary : très proche de la seed, désaturée pour rôle de soutien.
    val secondaryHue = (h + 8f) % 360f
    val secondarySat = (s * 0.70f).coerceIn(0f, 1f)
    val secondaryLum = if (dark) (l * 0.85f).coerceIn(0f,1f) else (l * 1.05f).coerceIn(0f,1f)
    val secondary = hslToColor(secondaryHue, secondarySat, secondaryLum)
    val secondaryContainer = if (dark) secondary.darken(0.32f) else secondary.lighten(0.42f)

    // Tertiary : légère variation inverse de hue, saturation plus réduite encore, luminosité adaptée pour contraste secondaire.
    val tertiaryHue = (h - 8f + 360f) % 360f
    val tertiarySat = (s * 0.55f).coerceIn(0f,1f)
    val tertiaryLum = if (dark) (l * 0.90f).coerceIn(0f,1f) else (l * 1.08f).coerceIn(0f,1f)
    val tertiary = hslToColor(tertiaryHue, tertiarySat, tertiaryLum)
    val tertiaryContainer = if (dark) tertiary.darken(0.30f) else tertiary.lighten(0.40f)

    // Neutres dérivés de la seed (désaturation forte pour conserver une teinte subtile)
    val neutralLightL = 0.98f
    val neutralDarkL = 0.12f
    val surface = hslToColor(h, (s*0.05f).coerceIn(0f,0.08f), if (dark) neutralDarkL else neutralLightL)
    val background = if (dark) surface.darken(0.015f) else surface.lighten(0.01f)

    // Variant un peu plus saturée & plus contrastée
    val surfaceVariant = hslToColor(h, (s*0.12f).coerceIn(0f,0.16f), if (dark) 0.30f else 0.90f)

    val outline = hslToColor(h, (s*0.25f).coerceIn(0f,0.30f), if (dark) 0.55f else 0.45f)
    val outlineVariant = hslToColor(h, (s*0.18f).coerceIn(0f,0.24f), if (dark) 0.40f else 0.70f)

    val onSurface = contrastOn(surface)
    val onBackground = contrastOn(background)
    val onSurfaceVariant = contrastOn(surfaceVariant)

    val inverseSurface = if (dark) hslToColor(h, (s*0.06f).coerceIn(0f,0.10f), 0.97f) else hslToColor(h, (s*0.10f).coerceIn(0f,0.14f), 0.15f)
    val inverseOnSurface = contrastOn(inverseSurface)
    val inversePrimary = if (dark) primary.lighten(0.40f) else primary.darken(0.40f)

    val scrim = Color(0x66000000)

    return ExtendedNuances(
        seed = seed,
        primary = primary,
        onPrimary = contrastOn(primary),
        primaryContainer = primaryContainer,
        onPrimaryContainer = contrastOn(primaryContainer),
        secondary = secondary,
        onSecondary = contrastOn(secondary),
        secondaryContainer = secondaryContainer,
        onSecondaryContainer = contrastOn(secondaryContainer),
        tertiary = tertiary,
        onTertiary = contrastOn(tertiary),
        tertiaryContainer = tertiaryContainer,
        onTertiaryContainer = contrastOn(tertiaryContainer),
        background = background,
        onBackground = onBackground,
        surface = surface,
        onSurface = onSurface,
        surfaceVariant = surfaceVariant,
        onSurfaceVariant = onSurfaceVariant,
        outline = outline,
        outlineVariant = outlineVariant,
        inverseSurface = inverseSurface,
        inverseOnSurface = inverseOnSurface,
        inversePrimary = inversePrimary,
        scrim = scrim
    )
}

// Couleurs seed disponibles dans Settings
val SeedPurple = Color(0xFF6750A4)
val SeedGreen  = Color(0xFF006E1C)
val SeedRed    = Color(0xFFB3261E)
val SeedBlue   = Color(0xFF1F6FEB)
val SeedOrange = Color(0xFFFB8C00)

val AllSeeds = listOf(SeedPurple, SeedGreen, SeedRed, SeedBlue, SeedOrange)
