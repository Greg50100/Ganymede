package com.joviansapps.ganymede.ui.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph
import com.joviansapps.ganymede.R

@Composable
@Preview
fun HomeScreen(
    onOpenCalculator: () -> Unit = {},
    onOpenConverter: () -> Unit = {},
    onOpenGraph: () -> Unit = {},
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(PaddingValues(24.dp)),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
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

        // Idées futures: ajoute d’autres boutons ici (QR, Chronomètre, Notes, etc.)
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