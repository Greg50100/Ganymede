package com.joviansapps.ganymede.ui.screens.utilities.electronics.resistorcalculator

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import java.util.Locale
import com.joviansapps.ganymede.R

@Composable
@Preview
fun ResistorColorCodeScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ResistorColorCodeCalculator()
    }
}

@Composable
fun ResistorCalculatorScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ResistorColorCodeCalculator()
    }
}

@Composable
private fun ResistorColorCodeCalculator() {
    val digitColors = listOf(
        stringResource(R.string.black) to 0,
        stringResource(R.string.brown) to 1,
        stringResource(R.string.red) to 2,
        stringResource(R.string.orange) to 3,
        stringResource(R.string.yellow) to 4,
        stringResource(R.string.green) to 5,
        stringResource(R.string.blue) to 6,
        stringResource(R.string.violet) to 7,
        stringResource(R.string.gray) to 8,
        stringResource(R.string.white) to 9
    )
    val multiplierColors = listOf(
        stringResource(R.string.black_1) to 1.0,
        stringResource(R.string.brown_10) to 10.0,
        stringResource(R.string.red_100) to 100.0,
        stringResource(R.string.orange_1k) to 1_000.0,
        stringResource(R.string.yellow_10k) to 10_000.0,
        stringResource(R.string.green_100k) to 100_000.0,
        stringResource(R.string.blue_1M) to 1_000_000.0,
        stringResource(R.string.violet_10M) to 10_000_000.0,
        stringResource(R.string.gray_100M) to 100_000_000.0,
        stringResource(R.string.white_1G) to 1_000_000_000.0,
        stringResource(R.string.gold_0_1) to 0.1,
        stringResource(R.string.silver_0_01) to 0.01
    )
    val toleranceColors = listOf(
        stringResource(R.string.gold_5) to 5.0,
        stringResource(R.string.silver_10) to 10.0,
        stringResource(R.string.brown_1) to 1.0,
        stringResource(R.string.red_2) to 2.0
    )
    val ppmColors = listOf(
        stringResource(R.string.brown_100ppm) to 100,
        stringResource(R.string.red_50ppm) to 50,
        stringResource(R.string.orange_15ppm) to 15,
        stringResource(R.string.yellow_25ppm) to 25,
        stringResource(R.string.blue_10ppm) to 10,
        stringResource(R.string.violet_5ppm) to 5
    )
    val colorMap = mapOf(
        stringResource(R.string.black) to Color(0xFF000000),
        stringResource(R.string.brown) to Color(0xFF795548),
        stringResource(R.string.red) to Color(0xFFF44336),
        stringResource(R.string.orange) to Color(0xFFFF9800),
        stringResource(R.string.yellow) to Color(0xFFFFEB3B),
        stringResource(R.string.green) to Color(0xFF4CAF50),
        stringResource(R.string.blue) to Color(0xFF2196F3),
        stringResource(R.string.violet) to Color(0xFF9C27B0),
        stringResource(R.string.gray) to Color(0xFF9E9E9E),
        stringResource(R.string.white) to Color(0xFFFFFFFF),
        stringResource(R.string.gold) to Color(0xFFFFD700),
        stringResource(R.string.silver) to Color(0xFFC0C0C0)
    )

    var bandCount by remember { mutableStateOf(4) }
    var band1 by remember { mutableStateOf(digitColors[1].first) }
    var band2 by remember { mutableStateOf(digitColors[0].first) }
    var band3 by remember { mutableStateOf(digitColors[0].first) }
    var band4 by remember { mutableStateOf(multiplierColors[0].first) }
    var band5 by remember { mutableStateOf(toleranceColors[0].first) }
    var band6 by remember { mutableStateOf(ppmColors[0].first) }

    // Hauteurs fixes définies en code (modifier ici)
    val END_BAND_HEIGHT_DP = 99
    val MID_BAND_HEIGHT_DP = 69

    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        listOf(3, 4, 5, 6).forEach { count ->
            val isSelected = bandCount == count
            val colors = if (isSelected) {
                ButtonDefaults.outlinedButtonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                ButtonDefaults.outlinedButtonColors()
            }
            OutlinedButton(
                onClick = { bandCount = count },
                shape = RoundedCornerShape(8.dp),
                colors = colors
            ) { Text(text = "$count") }
        }
    }


    BoxWithConstraints(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
    )
    {
        Image(
            painter = painterResource(id = R.drawable.bg_resistor_body_2),
            contentDescription = null,
            modifier = Modifier.matchParentSize()
        )
        val bandWidth = 16.dp
        val FRACTIONS_6 = listOf(0.225f, 0.355f, 0.455f, 0.555f, 0.655f, 0.775f)
        // Définition des indices actifs selon la demande utilisateur
        val activeIndices: List<Int> = when (bandCount) {
            3 -> listOf(1, 2, 3)          // D1, D2, Mult décalés
            4 -> listOf(1, 2, 3, 4)       // D1, D2, Mult, Tol
            5 -> listOf(0, 1, 2, 3, 4)    // D1, D2, D3, Mult, Tol
            6 -> listOf(0, 1, 2, 3, 4, 5) // Tous
            else -> emptyList()
        }
        // Séquence logique des couleurs à répartir sur les indices actifs
        val logicalColors: List<String> = when (bandCount) {
            3 -> listOf(band1, band2, band4)
            4 -> listOf(band1, band2, band4, band5)
            5 -> listOf(band1, band2, band3, band4, band5)
            6 -> listOf(band1, band2, band3, band4, band5, band6)
            else -> emptyList()
        }
        // Construction mapping index physique -> couleur (ou null si inactif)
        val indexToColor: Map<Int, String?> = buildMap {
            activeIndices.forEachIndexed { seqIdx, physicalIndex ->
                put(physicalIndex, logicalColors.getOrNull(seqIdx))
            }
            // indices non actifs seront absents => null
        }
        for (index in 0 until 6) {
            val active = index in activeIndices
            val colorName = indexToColor[index]
            val fraction = FRACTIONS_6[index]
            val bandHeight = if (index == 0 || index == 5) END_BAND_HEIGHT_DP.dp else MID_BAND_HEIGHT_DP.dp
            val yOffset = (maxHeight - bandHeight) / 2
            val xOffset = maxWidth * fraction - bandWidth / 2
            val baseColor = colorName?.let { colorMap[it.substringBefore(' ')] } ?: Color.LightGray
            val displayColor = if (active) baseColor else baseColor.copy(alpha = 0.18f)
            Box(
                modifier = Modifier
                    .offset(x = xOffset, y = yOffset)
                    .size(width = bandWidth, height = bandHeight)
                    .background(displayColor)
            )
        }
    }
    // Spinners verticaux compacts empilés
    CompactColorDropdown(
        items = digitColors.map { it.first },
        selected = band1,
        onSelected = { band1 = it },
        label = stringResource(id = R.string.band_1_label),
        colorMap = colorMap
    )
    CompactColorDropdown(
        items = digitColors.map { it.first },
        selected = band2,
        onSelected = { band2 = it },
        label = stringResource(id = R.string.band_2_label),
        colorMap = colorMap
    )
    if (bandCount >= 5) {
        CompactColorDropdown(
            items = digitColors.map { it.first },
            selected = band3,
            onSelected = { band3 = it },
            label = stringResource(id = R.string.band_3_label),
            colorMap = colorMap
        )
    }
    CompactColorDropdown(
        items = multiplierColors.map { it.first },
        selected = band4,
        onSelected = { band4 = it },
        label = stringResource(id = R.string.multiplier_label),
        colorMap = colorMap
    )
    if (bandCount >= 4) {
        CompactColorDropdown(
            items = toleranceColors.map { it.first },
            selected = band5,
            onSelected = { band5 = it },
            label = stringResource(id = R.string.tolerance_label),
            colorMap = colorMap
        )
    }
    if (bandCount == 6) {
        CompactColorDropdown(
            items = ppmColors.map { it.first },
            selected = band6,
            onSelected = { band6 = it },
            label = stringResource(id = R.string.ppm_label),
            colorMap = colorMap
        )
    }

    val d1 = digitColors.first { it.first == band1 }.second
    val d2 = digitColors.first { it.first == band2 }.second
    val d3 = if (bandCount >= 5) digitColors.first { it.first == band3 }.second else null
    val mult = multiplierColors.first { it.first == band4 }.second
    val tol = if (bandCount >= 4) toleranceColors.first { it.first == band5 }.second else 20.0
    val ppm = if (bandCount == 6) ppmColors.first { it.first == band6 }.second else null
    val resistance = when (bandCount) {
        3, 4 -> ((d1 * 10) + d2) * mult
        5, 6 -> ((d1 * 100) + (d2 * 10) + (d3 ?: 0)) * mult
        else -> 0.0
    }
    Spacer(Modifier.height(8.dp))
    Text(text = stringResource(id = R.string.resistance_result_format, formatResistance(resistance)))
    Text(text = stringResource(id = R.string.tolerance_percentage_format, tol) + if (bandCount == 3) stringResource(id = R.string.default_suffix) else "")
    if (bandCount == 6 && ppm != null) {
        Text(text = stringResource(id = R.string.temp_coefficient_format, ppm))
    }
}

@Composable
private fun CompactColorDropdown(
    items: List<String>,
    selected: String,
    onSelected: (String) -> Unit,
    label: String,
    colorMap: Map<String, Color>
) {
    var expanded by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()
    val listBg = MaterialTheme.colorScheme.surfaceVariant
    val selectedBg = MaterialTheme.colorScheme.primaryContainer
    val selectedText = MaterialTheme.colorScheme.onPrimaryContainer
    val itemTextColor = MaterialTheme.colorScheme.onSurfaceVariant

    Column(Modifier.fillMaxWidth()) {
        Box {
            OutlinedTextField(
                value = selected,
                onValueChange = { /* Ne rien faire car en lecture seule */ },
                modifier = Modifier
                    .fillMaxWidth(),
                label = { Text(label) },
                readOnly = true,
                trailingIcon = {
                    Text(if (expanded) stringResource(id = R.string.arrow_up) else stringResource(id = R.string.arrow_down))
                },
                leadingIcon = {
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .background(
                                colorMap[selected.substringBefore(' ')] ?: Color.Transparent,
                                RoundedCornerShape(4.dp)
                            )
                    )
                })
            // Box transparente pour intercepter les clics sur tout le champ
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .clickable { expanded = !expanded }
            )

        }

        if (expanded) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp) // Un peu de marge
                    .background(listBg, RoundedCornerShape(4.dp))
            ) {
                Column(
                    modifier = Modifier
                        .heightIn(max = 240.dp)
                        .verticalScroll(scrollState)
                        .padding(vertical = 6.dp),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    items.forEach { item ->
                        val isSel = item == selected
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    if (isSel) selectedBg else Color.Transparent,
                                    RoundedCornerShape(6.dp)
                                )
                                .clickable {
                                    onSelected(item)
                                    expanded = false
                                }
                                .padding(horizontal = 14.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(18.dp)
                                    .background(
                                        colorMap[item.substringBefore(' ')] ?: Color.Transparent,
                                        RoundedCornerShape(4.dp)
                                    )
                            )
                            Text(
                                text = item,
                                style = MaterialTheme.typography.bodySmall,
                                color = if (isSel) selectedText else itemTextColor,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        }
        Spacer(Modifier.height(6.dp))
    }
}

private fun formatResistance(r: Double): String {
    return when {
        r >= 1_000_000 -> String.format(Locale.US, "%.2fM", r / 1_000_000)
        r >= 1000 -> String.format(Locale.US, "%.2fk", r / 1000)
        else -> String.format(Locale.US, "%.2f", r)
    }
}
