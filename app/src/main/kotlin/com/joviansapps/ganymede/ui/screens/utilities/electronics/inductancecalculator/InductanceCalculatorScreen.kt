package com.joviansapps.ganymede.ui.screens.utilities.electronics.inductancecalculator

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.joviansapps.ganymede.R
import com.joviansapps.ganymede.ui.components.ColorSelectionDropdown
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.floor
import kotlin.math.log10
import kotlin.math.pow

private val digitColors = @Composable {
    listOf(
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
}

private val multiplierColors = @Composable {
    listOf(
        stringResource(R.string.black) to 1.0,
        stringResource(R.string.brown) to 10.0,
        stringResource(R.string.red) to 100.0,
        stringResource(R.string.orange) to 1000.0,
        stringResource(R.string.yellow) to 10000.0,
        stringResource(R.string.gold) to 0.1,
        stringResource(R.string.silver) to 0.01
    )
}

private val toleranceColors = @Composable {
    listOf(
        stringResource(R.string.gold) to "±5%",
        stringResource(R.string.silver) to "±10%",
        stringResource(R.string.black) to "±20%",
        stringResource(R.string.brown) to "±1%",
        stringResource(R.string.red) to "±2%",
        stringResource(R.string.orange) to "±3%",
        stringResource(R.string.yellow) to "±4%",
        stringResource(R.string.green) to "±0.5%",
        stringResource(R.string.blue) to "±0.25%",
        stringResource(R.string.violet) to "±0.1%",
        stringResource(R.string.gray) to "±0.05%",
        stringResource(R.string.none) to "±20%"
    )
}

private val colorMap = @Composable {
    mapOf(
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
        stringResource(R.string.silver) to Color(0xFFC0C0C0),
        stringResource(R.string.none) to Color.Transparent,
    )
}

@Composable
@Preview
fun InductanceColorCodeScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        InductanceColorCodeCalculator()
    }
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun InductanceCalculatorScreen() {
    val tabs = listOf(
        stringResource(id = R.string.tab_color_code),
        stringResource(id = R.string.tab_value_to_color),
        stringResource(id = R.string.tab_winding)
    )
    val pagerState = rememberPagerState(pageCount = { tabs.size })
    val coroutineScope = rememberCoroutineScope()

    Column(modifier = Modifier.fillMaxSize()) {
        TabRow(selectedTabIndex = pagerState.currentPage) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = pagerState.currentPage == index,
                    onClick = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(index)
                        }
                    },
                    text = { Text(title, maxLines = 1, overflow = TextOverflow.Ellipsis) }
                )
            }
        }
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                when (page) {
                    0 -> InductanceColorCodeCalculator()
                    1 -> InductanceValueToColorCalculator()
                    2 -> CoilWindingCalculator()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun InductanceValueToColorCalculator() {
    var inductanceInput by remember { mutableStateOf("10") }
    val units = listOf("µH", "mH")
    var selectedUnit by remember { mutableStateOf(units[0]) }
    val currentToleranceColors = toleranceColors()
    var selectedToleranceString by remember { mutableStateOf(currentToleranceColors[0].second) }

    val toleranceValueToColorNameMap = remember { currentToleranceColors.associate { (k, v) -> v to k } }
    val toleranceBandColorName = toleranceValueToColorNameMap[selectedToleranceString] ?: currentToleranceColors[0].first

    val currentDigitColors = digitColors()
    val currentMultiplierColors = multiplierColors()
    val currentColorMap = colorMap() // récupéré dans le scope @Composable
    val (band1, band2, multiplier) = remember(inductanceInput, selectedUnit) {
        calculateBandsFromValue(inductanceInput, selectedUnit, currentDigitColors, currentMultiplierColors)
    }

    InductorImageWithBands(band1, band2, multiplier, toleranceBandColorName, currentColorMap)

    Spacer(modifier = Modifier.height(16.dp))

    var unitMenuExpanded by remember { mutableStateOf(false) }
    Row(verticalAlignment = Alignment.CenterVertically) {
        OutlinedTextField(
            value = inductanceInput,
            onValueChange = { inductanceInput = it },
            label = { Text(stringResource(id = R.string.inductance_value_label)) },
            modifier = Modifier.weight(1f),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
        )
        Spacer(modifier = Modifier.width(8.dp))
        ExposedDropdownMenuBox(
            expanded = unitMenuExpanded,
            onExpandedChange = { unitMenuExpanded = !it }
        ) {
            OutlinedTextField(
                value = selectedUnit,
                onValueChange = {},
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = unitMenuExpanded) },
                modifier = Modifier.width(100.dp)
            )
            ExposedDropdownMenu(
                expanded = unitMenuExpanded,
                onDismissRequest = { unitMenuExpanded = false }
            ) {
                units.forEach { unit ->
                    DropdownMenuItem(
                        text = { Text(unit) },
                        onClick = {
                            selectedUnit = unit
                            unitMenuExpanded = false
                        }
                    )
                }
            }
        }
    }

    ColorSelectionDropdown(
        items = currentToleranceColors.map { it.second },
        selected = selectedToleranceString,
        onSelected = { selectedToleranceString = it },
        label = stringResource(id = R.string.tolerance_label),
        getColorForItem = { item ->
            val colorName = toleranceValueToColorNameMap[item]
            currentColorMap[colorName]
        }
    )
}

private fun calculateBandsFromValue(
    valueStr: String,
    unit: String,
    digitColors: List<Pair<String, Int>>,
    multiplierColors: List<Pair<String, Double>>
): Triple<String, String, String> {
    val value = valueStr.toDoubleOrNull() ?: 0.0
    if (value <= 0) {
        return Triple(digitColors[0].first, digitColors[0].first, multiplierColors[0].first)
    }

    val valueInMicroHenries = if (unit == "mH") value * 1000 else value

    val orderOfMagnitude = floor(log10(valueInMicroHenries)).toInt()
    val multiplierValue = 10.0.pow(orderOfMagnitude - 1)

    val significantDigits = (valueInMicroHenries / multiplierValue).toInt()
    val firstDigit = significantDigits / 10
    val secondDigit = significantDigits % 10

    val band1 = digitColors.find { it.second == firstDigit }?.first ?: digitColors[0].first
    val band2 = digitColors.find { it.second == secondDigit }?.first ?: digitColors[0].first
    val multiplier = multiplierColors.minByOrNull { abs(it.second - multiplierValue) }?.first ?: multiplierColors[0].first

    return Triple(band1, band2, multiplier)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CoilWindingCalculator() {
    var turnsStr by remember { mutableStateOf("10") }
    var diameterStr by remember { mutableStateOf("10") }
    var lengthStr by remember { mutableStateOf("10") }
    val units = listOf("mm", "cm", "in")
    var diameterUnit by remember { mutableStateOf("mm") }
    var lengthUnit by remember { mutableStateOf("mm") }
    var diameterUnitExpanded by remember { mutableStateOf(false) }
    var lengthUnitExpanded by remember { mutableStateOf(false) }

    val inductance = remember(turnsStr, diameterStr, lengthStr, diameterUnit, lengthUnit) {
        calculateCoilInductance(turnsStr, diameterStr, lengthStr, diameterUnit, lengthUnit)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(stringResource(id = R.string.coil_winding_calculator_title), style = MaterialTheme.typography.headlineSmall)

        OutlinedTextField(
            value = turnsStr,
            onValueChange = { if (it.all(Char::isDigit)) turnsStr = it },
            label = { Text(stringResource(id = R.string.turns_label)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        Row(verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                value = diameterStr,
                onValueChange = { newValue ->
                    if (newValue.isEmpty() || newValue.matches(Regex("^\\d*\\.?\\d*"))) {
                        diameterStr = newValue
                    }
                },
                label = { Text(stringResource(id = R.string.coil_diameter_label)) },
                modifier = Modifier.weight(1f),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
            )
            Spacer(modifier = Modifier.width(8.dp))
            ExposedDropdownMenuBox(expanded = diameterUnitExpanded, onExpandedChange = { diameterUnitExpanded = !it }) {
                OutlinedTextField(
                    value = diameterUnit,
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = diameterUnitExpanded) },
                    modifier = Modifier.width(100.dp)
                )
                ExposedDropdownMenu(expanded = diameterUnitExpanded, onDismissRequest = { diameterUnitExpanded = false }) {
                    units.forEach { unit -> DropdownMenuItem(text = { Text(unit) }, onClick = { diameterUnit = unit; diameterUnitExpanded = false }) }
                }
            }
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                value = lengthStr,
                onValueChange = { newValue ->
                    if (newValue.isEmpty() || newValue.matches(Regex("^\\d*\\.?\\d*"))) {
                        lengthStr = newValue
                    }
                },
                label = { Text(stringResource(id = R.string.coil_length_label)) },
                modifier = Modifier.weight(1f),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
            )
            Spacer(modifier = Modifier.width(8.dp))
            ExposedDropdownMenuBox(expanded = lengthUnitExpanded, onExpandedChange = { lengthUnitExpanded = !it }) {
                OutlinedTextField(
                    value = lengthUnit,
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = lengthUnitExpanded) },
                    modifier = Modifier.width(100.dp)
                )
                ExposedDropdownMenu(expanded = lengthUnitExpanded, onDismissRequest = { lengthUnitExpanded = false }) {
                    units.forEach { unit -> DropdownMenuItem(text = { Text(unit) }, onClick = { lengthUnit = unit; lengthUnitExpanded = false }) }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = stringResource(id = R.string.calculated_inductance_format, inductance),
            style = MaterialTheme.typography.headlineSmall
        )
    }
}

private fun calculateCoilInductance(
    turnsStr: String,
    diameterStr: String,
    lengthStr: String,
    diameterUnit: String,
    lengthUnit: String
): Double {
    val n = turnsStr.toDoubleOrNull() ?: 0.0
    val dRaw = diameterStr.toDoubleOrNull() ?: 0.0
    val lRaw = lengthStr.toDoubleOrNull() ?: 0.0

    if (n <= 0 || dRaw <= 0 || lRaw <= 0) return 0.0

    val d = dRaw * when (diameterUnit) {
        "mm" -> 1 / 25.4
        "cm" -> 1 / 2.54
        else -> 1.0
    }
    val l = lRaw * when (lengthUnit) {
        "mm" -> 1 / 25.4
        "cm" -> 1 / 2.54
        else -> 1.0
    }

    return (d * d * n * n) / (18 * d + 40 * l)
}

@Composable
private fun InductanceColorCodeCalculator() {
    val currentDigitColors = digitColors()
    val currentMultiplierColors = multiplierColors()
    val currentToleranceColors = toleranceColors()
    val currentColorMap = colorMap()

    var band1 by remember { mutableStateOf(currentDigitColors[0].first) }
    var band2 by remember { mutableStateOf(currentDigitColors[0].first) }
    var multiplier by remember { mutableStateOf(currentMultiplierColors[0].first) }
    var tolerance by remember { mutableStateOf(currentToleranceColors[0].first) }

    InductorImageWithBands(band1, band2, multiplier, tolerance, currentColorMap)

    Spacer(modifier = Modifier.height(16.dp))
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        val getColor = { item: String -> currentColorMap[item.substringBefore(' ')] }
        ColorSelectionDropdown(
            items = currentDigitColors.map { it.first },
            selected = band1,
            onSelected = { band1 = it },
            label = stringResource(id = R.string.band_1_label),
            getColorForItem = getColor
        )
        ColorSelectionDropdown(
            items = currentDigitColors.map { it.first },
            selected = band2,
            onSelected = { band2 = it },
            label = stringResource(id = R.string.band_2_label),
            getColorForItem = getColor
        )
        ColorSelectionDropdown(
            items = currentMultiplierColors.map { it.first },
            selected = multiplier,
            onSelected = { multiplier = it },
            label = stringResource(id = R.string.multiplier_label),
            getColorForItem = getColor
        )

        val toleranceValueToColorNameMap = remember { currentToleranceColors.associate { (k, v) -> v to k } }
        val selectedToleranceString = currentToleranceColors.find { it.first == tolerance }?.second ?: currentToleranceColors[0].second

        ColorSelectionDropdown(
            items = currentToleranceColors.map { it.second },
            selected = selectedToleranceString,
            onSelected = {
                tolerance = toleranceValueToColorNameMap[it] ?: currentToleranceColors[0].first
            },
            label = stringResource(id = R.string.tolerance_label),
            getColorForItem = { item ->
                val colorName = toleranceValueToColorNameMap[item]
                currentColorMap[colorName]
            }
        )
    }

    val digitMap = currentDigitColors.toMap()
    val multiplierMap = currentMultiplierColors.toMap()
    val toleranceMap = currentToleranceColors.toMap()
    val inductanceValue = (digitMap[band1]!! * 10 + digitMap[band2]!!) * multiplierMap[multiplier]!!
    val inductanceString = if (inductanceValue >= 1000) {
        "%.2f mH".format(inductanceValue / 1000.0)
    } else {
        "%.2f µH".format(inductanceValue)
    }

    Spacer(modifier = Modifier.height(16.dp))
    Text(
        text = stringResource(id = R.string.inductance_result_format, inductanceString),
        style = MaterialTheme.typography.headlineSmall
    )
    Text(
        text = stringResource(id = R.string.tolerance_result_format, toleranceMap[tolerance] ?: ""),
        style = MaterialTheme.typography.headlineSmall
    )
}

@Composable
fun InductorImageWithBands(
    band1: String,
    band2: String,
    multiplier: String,
    tolerance: String,
    colorMap: Map<String, Color>
) {
    val END_BAND_HEIGHT_DP = 99
    val MID_BAND_HEIGHT_DP = 69

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
    ) {
        // Utilise la scope de BoxWithConstraints
        val boxMaxHeight = this.maxHeight
        val boxMaxWidth = this.maxWidth

        Image(
            painter = painterResource(id = R.drawable.bg_inductor_body),
            contentDescription = null,
            modifier = Modifier.matchParentSize()
        )

        val bandWidth = 16.dp
        val fractions = listOf(0.2250f, 0.4095f, 0.595f, 0.7750f)
        val colors = listOf(band1, band2, multiplier, tolerance)

        fractions.forEachIndexed { index, fraction ->
            val bandHeight = if (index == 0 || index == fractions.size - 1) END_BAND_HEIGHT_DP.dp else MID_BAND_HEIGHT_DP.dp
            val yOffset = (boxMaxHeight - bandHeight) / 2
            val xOffset = boxMaxWidth * fraction - bandWidth / 2
            Box(
                modifier = Modifier
                    .offset(x = xOffset, y = yOffset)
                    .size(width = bandWidth, height = bandHeight)
                    .background(colorMap[colors[index].substringBefore(' ')] ?: Color.Transparent)
            )
        }
    }
}
