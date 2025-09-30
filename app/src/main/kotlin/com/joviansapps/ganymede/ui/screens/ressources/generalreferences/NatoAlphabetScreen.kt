package com.joviansapps.ganymede.ui.screens.ressources.generalreferences

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.*
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.Icons
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.joviansapps.ganymede.R

@Composable
fun NatoAlphabetScreen() {
    val alphabet = mapOf(
        "A" to "Alpha", "B" to "Bravo", "C" to "Charlie", "D" to "Delta", "E" to "Echo",
        "F" to "Foxtrot", "G" to "Golf", "H" to "Hotel", "I" to "India", "J" to "Juliett",
        "K" to "Kilo", "L" to "Lima", "M" to "Mike", "N" to "November", "O" to "Oscar",
        "P" to "Papa", "Q" to "Quebec", "R" to "Romeo", "S" to "Sierra", "T" to "Tango",
        "U" to "Uniform", "V" to "Victor", "W" to "Whiskey", "X" to "X-ray", "Y" to "Yankee", "Z" to "Zulu"
    )

    Scaffold(
        topBar = { TopAppBar(title = { Text(stringResource(id = R.string.nato_phonetic_alphabet)) }, navigationIcon = { IconButton(onClick = {}) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back") } }) }
    ) { padding ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.padding(padding),
            contentPadding = PaddingValues(16.dp)
        ) {
            items(alphabet.entries.toList()) { (letter, word) ->
                Text("$letter - $word", fontWeight = FontWeight.Bold, modifier = Modifier.padding(8.dp))
            }
        }
    }
}
