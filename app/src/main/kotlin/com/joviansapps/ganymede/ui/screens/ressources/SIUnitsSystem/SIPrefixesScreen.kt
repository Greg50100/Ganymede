/**
 * File: SIPrefixesScreen.kt
 * Project: Ganymede
 *
 * Author: Greg50100
 * Date: 28/09/2025
 *
 * Description:
 * Composable screen listing SI prefixes (e.g., kilo, milli) with their factors and symbols.
 * Serves as a quick reference for unit conversions and scientific notation.
 *
 * This file header follows the style used in `TransformerCalculatorScreen.kt` to keep
 * file-level documentation consistent across the project.
 *
 * Repository: https://github.com/Greg50100/Ganymede
 */

package com.joviansapps.ganymede.ui.screens.ressources.SIUnitsSystem

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private data class SIPrefix(
    val name: String,
    val symbol: String,
    val exponent: Int,
    val shortScaleName: String = "",
    val longScaleName: String = ""
)

private val SI_PREFIXES = listOf(
    SIPrefix("quetta", "Q", 30, shortScaleName = "nonillion", longScaleName = "quintillion"),
    SIPrefix("ronna", "R", 27, shortScaleName = "octillion", longScaleName = "quadrilliard"),
    SIPrefix("yotta", "Y", 24, shortScaleName = "septillion", longScaleName = "quadrillion"),
    SIPrefix("zetta", "Z", 21, shortScaleName = "sextillion", longScaleName = "trilliard"),
    SIPrefix("exa", "E", 18, shortScaleName = "quintillion", longScaleName = "trillion"),
    SIPrefix("peta", "P", 15, shortScaleName = "quadrillion", longScaleName = "billiard"),
    SIPrefix("tera", "T", 12, shortScaleName = "trillion", longScaleName = "billion"),
    SIPrefix("giga", "G", 9, shortScaleName = "billion", longScaleName = "milliard"),
    SIPrefix("mega", "M", 6, shortScaleName = "million", longScaleName = "million"),
    SIPrefix("kilo", "k", 3, shortScaleName = "thousand", longScaleName = "thousand"),
    SIPrefix("hecto", "h", 2),
    SIPrefix("deca", "da", 1),
    SIPrefix("deci", "d", -1, shortScaleName = "tenth", longScaleName = "tenth"),
    SIPrefix("centi", "c", -2, shortScaleName = "hundredth", longScaleName = "hundredth"),
    SIPrefix("milli", "m", -3, shortScaleName = "thousandth", longScaleName = "thousandth"),
    SIPrefix("micro", "µ", -6, shortScaleName = "millionth", longScaleName = "millionth"),
    SIPrefix("nano", "n", -9, shortScaleName = "billionth", longScaleName = "milliardth"),
    SIPrefix("pico", "p", -12, shortScaleName = "trillionth", longScaleName = "billionth"),
    SIPrefix("femto", "f", -15, shortScaleName = "quadrillionth", longScaleName = "billiardth"),
    SIPrefix("atto", "a", -18, shortScaleName = "quintillionth", longScaleName = "trillionth"),
    SIPrefix("zepto", "z", -21, shortScaleName = "sextillionth", longScaleName = "trilliardth"),
    SIPrefix("yocto", "y", -24, shortScaleName = "septillionth", longScaleName = "quadrillionth"),
    SIPrefix("ronto", "r", -27, shortScaleName = "octillionth", longScaleName = "quadrilliardth"),
    SIPrefix("quecto", "q", -30, shortScaleName = "nonillionth", longScaleName = "quintillionth")
)

@Composable
fun SIPrefixesScreen(modifier: Modifier = Modifier) {
    // Utiliser la même couleur de fond que les écrans utilities
    Surface(modifier = modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {

                Column(modifier = Modifier.padding(8.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .padding(vertical = 8.dp, horizontal = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Prefix", modifier = Modifier
                            .weight(0.7f),
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary,
                            textAlign = TextAlign.Center
                            )
                        
                        Text("Sy", modifier = Modifier
                            .weight(0.2f),
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary,
                            textAlign = TextAlign.Center
                        )
                        ExponentText("10^n", modifier = Modifier
                            .weight(0.8f),
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary,
                            textAlign = TextAlign.Center,
                        )
                        Text("Short scale", modifier = Modifier
                            .weight(1.2f),
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary,
                            textAlign = TextAlign.Center
                        )
                        Text("Long scale", modifier = Modifier
                            .weight(1.2f),
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary,
                            textAlign = TextAlign.Center
                        )
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

                    LazyColumn {
                        items(SI_PREFIXES) { pfx ->
                            PrefixRow(pfx)
                            HorizontalDivider()
                        }
                    }
                }


    }
}

@Composable
private fun PrefixRow(pfx: SIPrefix) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = pfx.name.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() },
            modifier = Modifier.weight(0.7f)
        )
        Text(
            text = pfx.symbol,
            modifier = Modifier.weight(0.2f),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyMedium
        )
        ExponentText(
            text = "10^${pfx.exponent}",
            modifier = Modifier.weight(0.8f),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            text = pfx.shortScaleName,
            modifier = Modifier.weight(1.2f),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            text = pfx.longScaleName,
            modifier = Modifier.weight(1.2f),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
fun ExponentText(
    text: String,
    modifier: Modifier = Modifier,
    textAlign: TextAlign = TextAlign.Center,
    style: TextStyle = MaterialTheme.typography.bodyMedium,
    fontWeight: FontWeight? = null,
    color: Color = Color.Unspecified
) {
    val parts = text.split('^')
    if (parts.size == 2) {
        Text(
            text = buildAnnotatedString {
                append(parts[0])
                withStyle(style = SpanStyle(baselineShift = BaselineShift.Superscript, fontSize = (style.fontSize.value * 0.75f).sp)) {
                    append(parts[1])
                }
            },
            modifier = modifier,
            textAlign = textAlign,
            style = style,
            fontWeight = fontWeight,
            color = color
        )
    } else {
        Text(
            text = text,
            modifier = modifier,
            textAlign = textAlign,
            style = style,
            fontWeight = fontWeight,
            color = color
        )
    }
}


@Preview(showBackground = true)
@Composable
private fun PreviewSIPrefixesScreen() {
    SIPrefixesScreen()
}
