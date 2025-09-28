package com.joviansapps.ganymede.ui.screens.utilities.electronics.info

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Build
import android.util.Log
import android.view.View
import android.webkit.ConsoleMessage
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.joviansapps.ganymede.R
import androidx.compose.ui.graphics.Color as ComposeColor

@SuppressLint("SetJavaScriptEnabled")
@Suppress("DEPRECATION")
@Composable
fun KaTeXView(
    latex: String,
    modifier: Modifier = Modifier,
    inline: Boolean = false,
    colorHex: String? = null,
    backgroundHex: String? = null
) {
    // Template HTML: essayer d'abord les scripts locaux dans assets/katex/, sinon injecter CDN
    val template = """
        <!doctype html>
        <html>
        <head>
          <meta name="viewport" content="width=device-width, initial-scale=1.0">
          <!-- Attempt to load local KaTeX files (relative to baseUrl=file:///android_asset/) -->
          <link rel="stylesheet" href="katex/katex.min.css">
          <script src="katex/katex.min.js"></script>
          <script src="katex/contrib/auto-render.min.js"></script>

          <style>
            /* background configurable; keep transparent by default */
            html, body { background: %BG%; margin: 0; padding: 0px 0px; color: %COLOR%; }
            body { font-size: %FONTSIZE%; line-height: 1; }

            /* Visual improvements: spacing, rounded bg for better contrast if background provided */
            .katex-wrapper { display:block; padding: 8px 10px; border-radius: 8px; box-shadow: rgba(0,0,0,0.06) 0px 1px 3px; }

            /* Make display formulas larger and give them breathing room */
            .katex-display { font-size: 28px !important; margin: 2px 0 !important; }
            /* Inline formulas slightly smaller */
            .katex { font-size: 22px !important; }

            /* force svg/text color for KaTeX rendering */
            .katex, .katex * { color: %COLOR% !important; }
            svg { fill: %COLOR% !important; stroke: %COLOR% !important; }

            /* Ensure elements don't collapse vertically */
            .katex-display, .katex { line-height: 1.2 !important; }
          </style>
        </head>
        <body><div class="katex-wrapper">%LATEX%</div></body>
        <script>
          (function(){
            console.log('KATEX_LOAD_STARTED');
            function tryRender() {
              try {
                if (window.renderMathInElement) {
                  console.log('KATEX_RENDER_AVAILABLE');
                  renderMathInElement(document.body, {delimiters: [{left:'\\[', right:'\\]'}, {left:'\\(', right:'\\)'}], throwOnError:false});
                  console.log('KATEX_TYPESAT_OK');
                  var h = document.body.scrollHeight || document.documentElement.scrollHeight || 0;
                  console.log('KATEX_HEIGHT:' + h);
                  return true;
                }
              } catch(e) { console.error('KATEX_ERROR_TRY', e); }
              return false;
            }

            // If local files didn't provide renderMathInElement, inject CDN after a short delay
            setTimeout(function() {
              if (!tryRender()) {
                console.log('KATEX_LOCAL_NOT_READY - injecting CDN');
                var s = document.createElement('script');
                s.src = 'https://cdn.jsdelivr.net/npm/katex@0.16.8/dist/katex.min.js';
                s.crossOrigin = 'anonymous';
                s.onload = function() {
                  console.log('KATEX_CDN_LOADED_SCRIPT');
                  var s2 = document.createElement('script');
                  s2.src = 'https://cdn.jsdelivr.net/npm/katex@0.16.8/dist/contrib/auto-render.min.js';
                  s2.onload = function(){
                    console.log('KATEX_CDN_AUTO_RENDER_LOADED');
                    setTimeout(function(){ tryRender(); }, 50);
                  };
                  s2.onerror = function(e){ console.error('KATEX_CDN_AUTO_RENDER_ERR', e); };
                  document.head.appendChild(s2);
                };
                s.onerror = function(e){ console.error('KATEX_CDN_ERR', e); };
                document.head.appendChild(s);

                // Also ensure CSS from CDN if local missing
                var link = document.createElement('link');
                link.rel = 'stylesheet';
                link.href = 'https://cdn.jsdelivr.net/npm/katex@0.16.8/dist/katex.min.css';
                document.head.appendChild(link);
              }
            }, 200);

            // Try initial render in case local scripts already loaded
            tryRender();
          })();
        </script>
        </html>
    """.trimIndent()

    fun escapeHtml(s: String): String = s
        .replace("&", "&amp;")
        .replace("<", "&lt;")
        .replace(">", "&gt;")

    val wrappedLatex = if (inline) "\\(${latex}\\)" else "\\[${latex}\\]"

    val colorCss = colorHex ?: "#000000"
    val bgCss = backgroundHex ?: "transparent"
    val fontSizeCss = if (inline) "16px" else "20px"

    var html = template.replace("%LATEX%", escapeHtml(wrappedLatex))
    html = html.replace("%COLOR%", colorCss)
    html = html.replace("%FONTSIZE%", fontSizeCss)
    html = html.replace("%BG%", bgCss)

    // baseUrl sur assets pour prioriser les fichiers locaux
    val baseUrl = "file:///android_asset/"

    val density = LocalDensity.current
    var heightDp by remember { mutableStateOf(350.dp) }

    AndroidView(
        factory = { ctx ->
            WebView(ctx).apply {
                try {
                    val bg = bgCss
                    if (bg != "transparent") setBackgroundColor(Color.parseColor(bg)) else setBackgroundColor(Color.TRANSPARENT)
                } catch (_: Throwable) {}

                try { setLayerType(View.LAYER_TYPE_SOFTWARE, null) } catch (_: Throwable) {}

                settings.javaScriptEnabled = true
                settings.domStorageEnabled = true
                try {
                    settings.allowFileAccessFromFileURLs = true
                    settings.allowUniversalAccessFromFileURLs = true
                } catch (e: Throwable) { Log.w("KaTeXView", "file access settings: ${e}") }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                }

                webViewClient = WebViewClient()
                webChromeClient = object : WebChromeClient() {
                    override fun onConsoleMessage(consoleMessage: ConsoleMessage?): Boolean {
                        if (consoleMessage != null) {
                            val msg = consoleMessage.message()
                            Log.d("KaTeXView", "WebView console: ${msg} (${consoleMessage.sourceId()} : ${consoleMessage.lineNumber()})")
                            if (msg.startsWith("KATEX_HEIGHT:")) {
                                val px = msg.substringAfter(':').toIntOrNull() ?: 0
                                val dpValue: Dp = with(density) { px.toDp() }
                                val finalDp = dpValue.coerceAtLeast(350.dp)
                                this@apply.post { heightDp = finalDp }
                            }
                        }
                        return true
                    }
                }
                loadDataWithBaseURL(baseUrl, html, "text/html", "utf-8", null)
            }
        },
        update = { web ->
            web.loadDataWithBaseURL(baseUrl, html, "text/html", "utf-8", null)
        },
        modifier = modifier
            .fillMaxWidth()
            .height(heightDp)
    )
}

@Composable
@Preview
fun TransformerCalculatorInfo() {
    // helper to convert Compose Color to hex like #RRGGBB
    fun composeColorToHex(c: ComposeColor): String {
        val r = (c.red * 255).toInt().coerceIn(0, 255)
        val g = (c.green * 255).toInt().coerceIn(0, 255)
        val b = (c.blue * 255).toInt().coerceIn(0, 255)
        return String.format("#%02X%02X%02X", r, g, b)
    }

    val onBgHex = composeColorToHex(MaterialTheme.colorScheme.onBackground)
    val surfaceHex = "transparent"

    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.transformator),
            contentDescription = "Transformer circuit diagram",
            contentScale = ContentScale.FillWidth,
            modifier = Modifier.fillMaxWidth(),
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary)
        )

        Image(
            painter = painterResource(id = R.drawable.transformer_formula),
            contentDescription = "Transformer icon",
            modifier = Modifier.height(200.dp),
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface)
        )

        Text(
            text = "Where:"
                + "\n• U₁, U₂ = Primary and secondary voltage"
                + "\n• I₁, I₂ = Primary and secondary intensity"
                + "\n• N₁, N₂ = Number of primary and secondary coil turns",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Start,
            modifier = Modifier.fillMaxWidth()
        )


    }
}