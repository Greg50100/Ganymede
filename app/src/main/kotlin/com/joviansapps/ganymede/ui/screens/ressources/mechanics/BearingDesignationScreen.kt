package com.joviansapps.ganymede.ui.screens.ressources.mechanics

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.joviansapps.ganymede.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BearingDesignationScreen(onBack: (() -> Unit)? = null) {
    Scaffold { padding ->
        Column(Modifier.padding(padding).padding(16.dp).verticalScroll(rememberScrollState())) {
            Text("Example: Bearing 6203-2RS", style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(16.dp))

            Text("First Digit: Bearing Type", fontWeight = FontWeight.Bold)
            Text("6 -> Deep Groove Ball Bearing")
            Spacer(Modifier.height(8.dp))

            Text("Second Digit: Series", fontWeight = FontWeight.Bold)
            Text("2 -> Light Series")
            Spacer(Modifier.height(8.dp))

            Text("Third & Fourth Digits: Bore Diameter", fontWeight = FontWeight.Bold)
            Text("03 -> Bore diameter = 3 x 5 = 15 mm (for 00-03, special rules apply. For 04+, multiply by 5)")
            Spacer(Modifier.height(8.dp))

            Text("Suffixes: Seals/Shields", fontWeight = FontWeight.Bold)
            Text("2RS -> Two Rubber Seals (one on each side)")
            Text("Common suffixes: Z (Single Shield), ZZ (Two Shields), RS (Single Rubber Seal)", fontFamily = FontFamily.Monospace)
        }
    }
}
