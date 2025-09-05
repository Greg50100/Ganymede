package com.joviansapps.ganymede.navigation

import android.content.Context
import android.content.res.Configuration
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import java.util.*

/**
 * Fournit à la composition un nouveau LocalContext configuré avec la locale `code`.
 */
@Composable
fun LanguageProvider(code: String, content: @Composable () -> Unit) {
    val ctx = LocalContext.current

    // 1) crée l'objet Locale
    val locale = remember(code) { Locale(code) }

    // 2) crée une Configuration basée sur l'existante + nouvelle locale
    val config = remember(locale) {
        Configuration(ctx.resources.configuration).apply {
            setLocale(locale)
        }
    }

    // 3) crée un contexte dérivé de celui-ci avec la nouvelle config
    val localizedCtx = remember(config) {
        ctx.createConfigurationContext(config)
    }

    // 4) injecte ce contexte dans la composition
    CompositionLocalProvider(
        LocalContext provides localizedCtx
    ) {
        content()
    }
}