package com.joviansapps.ganymede.ui.screens.ressources.mathematics

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import com.joviansapps.ganymede.R

private data class IdentityCategory(val name: String, val identities: List<String>)

@Composable
fun TrigIdentitiesScreen() {
    val categories = listOf(
        IdentityCategory("Pythagorean Identities", listOf(
            "sin²(θ) + cos²(θ) = 1",
            "tan²(θ) + 1 = sec²(θ)"
        )),
        IdentityCategory("Angle Sum/Difference", listOf(
            "sin(α ± β) = sin(α)cos(β) ± cos(α)sin(β)",
            "cos(α ± β) = cos(α)cos(β) ∓ sin(α)sin(β)"
        )),
        IdentityCategory("Double Angle", listOf(
            "sin(2θ) = 2sin(θ)cos(θ)",
            "cos(2θ) = cos²(θ) - sin²(θ)"
        ))
    )

    Scaffold { padding ->
        LazyColumn(modifier = Modifier.padding(padding)) {
            items(categories) { category ->
                Card(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)) {
                    Column(Modifier.padding(16.dp)) {
                        Text(category.name, style = MaterialTheme.typography.titleMedium)
                        category.identities.forEach {
                            Text(it, fontFamily = FontFamily.Monospace, modifier = Modifier.padding(top = 8.dp))
                        }
                    }
                }
            }
        }
    }
}
