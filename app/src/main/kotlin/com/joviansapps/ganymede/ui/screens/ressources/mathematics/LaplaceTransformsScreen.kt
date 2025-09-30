package com.joviansapps.ganymede.ui.screens.ressources.mathematics

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

private data class LaplaceTransform(val function: String, val transform: String)

@Composable
fun LaplaceTransformsScreen() {
    val transforms = listOf(
        LaplaceTransform("1", "1/s"),
        LaplaceTransform("tⁿ", "n! / sⁿ⁺¹"),
        LaplaceTransform("eᵃᵗ", "1 / (s-a)"),
        LaplaceTransform("sin(at)", "a / (s² + a²)"),
        LaplaceTransform("cos(at)", "s / (s² + a²)"),
        LaplaceTransform("f'(t)", "sF(s) - f(0)")
    )

    Scaffold { padding ->
        LazyColumn(modifier = Modifier.padding(padding).padding(16.dp)) {
            item {
                Row(Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.primaryContainer).padding(vertical = 8.dp)) {
                    Text("f(t)", modifier = Modifier.weight(1f).padding(start=8.dp), fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimaryContainer)
                    Text("F(s)", modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimaryContainer)
                }
            }
            items(transforms) { t ->
                Row(Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                    Text(t.function, modifier = Modifier.weight(1f).padding(start=8.dp), fontFamily = FontFamily.Monospace)
                    Text(t.transform, modifier = Modifier.weight(1f), fontFamily = FontFamily.Monospace)
                }
            }
        }
    }
}
