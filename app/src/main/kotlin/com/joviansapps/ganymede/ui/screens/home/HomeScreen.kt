package com.joviansapps.ganymede.ui.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.joviansapps.ganymede.R

@Composable
fun HomeScreen(
    onOpenCalculator: () -> Unit,
    onOpenConverter: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(PaddingValues(24.dp)),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(stringResource(R.string.welcome_home), style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(24.dp))

        CardButton(
            title = stringResource(R.string.calculator_title),
            description = stringResource(R.string.calculator_description),
            onClick = onOpenCalculator
        )

        Spacer(Modifier.height(16.dp))

        CardButton(
            title = stringResource(R.string.converter_title),
            description = stringResource(R.string.converter_description),
            onClick = onOpenConverter
        )

        // Idées futures: ajoute d’autres CardButton ici (QR, Chronomètre, Notes, etc.)
    }
}

@Composable
private fun CardButton(
    title: String,
    description: String,
    onClick: () -> Unit
) {
    Card(onClick = onClick) {
        Column(Modifier.padding(20.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(6.dp))
            Text(description, style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.height(12.dp))
            Button(onClick = onClick) { Text(stringResource(R.string.open_button)) }
        }
    }
}