package com.joviansapps.ganymede.ui.screens.ressources

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

/**
 * Data class for an ASCII table entry.
 */
data class AsciiEntry(
    val dec: Int,
    val oct: String,
    val hex: String,
    val bin: String,
    val symbol: String,
    val html: String,
    val escape: String,
    val description: String
)

/**
 * Main composable for displaying ASCII tables with tabs.
 */
@Composable
fun ASCIITablesScreen(modifier: Modifier = Modifier) {
    var tabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("Contrôle (0-31)", "Imprimable (32-127)", "Étendu (128-255)")

    Column(modifier = modifier.fillMaxSize()) {
        TabRow(selectedTabIndex = tabIndex) {
            tabs.forEachIndexed { i, title ->
                Tab(
                    selected = tabIndex == i,
                    onClick = { tabIndex = i },
                    text = { Text(title) }
                )
            }
        }

        // Display the selected table based on the tab index
        when (tabIndex) {
            0 -> AsciiTable(getControlEntries())
            1 -> AsciiTable(getPrintableEntries())
            2 -> AsciiTable(getExtendedEntries())
        }
    }
}

/**
 * A generic composable to display a scrollable ASCII table.
 * @param entries The list of AsciiEntry to display.
 */
@Composable
private fun AsciiTable(entries: List<AsciiEntry>) {
    val horizontalScrollState = rememberScrollState()
    Column(modifier = Modifier.horizontalScroll(horizontalScrollState)) {
        TableHeader()
        HorizontalDivider()
        Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
            entries.forEachIndexed { index, entry ->
                AsciiRow(
                    entry = entry,
                    isEven = index % 2 == 0
                )
                HorizontalDivider()
            }
        }
    }
}

/**
 * Displays the header row for the ASCII tables.
 */
@Composable
private fun TableHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(horizontal = 8.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val headerStyle = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold)
        Text("DEC", modifier = Modifier.width(55.dp), style = headerStyle)
        Text("OCT", modifier = Modifier.width(55.dp), style = headerStyle)
        Text("HEX", modifier = Modifier.width(55.dp), style = headerStyle)
        Text("BIN", modifier = Modifier.width(90.dp), style = headerStyle)
        Text("Symbol", modifier = Modifier.width(70.dp), style = headerStyle, textAlign = TextAlign.Center)
        Text("HTML Code", modifier = Modifier.width(140.dp), style = headerStyle)
        Text("Char Escape", modifier = Modifier.width(100.dp), style = headerStyle)
        Text("Description", modifier = Modifier.width(250.dp), style = headerStyle)
    }
}

/**
 * Displays a single row in an ASCII table with alternating background color.
 * @param entry The data for the row.
 * @param isEven Determines if the row should have a slightly different background color.
 */
@Composable
private fun AsciiRow(entry: AsciiEntry, isEven: Boolean) {
    val backgroundColor = if (isEven) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(backgroundColor)
            .padding(horizontal = 8.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val cellStyle = MaterialTheme.typography.bodySmall
        val monoStyle = cellStyle.copy(fontFamily = FontFamily.Monospace)
        Text(entry.dec.toString(), modifier = Modifier.width(55.dp), style = monoStyle)
        Text(entry.oct, modifier = Modifier.width(55.dp), style = monoStyle)
        Text(entry.hex, modifier = Modifier.width(55.dp), style = monoStyle)
        Text(entry.bin, modifier = Modifier.width(90.dp), style = monoStyle)
        Text(entry.symbol, modifier = Modifier.width(70.dp), style = cellStyle.copy(fontFamily = FontFamily.Default), textAlign = TextAlign.Center)
        Text(entry.html, modifier = Modifier.width(140.dp), style = monoStyle)
        Text(entry.escape, modifier = Modifier.width(100.dp), style = monoStyle)
        Text(entry.description, modifier = Modifier.width(250.dp), style = cellStyle)
    }
}

// --- Data Generation Functions ---

private fun getControlEntries(): List<AsciiEntry> {
    val descriptions = mapOf(
        0 to "Caractère Nul (Null)", 1 to "Début d'en-tête (Start of Heading)", 2 to "Début de texte (Start of Text)", 3 to "Fin de texte (End of Text)",
        4 to "Fin de transmission (End of Transmission)", 5 to "Demande (Enquiry)", 6 to "Accusé de réception (Acknowledgment)", 7 to "Sonnerie (Bell)",
        8 to "Retour arrière (Backspace)", 9 to "Tabulation horizontale (Horizontal Tab)", 10 to "Saut de ligne (Line Feed)", 11 to "Tabulation verticale (Vertical Tab)",
        12 to "Saut de page (Form Feed)", 13 to "Retour chariot (Carriage Return)", 14 to "Décode (Shift Out)", 15 to "Code (Shift In)",
        16 to "Échappement liaison de données (Data Link Escape)", 17 to "Commande de périphérique 1 (XON)", 18 to "Commande de périphérique 2",
        19 to "Commande de périphérique 3 (XOFF)", 20 to "Commande de périphérique 4", 21 to "Accusé de réception négatif (Negative Acknowledgment)",
        22 to "Repos synchrone (Synchronous Idle)", 23 to "Fin de bloc de transmission (End of Transmission Block)", 24 to "Annuler (Cancel)",
        25 to "Fin de support (End of Medium)", 26 to "Substituer (Substitute)", 27 to "Échappement (Escape)", 28 to "Séparateur de fichier (File Separator)",
        29 to "Séparateur de groupe (Group Separator)", 30 to "Séparateur d'enregistrement (Record Separator)", 31 to "Séparateur d'unité (Unit Separator)"
    )
    val symbols = mapOf(
        0 to "␀", 1 to "␁", 2 to "␂", 3 to "␃", 4 to "␄", 5 to "␅", 6 to "␆", 7 to "␇",
        8 to "␈", 9 to "␉", 10 to "␊", 11 to "␋", 12 to "␌", 13 to "␍", 14 to "␎", 15 to "␏",
        16 to "␐", 17 to "␑", 18 to "␒", 19 to "␓", 20 to "␔", 21 to "␕", 22 to "␖", 23 to "␗",
        24 to "␘", 25 to "␙", 26 to "␚", 27 to "␛", 28 to "␜", 29 to "␝", 30 to "␞", 31 to "␟"
    )
    return (0..31).map { dec ->
        AsciiEntry(
            dec = dec,
            oct = dec.toString(8).padStart(3, '0'),
            hex = dec.toString(16).uppercase().padStart(2, '0'),
            bin = dec.toString(2).padStart(8, '0'),
            symbol = symbols[dec] ?: "^${(dec + 64).toChar()}",
            html = "&#${dec};",
            escape = when (dec) { 8 -> "\\b"; 9 -> "\\t"; 10 -> "\\n"; 12 -> "\\f"; 13 -> "\\r"; else -> "" },
            description = descriptions[dec] ?: ""
        )
    }
}

private fun getPrintableEntries(): List<AsciiEntry> {
    return (32..127).map { dec ->
        val htmlNamed = when(dec) { 34 -> "&quot;"; 38 -> "&amp;"; 60 -> "&lt;"; 62 -> "&gt;"; else -> null }
        AsciiEntry(
            dec = dec,
            oct = dec.toString(8).padStart(3, '0'),
            hex = dec.toString(16).uppercase().padStart(2, '0'),
            bin = dec.toString(2).padStart(8, '0'),
            symbol = if (dec == 32) "␠" else dec.toChar().toString(),
            html = if (htmlNamed != null) "$htmlNamed / &#$dec;" else "&#$dec;",
            escape = when(dec) { 34 -> "\\\""; 39 -> "\\'"; 92 -> "\\\\"; else -> ""},
            description = when (dec) { 32 -> "Espace"; 127 -> "Supprimer"; else -> "" }
        )
    }
}

private fun getExtendedEntries(): List<AsciiEntry> {
    return (128..255).map { dec ->
        AsciiEntry(
            dec = dec,
            oct = dec.toString(8).padStart(3, '0'),
            hex = dec.toString(16).uppercase().padStart(2, '0'),
            bin = dec.toString(2).padStart(8, '0'),
            symbol = extended_symbols[dec] ?: dec.toChar().toString(),
            html = if (htmlNamedEntities[dec] != null) "${htmlNamedEntities[dec]} / &#$dec;" else "&#$dec;",
            escape = "",
            description = iso8859_1_descriptions[dec] ?: ""
        )
    }
}

private val extended_symbols = mapOf(
    128 to "€", 129 to " ", 130 to "‚", 131 to "ƒ", 132 to "„", 133 to "…", 134 to "†", 135 to "‡",
    136 to "ˆ", 137 to "‰", 138 to "Š", 139 to "‹", 140 to "Œ", 141 to " ", 142 to "Ž",
    143 to " ", 144 to " ", 145 to "‘", 146 to "’", 147 to "“", 148 to "”", 149 to "•",
    150 to "–", 151 to "—", 152 to "˜", 153 to "™", 154 to "š", 155 to "›", 156 to "œ",
    157 to " ", 158 to "ž", 159 to "Ÿ"
)

private val htmlNamedEntities = mapOf(
    160 to "&nbsp;", 161 to "&iexcl;", 162 to "&cent;", 163 to "&pound;", 164 to "&curren;",
    165 to "&yen;", 166 to "&brvbar;", 167 to "&sect;", 168 to "&uml;", 169 to "&copy;",
    170 to "&ordf;", 171 to "&laquo;", 172 to "&not;", 173 to "&shy;", 174 to "&reg;",
    175 to "&macr;", 176 to "&deg;", 177 to "&plusmn;", 178 to "&sup2;", 179 to "&sup3;",
    180 to "&acute;", 181 to "&micro;", 182 to "&para;", 183 to "&middot;", 184 to "&cedil;",
    185 to "&sup1;", 186 to "&ordm;", 187 to "&raquo;", 188 to "&frac14;", 189 to "&frac12;",
    190 to "&frac34;", 191 to "&iquest;", 192 to "&Agrave;", 193 to "&Aacute;", 194 to "&Acirc;",
    195 to "&Atilde;", 196 to "&Auml;", 197 to "&Aring;", 198 to "&AElig;", 199 to "&Ccedil;",
    200 to "&Egrave;", 201 to "&Eacute;", 202 to "&Ecirc;", 203 to "&Euml;", 204 to "&Igrave;",
    205 to "&Iacute;", 206 to "&Icirc;", 207 to "&Iuml;", 208 to "&ETH;", 209 to "&Ntilde;",
    210 to "&Ograve;", 211 to "&Oacute;", 212 to "&Ocirc;", 213 to "&Otilde;", 214 to "&Ouml;",
    215 to "&times;", 216 to "&Oslash;", 217 to "&Ugrave;", 218 to "&Uacute;", 219 to "&Ucirc;",
    220 to "&Uuml;", 221 to "&Yacute;", 222 to "&THORN;", 223 to "&szlig;", 224 to "&agrave;",
    225 to "&aacute;", 226 to "&acirc;", 227 to "&atilde;", 228 to "&auml;", 229 to "&aring;",
    230 to "&aelig;", 231 to "&ccedil;", 232 to "&egrave;", 233 to "&eacute;", 234 to "&ecirc;",
    235 to "&euml;", 236 to "&igrave;", 237 to "&iacute;", 238 to "&icirc;", 239 to "&iuml;",
    240 to "&eth;", 241 to "&ntilde;", 242 to "&ograve;", 243 to "&oacute;", 244 to "&ocirc;",
    245 to "&otilde;", 246 to "&ouml;", 247 to "&divide;", 248 to "&oslash;", 249 to "&ugrave;",
    250 to "&uacute;", 251 to "&ucirc;", 252 to "&uuml;", 253 to "&yacute;", 254 to "&thorn;",
    255 to "&yuml;"
)

private val iso8859_1_descriptions = mapOf(
    128 to "Signe Euro", 129 to "Non assigné", 130 to "Guillemet-virgule simple inférieur",
    131 to "Lettre f latine minuscule avec crochet", 132 to "Guillemet-virgule double inférieur", 133 to "Points de suspension", 134 to "Dague", 135 to "Double dague",
    136 to "Accent circonflexe", 137 to "Signe pour mille", 138 to "S majuscule avec caron", 139 to "Guillemet simple vers la gauche", 140 to "Ligature OE majuscule",
    141 to "Non assigné", 142 to "Z majuscule avec caron", 143 to "Non assigné", 144 to "Non assigné",
    145 to "Guillemet-apostrophe culbuté", 146 to "Guillemet-apostrophe", 147 to "Guillemet-apostrophe double culbuté", 148 to "Guillemet-apostrophe double",
    149 to "Puce", 150 to "Tiret demi-cadratin", 151 to "Tiret cadratin", 152 to "Tilde",
    153 to "Signe de marque commerciale", 154 to "s minuscule avec caron", 155 to "Guillemet simple vers la droite", 156 to "Ligature oe minuscule",
    157 to "Non assigné", 158 to "z minuscule avec caron", 159 to "Y majuscule avec tréma",
    160 to "Espace insécable", 161 to "Point d'exclamation inversé", 162 to "Signe centime", 163 to "Signe livre",
    164 to "Signe monétaire", 165 to "Signe yen", 166 to "Barre verticale brisée", 167 to "Signe de section",
    168 to "Tréma", 169 to "Signe de copyright", 170 to "Indicateur ordinal féminin", 171 to "Guillemet français ouvrant",
    172 to "Signe non", 173 to "Trait d'union conditionnel", 174 to "Signe de marque déposée", 175 to "Macron",
    176 to "Signe de degré", 177 to "Signe plus ou moins", 178 to "Exposant deux", 179 to "Exposant trois",
    180 to "Accent aigu", 181 to "Signe micro", 182 to "Signe de paragraphe", 183 to "Point médian",
    184 to "Cédille", 185 to "Exposant un", 186 to "Indicateur ordinal masculin",
    187 to "Guillemet français fermant", 188 to "Fraction un quart",
    189 to "Fraction un demi", 190 to "Fraction trois quarts", 191 to "Point d'interrogation inversé",
    192 to "A majuscule avec accent grave", 193 to "A majuscule avec accent aigu",
    194 to "A majuscule avec accent circonflexe", 195 to "A majuscule avec tilde",
    196 to "A majuscule avec tréma", 197 to "A majuscule avec rond en chef",
    198 to "AE majuscule", 199 to "C majuscule avec cédille",
    200 to "E majuscule avec accent grave", 201 to "E majuscule avec accent aigu",
    202 to "E majuscule avec accent circonflexe", 203 to "E majuscule avec tréma",
    204 to "I majuscule avec accent grave", 205 to "I majuscule avec accent aigu",
    206 to "I majuscule avec accent circonflexe", 207 to "I majuscule avec tréma",
    208 to "Eth majuscule", 209 to "N majuscule avec tilde",
    210 to "O majuscule avec accent grave", 211 to "O majuscule avec accent aigu",
    212 to "O majuscule avec accent circonflexe", 213 to "O majuscule avec tilde",
    214 to "O majuscule avec tréma", 215 to "Signe de multiplication",
    216 to "O majuscule barré", 217 to "U majuscule avec accent grave",
    218 to "U majuscule avec accent aigu", 219 to "U majuscule avec accent circonflexe",
    220 to "U majuscule avec tréma", 221 to "Y majuscule avec accent aigu",
    222 to "Thorn majuscule", 223 to "S minuscule allemand (eszett)",
    224 to "a minuscule avec accent grave", 225 to "a minuscule avec accent aigu",
    226 to "a minuscule avec accent circonflexe", 227 to "a minuscule avec tilde",
    228 to "a minuscule avec tréma", 229 to "a minuscule avec rond en chef",
    230 to "ae minuscule", 231 to "c minuscule avec cédille",
    232 to "e minuscule avec accent grave", 233 to "e minuscule avec accent aigu",
    234 to "e minuscule avec accent circonflexe", 235 to "e minuscule avec tréma",
    236 to "i minuscule avec accent grave", 237 to "i minuscule avec accent aigu",
    238 to "i minuscule avec accent circonflexe", 239 to "i minuscule avec tréma",
    240 to "eth minuscule", 241 to "n minuscule avec tilde",
    242 to "o minuscule avec accent grave", 243 to "o minuscule avec accent aigu",
    244 to "o minuscule avec accent circonflexe", 245 to "o minuscule avec tilde",
    246 to "o minuscule avec tréma", 247 to "Signe de division",
    248 to "o minuscule barré", 249 to "u minuscule avec accent grave",
    250 to "u minuscule avec accent aigu", 251 to "u minuscule avec accent circonflexe",
    252 to "u minuscule avec tréma", 253 to "y minuscule avec accent aigu",
    254 to "thorn minuscule", 255 to "y minuscule avec tréma"
)


@Preview(showBackground = true)
@Composable
private fun ASCIITablesPreview() {
    ASCIITablesScreen()
}

