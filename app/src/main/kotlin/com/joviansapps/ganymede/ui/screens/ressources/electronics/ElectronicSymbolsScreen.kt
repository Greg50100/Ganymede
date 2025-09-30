package com.joviansapps.ganymede.ui.screens.ressources.electronics

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.joviansapps.ganymede.R

data class SymbolInfo(val componentName: String, val iecResId: Int, val ansiResId: Int)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ElectronicSymbolsScreen() {
    // NOTE: You need to add the corresponding drawable resources to your project.
    // I've used existing drawable resources from your project as placeholders.
    // You should replace them with the correct symbol images.
    val symbols = listOf(
        SymbolInfo("Résistance", R.drawable.ic_launcher_foreground, R.drawable.ic_launcher_round_foreground),
        SymbolInfo("Condensateur", R.drawable.ic_symbol_c, R.drawable.ic_symbol_c),
        SymbolInfo("Bobine", R.drawable.ic_launcher_foreground, R.drawable.ic_launcher_round_foreground),
        SymbolInfo("Diode", R.drawable.ic_launcher_foreground, R.drawable.ic_launcher_round_foreground),
        SymbolInfo("Source DC", R.drawable.ic_launcher_foreground, R.drawable.ic_launcher_round_foreground),
        SymbolInfo("Interrupteur", R.drawable.ic_launcher_foreground, R.drawable.ic_launcher_round_foreground)
    )

    Scaffold { padding ->
        LazyColumn(modifier = Modifier.padding(padding)) {
            items(symbols) { symbol ->
                SymbolCard(symbolInfo = symbol)
            }
        }
    }
}

@Composable
fun SymbolCard(symbolInfo: SymbolInfo) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(symbolInfo.componentName, style = MaterialTheme.typography.titleLarge, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.weight(1f), horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally) {
                    Text("IEC", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                    Icon(
                        painter = painterResource(id = symbolInfo.iecResId),
                        contentDescription = "IEC Symbol for ${symbolInfo.componentName}",
                        modifier = Modifier.height(48.dp)
                    )
                }
                Column(modifier = Modifier.weight(1f), horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally) {
                    Text("ANSI", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                    Icon(
                        painter = painterResource(id = symbolInfo.ansiResId),
                        contentDescription = "ANSI Symbol for ${symbolInfo.componentName}",
                        modifier = Modifier.height(48.dp)
                    )
                }
            }
        }
    }
}
