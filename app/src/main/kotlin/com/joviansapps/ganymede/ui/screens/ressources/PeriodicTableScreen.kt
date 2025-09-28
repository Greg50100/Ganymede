package com.joviansapps.ganymede.ui.screens.ressources

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private data class Element(
    val number: Int,
    val symbol: String,
    val name: String,
    val mass: Double,
    val category: String,
    val group: Int, // column 1-18
    val period: Int // row 1-9 (including placeholders for lanthanides/actinides)
)

@Composable
fun PeriodicTableScreen() {
    val elements = getPeriodicElements()
    var selectedElement by remember { mutableStateOf<Element?>(null) }
    val categories = elements.map { it.category }.distinct().sorted()
    val categoryColors = getCategoryColors()

    Column(modifier = Modifier.fillMaxSize().padding(4.dp)) {
        // Scrollable container for the periodic table
        Box(modifier = Modifier
            .weight(1f)
            .verticalScroll(rememberScrollState())
            .horizontalScroll(rememberScrollState())
        ) {
            val cellWidth = 60.dp
            val cellHeight = 60.dp
            val tableWidth = cellWidth * 18
            val tableHeight = cellHeight * 10

            Box(modifier = Modifier.size(width = tableWidth, height = tableHeight)) {
                elements.forEach { element ->
                    ElementCard(
                        element = element,
                        color = categoryColors[element.category] ?: Color.LightGray,
                        modifier = Modifier
                            .width(cellWidth)
                            .height(cellHeight)
                            .offset(
                                x = cellWidth * (element.group - 1),
                                y = cellHeight * (element.period - 1)
                            )
                            .clickable { selectedElement = element }
                    )
                }
            }
        }

        CategoryLegend(categories, categoryColors)

        selectedElement?.let {
            ElementDetailDialog(element = it, onDismiss = { selectedElement = null })
        }
    }
}

@Composable
private fun ElementCard(element: Element, color: Color, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .padding(1.dp)
            .background(color.copy(alpha = 0.6f))
            .border(0.5.dp, Color.Black.copy(alpha = 0.3f)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(2.dp)
        ) {
            Text(element.number.toString(), fontSize = 8.sp, textAlign = TextAlign.Center, color = Color.Black)
            Text(element.symbol, fontWeight = FontWeight.Bold, fontSize = 12.sp, textAlign = TextAlign.Center, color = Color.Black)
            Text(element.name, fontSize = 6.sp, textAlign = TextAlign.Center, maxLines = 1, color = Color.Black.copy(alpha = 0.8f))
        }
    }
}

@Composable
private fun CategoryLegend(categories: List<String>, colors: Map<String, Color>) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 140.dp),
        contentPadding = PaddingValues(vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier.heightIn(max = 100.dp).padding(horizontal = 4.dp)
    ) {
        items(categories) { category ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(12.dp).background(colors[category] ?: Color.Transparent).border(0.5.dp, Color.Black))
                Spacer(Modifier.width(4.dp))
                Text(category, fontSize = 10.sp)
            }
        }
    }
}

@Composable
private fun ElementDetailDialog(element: Element, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("${element.name} (${element.symbol})") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Numéro atomique: ${element.number}")
                Text("Masse atomique: ${element.mass} u")
                Text("Catégorie: ${element.category}")
                Text("Période: ${element.period}, Groupe: ${element.group}")
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Fermer")
            }
        }
    )
}

// Data
private fun getCategoryColors(): Map<String, Color> {
    return mapOf(
        "Gaz noble" to Color(0xFFC0A0FF),
        "Métal alcalin" to Color(0xFFFFA07A),
        "Métal alcalino-terreux" to Color(0xFFFFFFA0),
        "Métalloïde" to Color(0xFFB3E5FC),
        "Halogène" to Color(0xFFC8E6C9),
        "Autre non-métal" to Color(0xFFE6EE9C),
        "Métal de transition" to Color(0xFFFFCCBC),
        "Métal pauvre" to Color(0xFFCFD8DC),
        "Lanthanide" to Color(0xFFFFE0B2),
        "Actinide" to Color(0xFFFFD180)
    )
}

// [Image d'un Tableau périodique des éléments]
private fun getPeriodicElements(): List<Element> {
    return listOf(
        Element(1, "H", "Hydrogène", 1.008, "Autre non-métal", 1, 1),
        Element(2, "He", "Hélium", 4.0026, "Gaz noble", 18, 1),
        Element(3, "Li", "Lithium", 6.94, "Métal alcalin", 1, 2),
        Element(4, "Be", "Béryllium", 9.0122, "Métal alcalino-terreux", 2, 2),
        Element(5, "B", "Bore", 10.81, "Métalloïde", 13, 2),
        Element(6, "C", "Carbone", 12.011, "Autre non-métal", 14, 2),
        Element(7, "N", "Azote", 14.007, "Autre non-métal", 15, 2),
        Element(8, "O", "Oxygène", 15.999, "Autre non-métal", 16, 2),
        Element(9, "F", "Fluor", 18.998, "Halogène", 17, 2),
        Element(10, "Ne", "Néon", 20.180, "Gaz noble", 18, 2),
        Element(11, "Na", "Sodium", 22.990, "Métal alcalin", 1, 3),
        Element(12, "Mg", "Magnésium", 24.305, "Métal alcalino-terreux", 2, 3),
        Element(13, "Al", "Aluminium", 26.982, "Métal pauvre", 13, 3),
        Element(14, "Si", "Silicium", 28.085, "Métalloïde", 14, 3),
        Element(15, "P", "Phosphore", 30.974, "Autre non-métal", 15, 3),
        Element(16, "S", "Soufre", 32.06, "Autre non-métal", 16, 3),
        Element(17, "Cl", "Chlore", 35.45, "Halogène", 17, 3),
        Element(18, "Ar", "Argon", 39.948, "Gaz noble", 18, 3),
        Element(19, "K", "Potassium", 39.098, "Métal alcalin", 1, 4),
        Element(20, "Ca", "Calcium", 40.078, "Métal alcalino-terreux", 2, 4),
        Element(21, "Sc", "Scandium", 44.956, "Métal de transition", 3, 4),
        Element(22, "Ti", "Titane", 47.867, "Métal de transition", 4, 4),
        Element(23, "V", "Vanadium", 50.942, "Métal de transition", 5, 4),
        Element(24, "Cr", "Chrome", 51.996, "Métal de transition", 6, 4),
        Element(25, "Mn", "Manganèse", 54.938, "Métal de transition", 7, 4),
        Element(26, "Fe", "Fer", 55.845, "Métal de transition", 8, 4),
        Element(27, "Co", "Cobalt", 58.933, "Métal de transition", 9, 4),
        Element(28, "Ni", "Nickel", 58.693, "Métal de transition", 10, 4),
        Element(29, "Cu", "Cuivre", 63.546, "Métal de transition", 11, 4),
        Element(30, "Zn", "Zinc", 65.38, "Métal de transition", 12, 4),
        Element(31, "Ga", "Gallium", 69.723, "Métal pauvre", 13, 4),
        Element(32, "Ge", "Germanium", 72.63, "Métalloïde", 14, 4),
        Element(33, "As", "Arsenic", 74.922, "Métalloïde", 15, 4),
        Element(34, "Se", "Sélénium", 78.971, "Autre non-métal", 16, 4),
        Element(35, "Br", "Brome", 79.904, "Halogène", 17, 4),
        Element(36, "Kr", "Krypton", 83.798, "Gaz noble", 18, 4),
        Element(37, "Rb", "Rubidium", 85.468, "Métal alcalin", 1, 5),
        Element(38, "Sr", "Strontium", 87.62, "Métal alcalino-terreux", 2, 5),
        Element(39, "Y", "Yttrium", 88.906, "Métal de transition", 3, 5),
        Element(40, "Zr", "Zirconium", 91.224, "Métal de transition", 4, 5),
        Element(41, "Nb", "Niobium", 92.906, "Métal de transition", 5, 5),
        Element(42, "Mo", "Molybdène", 95.96, "Métal de transition", 6, 5),
        Element(43, "Tc", "Technétium", 98.0, "Métal de transition", 7, 5),
        Element(44, "Ru", "Ruthénium", 101.07, "Métal de transition", 8, 5),
        Element(45, "Rh", "Rhodium", 102.91, "Métal de transition", 9, 5),
        Element(46, "Pd", "Palladium", 106.42, "Métal de transition", 10, 5),
        Element(47, "Ag", "Argent", 107.87, "Métal de transition", 11, 5),
        Element(48, "Cd", "Cadmium", 112.41, "Métal de transition", 12, 5),
        Element(49, "In", "Indium", 114.82, "Métal pauvre", 13, 5),
        Element(50, "Sn", "Étain", 118.71, "Métal pauvre", 14, 5),
        Element(51, "Sb", "Antimoine", 121.76, "Métalloïde", 15, 5),
        Element(52, "Te", "Tellure", 127.6, "Métalloïde", 16, 5),
        Element(53, "I", "Iode", 126.9, "Halogène", 17, 5),
        Element(54, "Xe", "Xénon", 131.29, "Gaz noble", 18, 5),
        Element(55, "Cs", "Césium", 132.91, "Métal alcalin", 1, 6),
        Element(56, "Ba", "Baryum", 137.33, "Métal alcalino-terreux", 2, 6),
        Element(57, "La", "Lanthane", 138.91, "Lanthanide", 3, 8),
        Element(58, "Ce", "Cérium", 140.12, "Lanthanide", 4, 8),
        Element(59, "Pr", "Praséodyme", 140.91, "Lanthanide", 5, 8),
        Element(60, "Nd", "Néodyme", 144.24, "Lanthanide", 6, 8),
        Element(61, "Pm", "Prométhium", 145.0, "Lanthanide", 7, 8),
        Element(62, "Sm", "Samarium", 150.36, "Lanthanide", 8, 8),
        Element(63, "Eu", "Europium", 151.96, "Lanthanide", 9, 8),
        Element(64, "Gd", "Gadolinium", 157.25, "Lanthanide", 10, 8),
        Element(65, "Tb", "Terbium", 158.93, "Lanthanide", 11, 8),
        Element(66, "Dy", "Dysprosium", 162.5, "Lanthanide", 12, 8),
        Element(67, "Ho", "Holmium", 164.93, "Lanthanide", 13, 8),
        Element(68, "Er", "Erbium", 167.26, "Lanthanide", 14, 8),
        Element(69, "Tm", "Thulium", 168.93, "Lanthanide", 15, 8),
        Element(70, "Yb", "Ytterbium", 173.05, "Lanthanide", 16, 8),
        Element(71, "Lu", "Lutécium", 174.97, "Lanthanide", 17, 8),
        Element(72, "Hf", "Hafnium", 178.49, "Métal de transition", 4, 6),
        Element(73, "Ta", "Tantale", 180.95, "Métal de transition", 5, 6),
        Element(74, "W", "Tungstène", 183.84, "Métal de transition", 6, 6),
        Element(75, "Re", "Rhénium", 186.21, "Métal de transition", 7, 6),
        Element(76, "Os", "Osmium", 190.23, "Métal de transition", 8, 6),
        Element(77, "Ir", "Iridium", 192.22, "Métal de transition", 9, 6),
        Element(78, "Pt", "Platine", 195.08, "Métal de transition", 10, 6),
        Element(79, "Au", "Or", 196.97, "Métal de transition", 11, 6),
        Element(80, "Hg", "Mercure", 200.59, "Métal de transition", 12, 6),
        Element(81, "Tl", "Thallium", 204.38, "Métal pauvre", 13, 6),
        Element(82, "Pb", "Plomb", 207.2, "Métal pauvre", 14, 6),
        Element(83, "Bi", "Bismuth", 208.98, "Métal pauvre", 15, 6),
        Element(84, "Po", "Polonium", 209.0, "Métalloïde", 16, 6),
        Element(85, "At", "Astate", 210.0, "Halogène", 17, 6),
        Element(86, "Rn", "Radon", 222.0, "Gaz noble", 18, 6),
        Element(87, "Fr", "Francium", 223.0, "Métal alcalin", 1, 7),
        Element(88, "Ra", "Radium", 226.0, "Métal alcalino-terreux", 2, 7),
        Element(89, "Ac", "Actinium", 227.0, "Actinide", 3, 9),
        Element(90, "Th", "Thorium", 232.04, "Actinide", 4, 9),
        Element(91, "Pa", "Protactinium", 231.04, "Actinide", 5, 9),
        Element(92, "U", "Uranium", 238.03, "Actinide", 6, 9),
        Element(93, "Np", "Neptunium", 237.0, "Actinide", 7, 9),
        Element(94, "Pu", "Plutonium", 244.0, "Actinide", 8, 9),
        Element(95, "Am", "Américium", 243.0, "Actinide", 9, 9),
        Element(96, "Cm", "Curium", 247.0, "Actinide", 10, 9),
        Element(97, "Bk", "Berkélium", 247.0, "Actinide", 11, 9),
        Element(98, "Cf", "Californium", 251.0, "Actinide", 12, 9),
        Element(99, "Es", "Einsteinium", 252.0, "Actinide", 13, 9),
        Element(100, "Fm", "Fermium", 257.0, "Actinide", 14, 9),
        Element(101, "Md", "Mendélévium", 258.0, "Actinide", 15, 9),
        Element(102, "No", "Nobélium", 259.0, "Actinide", 16, 9),
        Element(103, "Lr", "Lawrencium", 262.0, "Actinide", 17, 9),
        Element(104, "Rf", "Rutherfordium", 267.0, "Métal de transition", 4, 7),
        Element(105, "Db", "Dubnium", 270.0, "Métal de transition", 5, 7),
        Element(106, "Sg", "Seaborgium", 271.0, "Métal de transition", 6, 7),
        Element(107, "Bh", "Bohrium", 270.0, "Métal de transition", 7, 7),
        Element(108, "Hs", "Hassium", 277.0, "Métal de transition", 8, 7),
        Element(109, "Mt", "Meitnerium", 276.0, "Métal de transition", 9, 7),
        Element(110, "Ds", "Darmstadtium", 281.0, "Métal de transition", 10, 7),
        Element(111, "Rg", "Roentgenium", 280.0, "Métal de transition", 11, 7),
        Element(112, "Cn", "Copernicium", 285.0, "Métal de transition", 12, 7),
        Element(113, "Nh", "Nihonium", 284.0, "Métal pauvre", 13, 7),
        Element(114, "Fl", "Flerovium", 289.0, "Métal pauvre", 14, 7),
        Element(115, "Mc", "Moscovium", 288.0, "Métal pauvre", 15, 7),
        Element(116, "Lv", "Livermorium", 293.0, "Métal pauvre", 16, 7),
        Element(117, "Ts", "Tennessine", 294.0, "Halogène", 17, 7),
        Element(118, "Og", "Oganesson", 294.0, "Gaz noble", 18, 7)
    )
}

