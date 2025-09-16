package com.joviansapps.ganymede.navigation

import android.content.ComponentCallbacks
import android.content.res.Configuration
import android.os.Build
import android.view.View
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import java.util.*

/**
 * Provides a Composition with a LocalContext configured for the specified locale code.
 */
@Composable
fun LanguageProvider(code: String, content: @Composable () -> Unit) {
    val ctx = LocalContext.current

    val locale = remember(code) { Locale(code) }
    val baseConfig = LocalConfiguration.current
    val config = remember(locale, baseConfig) {
        Configuration(baseConfig).apply {
            setLocale(locale)
        }
    }

    // Gestion de la direction de la mise en page (RTL/LTR)
    val layoutDirection = remember(locale) {
        if (TextUtils.getLayoutDirectionFromLocale(locale) == View.LAYOUT_DIRECTION_RTL) {
            LayoutDirection.Rtl
        } else {
            LayoutDirection.Ltr
        }
    }

    val localizedCtx = remember(config) {
        ctx.createConfigurationContext(config)
    }

    DisposableEffect(ctx) {
        val callback = object : ComponentCallbacks {
            override fun onConfigurationChanged(newConfig: Configuration) {}
            override fun onLowMemory() {}
        }
        ctx.registerComponentCallbacks(callback)
        onDispose { ctx.unregisterComponentCallbacks(callback) }
    }

    CompositionLocalProvider(
        LocalContext provides localizedCtx,
        LocalLayoutDirection provides layoutDirection
    ) {
        content()
    }
}

// Helper pour la direction de la mise en page
object TextUtils {
    fun getLayoutDirectionFromLocale(locale: Locale?): Int {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            android.text.TextUtils.getLayoutDirectionFromLocale(locale)
        } else {
            // Fallback pour les anciennes versions, bien que votre minSdk soit probablement > 17
            // Vous pouvez ajuster cette logique si nécessaire.
            if (locale != null && isRtl(locale)) {
                View.LAYOUT_DIRECTION_RTL
            } else {
                View.LAYOUT_DIRECTION_LTR
            }
        }
    }

    private fun isRtl(locale: Locale): Boolean {
        val language = locale.language
        return language in listOf("ar", "fa", "he", "iw", "ur")
    }
}
