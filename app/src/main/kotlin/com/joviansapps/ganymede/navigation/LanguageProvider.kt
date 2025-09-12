package com.joviansapps.ganymede.navigation

import android.content.ComponentCallbacks
import android.content.res.Configuration
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import java.util.*

/**
 * Provides a Composition with a LocalContext configured for the specified locale code.
 */
@Composable
fun LanguageProvider(code: String, content: @Composable () -> Unit) {
    val ctx = LocalContext.current

    // 1) Create the Locale object from the provided code
    val locale = remember(code) { Locale(code) }

    // 2) Read the current base configuration using LocalConfiguration and create a new Configuration with the new locale
    val baseConfig = LocalConfiguration.current
    val config = remember(locale, baseConfig) {
        Configuration(baseConfig).apply {
            setLocale(locale)
        }
    }

    // 3) Create a localized context from the modified configuration
    val localizedCtx = remember(config) {
        ctx.createConfigurationContext(config)
    }

    // 4) Listen for configuration changes to allow recomposition if the system configuration changes
    DisposableEffect(ctx) {
        val callback = object : ComponentCallbacks {
            override fun onConfigurationChanged(newConfig: Configuration) {
                // No-op here; keeping the callback ensures system changes are observable.
                // If you need to force a recomposition, you can update a state observed by the composable.
            }

            override fun onLowMemory() {}
        }
        ctx.registerComponentCallbacks(callback)
        onDispose { ctx.unregisterComponentCallbacks(callback) }
    }

    // TODO: Gérer la direction RTL si une langue RTL est ajoutée (CompositionLocalProvider(LocalLayoutDirection, ...)).
    // TODO: Envisager un mécanisme de recomposition forcée si la config système change (orientation, locales système),
    //       par exemple via un state clé plutôt qu'un ComponentCallbacks no-op.

    // 5) Provide the localized context to the composition
    CompositionLocalProvider(
        LocalContext provides localizedCtx
    ) {
        content()
    }
}