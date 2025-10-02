package com.joviansapps.ganymede.ui.screens.ressources.electronics

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.joviansapps.ganymede.ui.components.TableHeader
import com.joviansapps.ganymede.ui.components.TableRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.tooling.preview.Preview

// Classes de données pour les formats de piles
data class CylindricalBatteryInfo(
    val commonName: String,
    val otherName: String,
    val iecName: String,
    val ansiName: String,
    val capacity: String,
    val voltage: String,
    val dimensions: String
)

data class RectangularBatteryInfo(
    val commonName: String,
    val otherName: String,
    val iecName: String,
    val ansiName: String,
    val capacity: String,
    val voltage: String,
    val dimensions: String
)

data class ButtonCellInfo(
    val iecName: String,
    val ansiName: String,
    val capacity: String,
    val voltage: String,
    val dimensions: String
)

data class AlkalineSilverButtonCellInfo(
    val iecName: String,
    val ansiName: String,
    val capacityAlkaline: String,
    val capacitySilver: String,
    val voltage: String,
    val dimensions: String
)

data class PhotoBatteryInfo(
    val commonName: String,
    val otherName: String,
    val iecName: String,
    val ansiName: String,
    val capacity: String,
    val voltage: String,
    val dimensions: String
)

// Sources de données

private fun getVolumeFromDimensions(dimensions: String): Float {
    val parts = dimensions.split("×").mapNotNull { it.trim().toFloatOrNull() }
    return when (parts.size) {
        2 -> { // Cylindrical
            val diameter = parts[0]
            val height = parts[1]
            (kotlin.math.PI * (diameter / 2) * (diameter / 2) * height).toFloat()
        }
        3 -> { // Rectangular
            parts.getOrElse(0) { 0f } * parts.getOrElse(1) { 0f } * parts.getOrElse(2) { 0f }
        }
        else -> 0f
    }
}

fun getCylindricalBatteries(): List<CylindricalBatteryInfo> {
    return listOf(
        CylindricalBatteryInfo("D", "Mono, Goliath", "LR20", "13A", "~12-20 Ah", "1.5V", "34.2 × 61.5"),
        CylindricalBatteryInfo("C", "Baby", "LR14", "14A", "~8 Ah", "1.5V", "26.2 × 50.0"),
        CylindricalBatteryInfo("Sub-C", "", "", "", "~2.2 Ah", "1.2V", "22.2 × 42.9"),
        CylindricalBatteryInfo("AA", "Mignon", "LR6", "15A", "~2.7 Ah", "1.5V", "14.5 × 50.5"),
        CylindricalBatteryInfo("AAA", "Micro", "LR03", "24A", "~1.2 Ah", "1.5V", "10.5 × 44.5"),
        CylindricalBatteryInfo("AAAA", "Mini", "LR61", "25A", "~0.6 Ah", "1.5V", "8.3 × 42.5"),
        CylindricalBatteryInfo("A23", "MN21", "8LR932", "1811A", "~55 mAh", "12V", "10.3 × 28.5"),
        CylindricalBatteryInfo("A27", "MN27", "8LR732", "", "~22 mAh", "12V", "8.0 × 28.2"),
        CylindricalBatteryInfo("N", "Lady", "LR1", "910A", "~1 Ah", "1.5V", "12.0 × 30.2")
    ).sortedBy { getVolumeFromDimensions(it.dimensions) }
}

fun getRectangularBatteries(): List<RectangularBatteryInfo> {
    return listOf(
        RectangularBatteryInfo("9-volt", "PP3, E-Block", "6LR61", "1604A", "~0.55 Ah", "9V", "48.5 × 26.5 × 17.5"),
        RectangularBatteryInfo("PP9", "", "6F100", "1603", "~5 Ah", "9V", "51.5 × 64.5 × 80.0"),
        RectangularBatteryInfo("PP7", "", "6F90", "1605", "~2.3 Ah", "9V", "46.8 × 46.8 × 62.7"),
        RectangularBatteryInfo("4.5-volt", "Flatpack", "3LR12", "3LR12", "~6 Ah", "4.5V", "67 × 62 × 22")
    ).sortedBy { getVolumeFromDimensions(it.dimensions) }
}

fun getLithiumButtonCells(): List<ButtonCellInfo> {
    return listOf(
        ButtonCellInfo("CR1216", "5005LC", "25 mAh", "3V", "12.5 × 1.6"),
        ButtonCellInfo("CR1220", "5012LC", "40 mAh", "3V", "12.5 × 2.0"),
        ButtonCellInfo("CR1616", "5021LC", "55 mAh", "3V", "16.0 × 1.6"),
        ButtonCellInfo("CR1620", "5009LC", "78 mAh", "3V", "16.0 × 2.0"),
        ButtonCellInfo("CR2016", "5000LC", "90 mAh", "3V", "20.0 × 1.6"),
        ButtonCellInfo("CR2025", "5003LC", "160 mAh", "3V", "20.0 × 2.5"),
        ButtonCellInfo("CR2032", "5004LC", "225 mAh", "3V", "20.0 × 3.2"),
        ButtonCellInfo("CR2430", "5011LC", "290 mAh", "3V", "24.5 × 3.0"),
        ButtonCellInfo("CR2450", "5029LC", "620 mAh", "3V", "24.5 × 5.0"),
        ButtonCellInfo("CR2477", "", "1000 mAh", "3V", "24.5 × 7.7")
    ).sortedBy { getVolumeFromDimensions(it.dimensions) }
}

fun getAlkalineSilverButtonCells(): List<AlkalineSilverButtonCellInfo> {
    return listOf(
        AlkalineSilverButtonCellInfo("LR41 / SR41", "1135SO", "~32 mAh", "~45 mAh", "1.5/1.55V", "7.9 × 3.6"),
        AlkalineSilverButtonCellInfo("LR43 / SR43", "1133SO", "~80 mAh", "~120 mAh", "1.5/1.55V", "11.6 × 4.2"),
        AlkalineSilverButtonCellInfo("LR44 / SR44", "1107SO", "~110 mAh", "~175 mAh", "1.5/1.55V", "11.6 × 5.4"),
        AlkalineSilverButtonCellInfo("LR54 / SR54", "1138SO", "~80 mAh", "~100 mAh", "1.5/1.55V", "11.6 × 3.1"),
        AlkalineSilverButtonCellInfo("LR55 / SR55", "1160SO", "~35 mAh", "~55 mAh", "1.5/1.55V", "11.6 × 2.1"),
        AlkalineSilverButtonCellInfo("LR621 / SR621", "1175SO", "~13 mAh", "~20 mAh", "1.5/1.55V", "6.8 × 2.1"),
        AlkalineSilverButtonCellInfo("LR754 / SR754", "1136SO", "~50 mAh", "~65 mAh", "1.5/1.55V", "7.9 × 5.4")
    ).sortedBy { getVolumeFromDimensions(it.dimensions) }
}

fun getPhotoBatteries(): List<PhotoBatteryInfo> {
    return listOf(
        PhotoBatteryInfo("CR123A", "123", "CR17345", "5018LC", "~1500 mAh", "3V", "34.5 × 17.0"),
        PhotoBatteryInfo("CR2", "", "CR15H270", "5046LC", "~750 mAh", "3V", "27.0 × 15.6"),
        PhotoBatteryInfo("2CR5", "EL2CR5", "2CR5", "5032LC", "~1500 mAh", "6V", "45.0 × 34.0 × 17.0"),
        PhotoBatteryInfo("CR-P2", "223A", "CR-P2", "5024LC", "~1500 mAh", "6V", "36.0 × 35.0 × 19.5"),
        PhotoBatteryInfo("CR-V3", "", "", "5047LC", "~3000 mAh", "3V", "52.2 × 28.05 × 14.15")
    ).sortedBy { getVolumeFromDimensions(it.dimensions) }
}


@Composable
@Preview
fun BatteryTechScreen() {
    var mainTabIndex by remember { mutableStateOf(0) }
    val mainTabs = listOf("Piles Cylindriques", "Piles Rectangulaires", "Piles Bouton", "Piles Photo")

    Scaffold { innerPadding ->
        Column(modifier = Modifier.fillMaxSize()) {
            @OptIn(ExperimentalMaterial3Api::class)
            PrimaryTabRow(
                selectedTabIndex = mainTabIndex,
            ) {
                mainTabs.forEachIndexed { index, title ->
                    Tab(
                        text = { Text(title) },
                        selected = mainTabIndex == index,
                        onClick = { mainTabIndex = index }
                    )
                }
            }

            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    when (mainTabIndex) {
                        0 -> CylindricalBatteriesTable()
                        1 -> RectangularBatteriesTable()
                        2 -> ButtonCellsTables()
                        3 -> PhotoBatteriesTable()
                    }
                }
            }
        }
    }
}

@Composable
fun Table(headers: List<String>, rows: List<List<String>>, modifier: Modifier = Modifier) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(scrollState)
    ) {
        TableHeader(headers)

        // Séparateur après l'en-tête
        HorizontalDivider(
            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
            thickness = 1.dp
        )

        rows.forEachIndexed { index, rowData ->
            TableRow(cells = rowData)

            // Séparateur entre les lignes (sauf la dernière)
            if (index < rows.size - 1) {
                HorizontalDivider(
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f),
                    thickness = 0.5.dp
                )
            }
        }
    }
}


@Composable
fun CylindricalBatteriesTable() {
    val batteries = getCylindricalBatteries()
    val headers = listOf("Nom", "Autres Noms", "IEC", "ANSI", "Capacité", "Tension", "Dimensions (mm)")
    val rows = batteries.map {
        listOf(it.commonName, it.otherName, it.iecName, it.ansiName, it.capacity, it.voltage, it.dimensions)
    }
    Table(headers = headers, rows = rows)
}

@Composable
fun RectangularBatteriesTable() {
    val batteries = getRectangularBatteries()
    val headers = listOf("Nom", "Autres Noms", "IEC", "ANSI", "Capacité", "Tension", "Dimensions (mm)")
    val rows = batteries.map {
        listOf(it.commonName, it.otherName, it.iecName, it.ansiName, it.capacity, it.voltage, it.dimensions)
    }
    Table(headers = headers, rows = rows)
}

@Composable
fun ButtonCellsTables() {
    val lithiumCells = getLithiumButtonCells()
    val alkalineSilverCells = getAlkalineSilverButtonCells()

    val lithiumHeaders = listOf("IEC", "ANSI", "Capacité", "Tension", "Dimensions (mm)")
    val lithiumRows = lithiumCells.map {
        listOf(it.iecName, it.ansiName, it.capacity, it.voltage, it.dimensions)
    }

    val alkalineHeaders = listOf("IEC (Alc/Ag)", "ANSI", "Capacité (Alc)", "Capacité (Ag)", "Tension", "Dimensions (mm)")
    val alkalineRows = alkalineSilverCells.map {
        listOf(it.iecName, it.ansiName, it.capacityAlkaline, it.capacitySilver, it.voltage, it.dimensions)
    }

    Column {
        Text(
            "Piles bouton au lithium",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Table(headers = lithiumHeaders, rows = lithiumRows)

        Text(
            "Piles bouton Alcaline / Oxyde d'argent",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(top = 24.dp, bottom = 8.dp)
        )
        Table(headers = alkalineHeaders, rows = alkalineRows)
    }
}

@Composable
fun PhotoBatteriesTable() {
    val batteries = getPhotoBatteries()
    val headers = listOf("Nom", "Autres Noms", "IEC", "ANSI", "Capacité", "Tension", "Dimensions (mm)")
    val rows = batteries.map {
        listOf(it.commonName, it.otherName, it.iecName, it.ansiName, it.capacity, it.voltage, it.dimensions)
    }
    Table(headers = headers, rows = rows)
}
