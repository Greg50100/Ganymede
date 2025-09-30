package com.joviansapps.ganymede.ui.screens.ressources.SIUnitsSystem

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.joviansapps.ganymede.R
import com.joviansapps.ganymede.ui.theme.AppTheme
import com.joviansapps.ganymede.viewmodel.ThemeMode

data class SIConstant(
    val name: String,
    @param:DrawableRes val symbolDrawable: Int,
    val value: String,
    val unit: String
)

// The 7 defining constants of the SI, using drawable resources for symbols
val siDefiningConstants = listOf(
    SIConstant("Fréquence de la transition hyperfine du césium", R.drawable.ic_symbol_delta_nu_cs, "9 192 631 770", "Hz"),
    SIConstant("Vitesse de la lumière dans le vide", R.drawable.ic_symbol_c, "299 792 458", "m s^-1"),
    SIConstant("Constante de Planck", R.drawable.ic_symbol_h, "6.626 070 15 x 10^-34", "J s"),
    SIConstant("Charge élémentaire", R.drawable.ic_symbol_e, "1.602 176 634 x 10^-19", "C"),
    SIConstant("Constante de Boltzmann", R.drawable.ic_symbol_k_b, "1.380 649 x 10^-23", "J K^-1"),
    SIConstant("Constante d'Avogadro", R.drawable.ic_symbol_n_a, "6.022 140 76 x 10^23", "mol^-1"),
    SIConstant("Efficacité lumineuse d'un rayonnement monochromatique défini", R.drawable.ic_symbol_k_cd, "683", "lm W^-1")
)

@Composable
fun SIConstantsScreen(modifier: Modifier = Modifier) {
    // Utiliser la couleur de surface du thème pour correspondre aux utilities
    Surface(modifier = modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        LazyColumn(modifier = Modifier.padding(vertical = 8.dp)) {
            items(siDefiningConstants) { constant ->
                ConstantItem(constant)
                HorizontalDivider(modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 16.dp)

                )
            }
        }
    }
}

@Composable
private fun ConstantItem(constant: SIConstant) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Text(
            text = constant.name,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp),
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Image(
                painter = painterResource(id = constant.symbolDrawable),
                contentDescription = constant.name, // Accessibility
                modifier = Modifier
                    .height(32.dp) // Adjust height as needed
                    .align(Alignment.CenterVertically),
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary)
            )
            Spacer(modifier = Modifier.width(16.dp))
            // Separate value and unit to avoid exponent parsing in value affecting unit
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                FormattedText(
                    text = constant.value,
                    textAlign = TextAlign.End,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.width(8.dp))
                FormattedText(
                    text = constant.unit,
                    textAlign = TextAlign.End,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun FormattedText(
    text: String,
    modifier: Modifier = Modifier,
    textAlign: TextAlign? = null,
    style: TextStyle = MaterialTheme.typography.bodyLarge,
    color: Color = Color.Unspecified
) {
    val annotatedString = buildAnnotatedString {
        // Matches ^{...} or ^digits (with optional sign), and _{...} or _digits
        val regex = Regex("\\^\\{([^}]+)\\}|\\^(-?\\d+)|_\\{([^}]+)\\}|_(-?\\d+)")
        var lastIndex = 0
        for (m in regex.findAll(text)) {
            // append text before match
            if (m.range.first > lastIndex) {
                append(text.substring(lastIndex, m.range.first))
            }

            val supers = m.groups[1]?.value ?: m.groups[2]?.value
            val subs = m.groups[3]?.value ?: m.groups[4]?.value

            if (supers != null) {
                pushStyle(SpanStyle(baselineShift = BaselineShift.Superscript, fontSize = style.fontSize * 0.75))
                append(supers)
                pop()
            } else if (subs != null) {
                pushStyle(SpanStyle(baselineShift = BaselineShift.Subscript, fontSize = style.fontSize * 0.75))
                append(subs)
                pop()
            }

            lastIndex = m.range.last + 1
        }

        // append remaining text
        if (lastIndex < text.length) append(text.substring(lastIndex))
    }
    Text(
        text = annotatedString,
        modifier = modifier,
        textAlign = textAlign,
        style = style,
        color = color
    )
}


@Preview(showBackground = true)
@Composable
fun SIConstantsScreenPreview() {
    AppTheme(themeMode = ThemeMode.AUTO) {
        SIConstantsScreen()
    }
}
