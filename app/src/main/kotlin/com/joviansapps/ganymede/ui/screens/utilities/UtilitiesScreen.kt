package com.joviansapps.ganymede.ui.screens.utilities

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.joviansapps.ganymede.R
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview


@Composable
@Preview
fun UtilitiesScreen(
    onOpenElectronics: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Top
    ) {

        UtilityButton(
            title = stringResource(id = R.string.electronics_category_title),
            description = stringResource(id = R.string.electronics_category_description),
            onClick = onOpenElectronics
        )

        Spacer(Modifier.height(32.dp))

        Text(
            text = stringResource(id = R.string.more_coming_soon),
            style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
        )
    }
}

@Composable
private fun UtilityButton(
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
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                )
            }
        }
        Surface(
            color = MaterialTheme.colorScheme.background,
            shape = corner,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = 20.dp)
                .offset(y = (-13).dp)
        ) {
            Text(
                text = title,
                modifier = Modifier.padding(horizontal = 8.dp),
                style = titleStyle
            )
        }
    }
}
