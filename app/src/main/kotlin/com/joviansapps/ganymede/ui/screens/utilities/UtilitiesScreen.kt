package com.joviansapps.ganymede.ui.screens.utilities

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.joviansapps.ganymede.R

// 1. Modèle de données pour la scalabilité
private data class Utility(
    val titleRes: Int,
    val descriptionRes: Int,
    val onClick: () -> Unit
)

@Composable
@Preview
fun UtilitiesScreen(
    onOpenElectronics: () -> Unit = {}
) {
    val utilities = listOf(
        Utility(
            titleRes = R.string.electronics_category_title,
            descriptionRes = R.string.electronics_category_description,
            onClick = onOpenElectronics
        )
        // Vous pourrez en ajouter d'autres ici
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        utilities.forEach { utility ->
            UtilityButton(
                title = stringResource(id = utility.titleRes),
                onClick = utility.onClick
            ) {
                Text(
                    text = stringResource(id = utility.descriptionRes),
                    style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        Text(
            text = stringResource(id = R.string.more_coming_soon),
            style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
        )
    }
}

@Composable
private fun UtilityButton(
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    titleStyle: TextStyle = MaterialTheme.typography.titleLarge.copy(color = MaterialTheme.colorScheme.primary),
    content: @Composable ColumnScope.() -> Unit // 3. Utilisation du Slot API
) {
    val titleHeightEstimate = 26.dp // Hauteur approximative du conteneur de titre

    Box(modifier = modifier) {
        OutlinedButton(
            onClick = onClick,
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                // 2. Éviter les nombres magiques en liant le padding à une estimation
                .padding(top = titleHeightEstimate / 2)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .padding(top = titleHeightEstimate / 2),
                horizontalAlignment = Alignment.Start,
                content = content
            )
        }

        Surface(
            color = MaterialTheme.colorScheme.background,
            modifier = Modifier
                .padding(start = 20.dp)
        ) {
            Text(
                text = title,
                modifier = Modifier.padding(horizontal = 8.dp),
                style = titleStyle
            )
        }
    }
}