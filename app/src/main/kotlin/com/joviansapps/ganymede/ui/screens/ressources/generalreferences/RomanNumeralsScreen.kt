package com.joviansapps.ganymede.ui.screens.ressources.generalreferences

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.Icons
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.joviansapps.ganymede.R

private data class RomanNumeral(val numeral: String, val value: Int)

@Composable
fun RomanNumeralsScreen() {
    val numerals = listOf(
        RomanNumeral("I", 1),
        RomanNumeral("V", 5),
        RomanNumeral("X", 10),
        RomanNumeral("L", 50),
        RomanNumeral("C", 100),
        RomanNumeral("D", 500),
        RomanNumeral("M", 1000)
    )
    val examples = listOf(
        "II" to "2 (1+1)",
        "IV" to "4 (5-1)",
        "IX" to "9 (10-1)",
        "XLII" to "42 (50-10 + 2)",
        "MMXXIV" to "2024 (1000+1000+10+10+5-1)"
    )

    Scaffold(
        topBar = { TopAppBar(title = { Text(stringResource(id = R.string.roman_numerals)) }, navigationIcon = { IconButton(onClick = {}) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back") } }) }
    ) { padding ->
        Column(Modifier.padding(padding).padding(16.dp)) {
            Text("Base Numerals", style = MaterialTheme.typography.h6)
            LazyColumn(modifier = Modifier.weight(1f)) {
                item {
                    Row(Modifier.fillMaxWidth().background(MaterialTheme.colors.primaryVariant).padding(vertical = 8.dp)) {
                        Text("Numeral", modifier = Modifier.weight(1f).padding(start = 8.dp), fontWeight = FontWeight.Bold, color = MaterialTheme.colors.onPrimary)
                        Text("Value", modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold, color = MaterialTheme.colors.onPrimary)
                    }
                }
                items(numerals) { n ->
                    Row(Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                        Text(n.numeral, modifier = Modifier.weight(1f).padding(start=8.dp))
                        Text(n.value.toString(), modifier = Modifier.weight(1f))
                    }
                }
            }
            Spacer(Modifier.height(16.dp))
            Text("Examples", style = MaterialTheme.typography.h6)
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(examples) { (numeral, value) ->
                    Text("$numeral = $value", modifier = Modifier.padding(vertical = 4.dp))
                }
            }
        }
    }
}
