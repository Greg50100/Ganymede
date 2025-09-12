package com.joviansapps.ganymede.ui.screens.home

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.joviansapps.ganymede.R
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImage
import androidx.compose.material3.IconButton
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign


@Composable
@Preview
@SuppressLint("SetJavaScriptEnabled")
fun HomeScreen(
    onOpenCalculator: () -> Unit = {},
    onOpenConverter: () -> Unit = {},
    onOpenGraph: () -> Unit = {},
    onOpenUtilities: () -> Unit = {},
) {
    val context = LocalContext.current

    // TODO: Extraire l'URL BuyMeACoffee vers strings.xml (ou BuildConfig) pour éviter la valeur en dur.
    // TODO: Vérifier le contraste/tailles pour accessibilité; éviter les magic numbers (200.dp) via dimension resources.

    // Use a Box to position top content and a bottom-aligned button without Modifier.weight
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // Contenu principal aligné en haut avec padding (padding appliqué seulement au contenu)
        Column(modifier = Modifier
            .align(Alignment.TopCenter)
            .padding(PaddingValues(24.dp))) {
            Text(stringResource(R.string.welcome_home), style = MaterialTheme.typography.headlineSmall)
            Spacer(Modifier.height(24.dp))

            // Bouton Outlined avec label chevauchant la bordure
            OutlinedLabelButton(
                title = stringResource(R.string.calculator_title),
                description = stringResource(R.string.calculator_description),
                onClick = onOpenCalculator
            )

            Spacer(Modifier.height(16.dp))

            OutlinedLabelButton(
                title = stringResource(R.string.graph_title),
                description = stringResource(R.string.graph_description),
                onClick = onOpenGraph
            )

            Spacer(Modifier.height(16.dp))

            OutlinedLabelButton(
                title = stringResource(R.string.converter_title),
                description = stringResource(R.string.converter_description),
                onClick = onOpenConverter
            )

            Spacer(Modifier.height(16.dp))

            OutlinedLabelButton(
                title = stringResource(R.string.utilities_title),
                description = stringResource(R.string.utilities_description),
                onClick = onOpenUtilities,
            )

            Spacer(Modifier.height(16.dp))

            OutlinedLabelButton(
                title = stringResource(R.string.more_coming_soon),
                description = stringResource(R.string.more_coming_soon_description),
                onClick = { /* Rien pour l'instant */ },
            )
        }

        // Bouton centré en bas (en dehors du padding appliqué au contenu)
        Box(modifier = Modifier.align(Alignment.BottomCenter)) {
            BuyMeACoffeeButton(modifier = Modifier
                .fillMaxWidth()
            ) {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://buymeacoffee.com/gregorychau")).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(intent)
            }
        }
    }
}

@Composable
@Preview
private fun OutlinedLabelButton(
    title: String,
    description: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    titleStyle: TextStyle = MaterialTheme.typography.titleLarge.copy(color = MaterialTheme.colorScheme.primary)
) {
    val corner = RoundedCornerShape(8.dp)

    Box(modifier = modifier.fillMaxWidth()) {
        OutlinedButton(
            onClick = onClick,
            shape = corner,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp)
        ) {
            Column(Modifier.padding(vertical = 8.dp, horizontal = 8.dp)) {
                Text(description, style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant) )
            }
        }

        // Label qui chevauche la bordure de l'OutlinedButton
        Surface(
            color = MaterialTheme.colorScheme.background,
            shape = corner,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = 20.dp)
                .offset(y = (-13).dp)
        ) {
            Text(title, modifier = Modifier.padding(horizontal = 8.dp), style = titleStyle)
        }
    }
}

@Composable
private fun BuyMeACoffeeButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit)
{
    // L'image drawable/yellow_img.webp devient le bouton. Utilise IconButton pour ripple et accessibilité.
    IconButton(
        onClick = onClick,
        modifier = modifier
    ) {
        // Utiliser la ressource locale (res/drawable/yellow_img.webp)
        androidx.compose.foundation.Image(
            painter = painterResource(id = R.drawable.yellow_img),
            contentDescription = stringResource(R.string.buy_me_a_coffee),
            // taille fixe pour conserver les proportions et rester alignée en bas
            modifier = Modifier.size(200
                .dp)


        )
    }
}
