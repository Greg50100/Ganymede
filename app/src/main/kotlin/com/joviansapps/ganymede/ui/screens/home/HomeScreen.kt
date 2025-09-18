package com.joviansapps.ganymede.ui.screens.home

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.joviansapps.ganymede.R

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
    val buyMeACoffeeUrl = stringResource(id = R.string.buy_me_a_coffee_url) // Changed to a dedicated URL string

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(stringResource(R.string.welcome_home), style = MaterialTheme.typography.headlineSmall)
            Spacer(Modifier.height(24.dp))

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
                onClick = { /* Nothing for now */ },
            )
        }

        // The button is now at the bottom of the main column
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            BuyMeACoffeeButton {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(buyMeACoffeeUrl)).apply {
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
                Text(description, style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant))
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
            Text(title, modifier = Modifier.padding(horizontal = 8.dp), style = titleStyle)
        }
    }
}

@Composable
private fun BuyMeACoffeeButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    FilledTonalButton(
        onClick = onClick,
        modifier = modifier,
        colors = ButtonDefaults.filledTonalButtonColors(
            containerColor = Color(0xFFFFDD00), // Yellow color from the image
            contentColor = Color.Black
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Icon(
            painter = painterResource(id = R.drawable.cofee_logo),
            contentDescription = null, // The text provides the description
            modifier = Modifier.size(ButtonDefaults.IconSize)
        )
        Spacer(Modifier.size(ButtonDefaults.IconSpacing))
        Text(stringResource(R.string.buy_me_a_coffee))
    }
}
