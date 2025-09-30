package com.joviansapps.ganymede.ui.screens.ressources.generalreferences

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Science
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.joviansapps.ganymede.R
import com.joviansapps.ganymede.ui.screens.ressources.common.ResourcesCategoryGridScreen
import com.joviansapps.ganymede.ui.screens.ressources.common.CategoryItem

@Composable
fun GeneralReferencesCategoryScreen(
    onOpenGreekAlphabet: () -> Unit,
    onOpenMorseCode: () -> Unit,
    onOpenNatoAlphabet: () -> Unit,
    onOpenRomanNumerals: () -> Unit,
    modifier: Modifier = Modifier
) {
    val generalReferencesItems = listOf(
        CategoryItem(
            title = "Greek Alphabet", //TODO To be added in strings.xml
            description = "List of Greek letters and their names.", //TODO To be added in strings.xml
            icon = Icons.Default.Science,
            onClick = onOpenGreekAlphabet
        ),
        CategoryItem(
            title = "Morse Code", //TODO To be added in strings.xml
            description = "Morse code chart for letters and numbers.", //TODO To be added in strings.xml
            icon = Icons.Default.Science,
            onClick = onOpenMorseCode
        ),
        CategoryItem(
            title = "NATO Phonetic Alphabet", //TODO To be added in strings.xml
            description = "NATO phonetic alphabet for clear communication.", //TODO To be added in strings.xml
            icon = Icons.Default.Science,
            onClick = onOpenNatoAlphabet
        ),
        CategoryItem(
            title = "Roman Numerals", //TODO To be added in strings.xml
            description = "Conversion chart for Roman numerals.", //TODO To be added in strings.xml
            icon = Icons.Default.Science,
            onClick = onOpenRomanNumerals
        )
        // Other general reference items can be added here
    )

    ResourcesCategoryGridScreen(items = generalReferencesItems, modifier = modifier)
}